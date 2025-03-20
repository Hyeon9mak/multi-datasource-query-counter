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

## 🖥️ 해결 방법

### 방법 1: Spring WebFlux + Kotlin Coroutine 통합

Spring WebFlux 환경에서는 Coroutine 컨텍스트 요소로 `QueryCountPerRequest` 를 전파할 수 있습니다.

#### 1-1. RequestAttributesCoroutineContextElement 구현

```kotlin
import kotlinx.coroutines.ThreadContextElement
import org.springframework.web.context.request.RequestAttributes
import org.springframework.web.context.request.RequestContextHolder
import kotlin.coroutines.CoroutineContext

class RequestAttributesCoroutineContextElement(
    private val requestAttributes: RequestAttributes
) : ThreadContextElement<RequestAttributes?> {
    // 고유 키 생성
    companion object Key : CoroutineContext.Key<RequestAttributesCoroutineContextElement>
    
    // 이 요소의 고유 키 반환
    override val key: CoroutineContext.Key<RequestAttributesCoroutineContextElement>
        get() = Key
    
    // Coroutine이 특정 스레드에서 실행되기 전에 호출됨
    override fun updateThreadContext(context: CoroutineContext): RequestAttributes? {
        val oldState = RequestContextHolder.getRequestAttributes()
        RequestContextHolder.setRequestAttributes(requestAttributes)
        return oldState
    }
    
    // Coroutine이 특정 스레드에서 실행을 마치고 나갈 때 호출됨
    override fun restoreThreadContext(context: CoroutineContext, oldState: RequestAttributes?) {
        if (oldState == null) {
            RequestContextHolder.resetRequestAttributes()
        } else {
            RequestContextHolder.setRequestAttributes(oldState)
        }
    }
}
```

#### 1-2. Coroutine 컨텍스트 관리 확장 함수

```kotlin
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.withContext
import org.springframework.web.context.request.RequestContextHolder
import kotlin.coroutines.CoroutineContext

/**
 * 현재 RequestAttributes를 Coroutine에 전파하는 확장 함수
 */
suspend fun <T> withRequestAttributes(block: suspend CoroutineScope.() -> T): T {
    val requestAttributes = RequestContextHolder.getRequestAttributes()
    return if (requestAttributes != null) {
        withContext(RequestAttributesCoroutineContextElement(requestAttributes)) {
            block()
        }
    } else {
        // RequestAttributes가 없는 경우 그냥 실행
        block()
    }
}
```

#### 1-3. 사용 예제

```kotlin
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class UserController(private val userRepository: UserRepository) {
    
    @GetMapping("/users/coroutine")
    suspend fun getUsers() = withRequestAttributes {
        coroutineScope {
            // 이제 이 Coroutine과 그 자식들은 RequestAttributes를 유지함
            val usersDeferred = async {
                userRepository.findAllUsers() // 쿼리 카운트 지점.
            }
            
            val countDeferred = async {
                userRepository.count()        // 쿼리 카운트 지점.
            }
            
            // 결과 결합
            val users = usersDeferred.await()
            val count = countDeferred.await()
            
            mapOf("users" to users, "count" to count)
        }
    }
}
```

### 방법 2: Coroutine 컨텍스트 기반 접근

Coroutine 컨텍스트를 활용하여 QueryCountPerRequest 관리를 더 Kotlin 친화적으로 구현할 수 있습니다.

#### 2-1. QueryCountCoroutineContext 구현

```kotlin
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.ThreadContextElement
import kotlin.coroutines.CoroutineContext

class QueryCountElement(
    val queryCount: QueryCountPerRequest
) : CoroutineContext.Element {
    companion object Key : CoroutineContext.Key<QueryCountElement>
    
    override val key: CoroutineContext.Key<QueryCountElement>
        get() = Key
}

// CoroutineContext에서 QueryCountPerRequest 가져오기
val CoroutineContext.queryCount: QueryCountPerRequest?
    get() = get(QueryCountElement.Key)?.queryCount
```

#### 2-2. Coroutine 스코프 확장

