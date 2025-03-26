# 🖥️ Kotlin Coroutine Environments QueryCountPerRequest Usage Guide

**English** | [한국어](README-kotlin-coroutine-environments.md)

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

### 1. Using CoroutineQueryCountContextElement

`CoroutineQueryCountContextElement` implements the [`ThreadContextElement` interface](https://kotlinlang.org/api/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines/-thread-context-element/) to propagate `RequestAttributes` to the branching Coroutine contexts.

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

The actual implementation is included in the library, so you can use it immediately.

### 2. Usage Example

```kotlin
@RestController
class UserController(
    private val userRepository: UserRepository,
) {
    @CountQueries
    @GetMapping("/users/coroutine")
    fun getUsers() {
        // Now this Coroutine and its branching Coroutines maintain the same RequestAttributes
        val element = CoroutineQueryCountContextElement()
        val threadPool = ForkJoinPool(2)
        return runBlocking(threadPool.asCoroutineDispatcher() + element) {
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
