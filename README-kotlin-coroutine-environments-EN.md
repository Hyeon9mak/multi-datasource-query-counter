# 🖥️ Kotlin Coroutine Environments QueryCountPerRequest Usage Guide

[한국어](README-kotlin-coroutine-environments.md) | **English**

## 🖥️ Overview

The Multi-Datasource-Query-Counter library counts the number of DB queries per API request.
The library basically uses `@RequestScope` to ensure that each API request has its own counter.
However, in Kotlin Coroutine environments, since Coroutines can freely move between threads (`suspension`, `resumption`),
an issue may occur where the context of `RequestContextHolder` is not maintained.

This guide explains how to properly use `QueryCountPerRequest` in Coroutine environments.

<br>

## 🖥️ The Problem

Coroutines have the following characteristics:

1. **Suspension, Resumption**: Coroutines can be suspended and resumed at any time.
2. **Thread Switching**: When a Coroutine resumes, it can resume on a different thread than the original one.
3. **Context Propagation**: When starting a new Coroutine with builders like `async` or `launch`, the parent Coroutine's context may not be automatically propagated.

Therefore, it is difficult to properly use `QueryCountPerRequest` by default.

<br>

## 🖥️ Solution: Coroutine Context Element

### 1. Utilizing CoroutineQueryCountContextElement

`CoroutineQueryCountContextElement` implements the [`ThreadContextElement` interface](https://kotlinlang.org/api/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines/-thread-context-element/) as a propagating context element.
It propagates the `QueryCountPerRequest` state through the branching Coroutine contexts.

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

### 2. Obtaining QueryCountPerRequest from CoroutineQueryCountContextElement

Acquire the `QueryCountPerRequest` by calling `CoroutineQueryCountContextElement` through the `QueryCountPerRequestHolder`.

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

### 3. Usage Example

Since it naturally acquires the `QueryCountPerRequest` in its own Coroutine context, no additional code modification is required.

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
                userRepository.findAllUsers() // Query count point.
            }

            val countDeferred = async {
                userRepository.count()        // Query count point.
            }
            
            val users = usersDeferred.await()
            val count = countDeferred.await()

            mapOf("users" to users, "count" to count)
        }
    }
}
```

<br>

## 🖥️ WebFlux/Reactor Environment

(WIP)