```kotlin
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.withContext
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes
import kotlin.coroutines.CoroutineContext

/**
 * 현재 요청의 QueryCountPerRequest와 함께 Coroutine 실행
 */
suspend fun <T> withQueryCount(
    queryCountPerRequest: QueryCountPerRequest,
    block: suspend CoroutineScope.() -> T
): T {
    return withContext(QueryCountElement(queryCountPerRequest)) {
        block()
    }
}

/**
 * 현재 요청에서 QueryCountPerRequest를 가져와 Coroutine 실행
 */
suspend fun <T> withCurrentRequestQueryCount(block: suspend CoroutineScope.() -> T): T {
    val attributes = RequestContextHolder.getRequestAttributes() as? ServletRequestAttributes
    return if (attributes != null) {
        // Spring 컨텍스트에서 QueryCountPerRequest 빈 가져오기
        val queryCountPerRequest = getQueryCountPerRequest()
        withContext(QueryCountElement(queryCountPerRequest)) {
            block()
        }
    } else {
        // 요청 컨텍스트가 없는 경우 그냥 실행
        block()
    }
}

// Spring 애플리케이션 컨텍스트에서 QueryCountPerRequest 가져오기
// (구현은 Spring 환경에 따라 달라질 수 있음)
private fun getQueryCountPerRequest(): QueryCountPerRequest {
    // Spring 애플리케이션 컨텍스트에서 빈 가져오기
    // 예시: ApplicationContextProvider.getBean(QueryCountPerRequest::class.java)
    // ...
}
```

#### 2-3. 서비스 레이어에서 활용

```kotlin
@Service
class UserService(private val userRepository: UserRepository) {
    
    suspend fun findAllUsersWithStats() = withCurrentRequestQueryCount {
        val users = userRepository.findAllSuspend() // 쿼리 카운트 지점.
        
        // 현재 Coroutine 컨텍스트에서 QueryCountPerRequest 접근
        val queryCount = coroutineContext.queryCount
        
        // 결과와 함께 현재 쿼리 통계 반환
        mapOf(
            "users" to users,
            "queryStats" to queryCount?.let {
                mapOf(
                    "count" to it.totalQueryCount,
                    "time" to it.totalQueryMilliSeconds
                )
            }
        )
    }
}
```

### 방법 3: Spring WebMVC + Kotlin Coroutine 사용 시 (Dispatchers 변경)

Spring WebMVC에서 Coroutine을 사용하는 경우, Dispatchers.IO 대신 RequestAttributes를 유지하는 커스텀 Dispatcher를 사용할 수 있습니다.

```kotlin
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asCoroutineDispatcher
import org.springframework.web.context.request.RequestAttributes
import org.springframework.web.context.request.RequestContextHolder
import java.util.concurrent.Executors

// RequestAttributes를 유지하는 커스텀 Dispatcher
object RequestAttributesDispatcher {
    private val executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()) { r ->
        Thread(r).apply { isDaemon = true }
    }
    
    val dispatcher: CoroutineDispatcher = executor.asCoroutineDispatcher()
    
    // RequestAttributes를 유지하는 래핑 함수
    suspend fun <T> preserveRequestAttributes(block: suspend () -> T): T {
        val attributes = RequestContextHolder.getRequestAttributes()
        return if (attributes != null) {
            withContext(dispatcher) {
                RequestContextHolder.setRequestAttributes(attributes)
                try {
                    block()
                } finally {
                    RequestContextHolder.resetRequestAttributes()
                }
            }
        } else {
            block()
        }
    }
}

// 사용 예
@RestController
class UserController(
    private val userService: UserService
) {
    
    @GetMapping("/users")
    suspend fun getUsers(): List<User> = RequestAttributesDispatcher.preserveRequestAttributes {
        userService.findAllUsers() // 이 과정에서 실행되는 쿼리는 카운팅됨
    }
}
```

## WebFlux + Coroutine + Reactive Repository 조합

Spring WebFlux, Kotlin Coroutine, R2DBC와 같은 리액티브 리포지토리를 함께 사용하는 경우:

```kotlin
@Configuration
class CoroutineConfig {
    
    @Bean
    fun requestAttributesContextFilter(): WebFilter {
        return WebFilter { exchange, chain ->
            val serverRequest = ServerRequest.create(exchange, emptyList())
            val context = Context.of("request", serverRequest)
            
            return@WebFilter chain.filter(exchange)
                .contextWrite(context)
        }
    }
}

@Repository
class UserReactiveRepository(
    private val databaseClient: DatabaseClient
) {
    
    suspend fun findAll(): List<User> = withContext(Dispatchers.IO) {
        // R2DBC 쿼리 실행
        databaseClient.select()
            .from(User::class.java)
            .fetch()
            .all()
            .collectList()
            .awaitSingle()
    }
}

@RestController
class UserController(
    private val userRepository: UserReactiveRepository,
) {
    
    @GetMapping("/users")
    suspend fun getUsers() = withRequestAttributes {
        userRepository.findAll() // 이 쿼리는 카운팅됨
    }
}
```
