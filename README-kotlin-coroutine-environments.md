# 🖥️ Kotlin Coroutine 환경 QueryCountPerRequest 사용 가이드

**한국어** | [English](README-kotlin-coroutine-environments-EN.md)

## 🖥️ 개요

Multi-Datasource-Query-Counter 라이브러리는 API 요청별 DB 쿼리 수를 카운팅합니다.
라이브러리는 기본적으로 `@RequestScope` 를 활용하여, 각 API 요청마다 카운터를 갖도록 하고 있습니다.
그러나 Kotlin Coroutine 환경에서는 Coroutine 이 스레드를 자유롭게 이동(`suspension`, `resumption`)할 수 있기 때문에,
`RequestContextHolder`의 컨텍스트가 유지되지 않는 문제가 발생할 수 있습니다.

이 가이드는 Coroutine 환경에서 `QueryCountPerRequest` 를 올바르게 사용하는 방법을 설명합니다.

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

`CoroutineQueryCountContextElement` 는 [`ThreadContextElement` 인터페이스](https://kotlinlang.org/api/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines/-thread-context-element/)를 구현하여 분화되는 Coroutine 컨텍스트에 `RequestAttributes` 를 전파합니다.

```kotlin
class CoroutineQueryCountContextElement(
    private val requestAttributes: RequestAttributes = RequestContextHolder.currentRequestAttributes(),
) : ThreadContextElement<RequestAttributes> {

    companion object Key : CoroutineContext.Key<CoroutineQueryCountContextElement>

    override val key: CoroutineContext.Key<CoroutineQueryCountContextElement>
        get() = Key

    override fun updateThreadContext(context: CoroutineContext): RequestAttributes {
        RequestContextHolder.setRequestAttributes(requestAttributes)
        return requestAttributes
    }

    override fun restoreThreadContext(context: CoroutineContext, oldState: RequestAttributes) {
        RequestContextHolder.setRequestAttributes(oldState)
    }
}
```

실제 구현체는 라이브러리에 포함되어 있으므로, 곧바로 사용할 수 있습니다.

### 2. 사용 예제

```kotlin
@RestController
class UserController(
    private val userRepository: UserRepository,
) {
    @CountQueries
    @GetMapping("/users/coroutine")
    fun getUsers() {
        // 이제 이 Coroutine 과 분화되는 Coroutine 들은 서로 같은 RequestAttributes 를 유지함
        val element = CoroutineQueryCountContextElement()
        val threadPool = ForkJoinPool(2)
        return runBlocking(threadPool.asCoroutineDispatcher() + element) {
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
