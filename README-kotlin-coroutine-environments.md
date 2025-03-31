# 🖥️ Kotlin Coroutine 환경 QueryCountPerRequest 동작 원리 가이드

**한국어** | [English](README-kotlin-coroutine-environments-EN.md)

## 🖥️ 개요

Multi-Datasource-Query-Counter 라이브러리는 API 요청별 DB 쿼리 수를 카운팅합니다.
라이브러리는 기본적으로 `@RequestScope` 를 활용하여, 각 API 요청마다 카운터를 갖도록 하고 있습니다.
그러나 Kotlin Coroutine 환경에서는 Coroutine 이 스레드를 자유롭게 이동(`suspension`, `resumption`)할 수 있기 때문에,
`RequestContextHolder`의 컨텍스트가 유지되지 않는 문제가 발생할 수 있습니다.

이 가이드는 Coroutine 환경에서 `QueryCountPerRequest` 를 올바르게 측정할 수 있는 원리를 설명합니다.

<br>

## 🖥️ 문제점

Coroutine 은 다음과 같은 특성을 갖습니다.

1. **Suspension, Resumption**: Coroutine 은 언제든지 중단, 재개될 수 있습니다.
2. **스레드 전환**: Coroutine 이 다시 재개될 때, 기존 스레드와 다른 스레드에서 재개될 수 있습니다.
3. **컨텍스트 전파**: `async`, `launch` 등의 빌더로 새 Coroutine 을 시작하면 부모 Coroutine 의 컨텍스트가 자동으로 전파되지 않을 수 있습니다.

때문에 기본적으로 `QueryCountPerRequest` 를 올바르게 사용하기 어렵습니다.

<br>

## 🖥️ 해결 방법: Coroutine Context Element

### 1. CoroutineQueryCountContextElement 활용

`CoroutineQueryCountContextElement` 는 [`ThreadContextElement` 인터페이스](https://kotlinlang.org/api/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines/-thread-context-element/)를 구현하여 전파되는 컨텍스트 요소입니다.
분화되는 Coroutine 컨텍스트에 `QueryCountPerRequest` 지닌 상태로 전파됩니다.

```kotlin
class CoroutineQueryCountContextElement(
    var queryCountPerRequest: QueryCountPerRequest? = null,
) : ThreadContextElement<QueryCountPerRequest?> {

    companion object Key : CoroutineContext.Key<CoroutineQueryCountContextElement>

    override val key: CoroutineContext.Key<CoroutineQueryCountContextElement>
        get() = Key

    override fun updateThreadContext(context: CoroutineContext): QueryCountPerRequest? {
        return queryCountPerRequest
    }

    override fun restoreThreadContext(context: CoroutineContext, oldState: QueryCountPerRequest?) {
        queryCountPerRequest = oldState
    }
}
```

### 2. CoroutineQueryCountContextElement 로 부터 QueryCountPerRequest 획득

`QueryCountPerRequestHolder` 를 통해 `CoroutineQueryCountContextElement` 를 호출하여 `QueryCountPerRequest` 를 획득합니다. 

```kotlin
object QueryCountPerRequestHolder {

    private val queryCountPerRequestHolder = ThreadLocal<QueryCountPerRequest?>()
    private val coroutineContextElement = CoroutineQueryCountContextElement()

    fun set(queryCountPerRequest: QueryCountPerRequest) {
        queryCountPerRequestHolder.set(queryCountPerRequest)
        coroutineContextElement.queryCountPerRequest = queryCountPerRequest
    }

    fun get(): QueryCountPerRequest? = queryCountPerRequestHolder.get()
        ?: coroutineContextElement.queryCountPerRequest

    fun remove() {
        queryCountPerRequestHolder.remove()
        coroutineContextElement.queryCountPerRequest = null
    }
}

```

### 3. 사용 예제

자신의 Coroutine 컨텍스트에서 `QueryCountPerRequest` 를 자연스럽게 획득하므로, 별도의 코드 수정이 필요 없습니다.

```kotlin
@RestController
class UserController(
    private val userRepository: UserRepository,
) {
    @CountQueries
    @GetMapping("/users/coroutine")
    fun getUsers() {
        return runBlocking {
            val usersDeferred = async {
                userRepository.findAllUsers() // 쿼리 카운트 지점.
            }

            val countDeferred = async {
                userRepository.count()        // 쿼리 카운트 지점.
            }
            
            val users = usersDeferred.await()
            val count = countDeferred.await()

            mapOf("users" to users, "count" to count)
        }
    }
}
```

<br>

## 🖥️ WebFlux/Reactor 환경

(WIP)
