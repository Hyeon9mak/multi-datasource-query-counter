# 🖥️ Sharing QueryCountPerRequest Across Threads in Asynchronous Environments

[한국어](README-java-asynchronous-environments.md) | **English**

## 🖥️ Overview

The Multi-Datasource-Query-Counter library counts database queries per API request. By default, it uses `@RequestScope` to ensure each request has its own counter. 
However, in asynchronous environments (`CompletableFuture`, `@Async`, WebFlux, etc.), execution switches to new threads, which cannot access the original request context, resulting in missed query counts.

This guide explains how to properly share the `QueryCountPerRequest` object across threads in asynchronous environments.

<br>

## 🖥️ The Problem

In a typical Spring application, a request is processed within a single thread. 
However, when using asynchronous code:

1. Execution switches to a new thread.
2. The new thread has a new context.

So, the new thread cannot access the original request context.

<br>

## 🖥️ Solution: Using TaskDecorator

Spring provides `TaskDecorator` which allows copying the request context to new threads before executing asynchronous tasks.

### 1-1. Configure ThreadPoolTaskExecutor

```java
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.util.concurrent.Executor;

@EnableAsync
@Configuration
public class AsyncConfig {
    
    @Bean(name = "asyncExecutor")
    public Executor asyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        
        // Configure thread pool settings according to your application needs... (skip)
        
        // Set TaskDecorator to copy the request context to the new thread!
        executor.setTaskDecorator(task -> {
            RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
            return () -> {
                try {
                    RequestContextHolder.setRequestAttributes(requestAttributes);
                    task.run();
                } finally {
                    RequestContextHolder.resetRequestAttributes();
                }
            };
        });
        
        executor.initialize();
        return executor;
    }
}
```

### 1-2. Using CompletableFuture

When using CompletableFuture directly, use the custom Executor:

```java
@Autowired
@Qualifier("asyncExecutor")
private Executor asyncExecutor;

public CompletableFuture<List<User>> findAllUsersAsync() {
    return CompletableFuture.supplyAsync(() -> {
        return userRepository.findAll(); // Query count point.
    }, asyncExecutor);                   // Specify the custom executor.
}
```

### 1-3. Using @Async Annotation

When using the @Async annotation, explicitly specify the configured Executor:

```java
@Async("asyncExecutor")  // Specify the bean name explicitly.
public CompletableFuture<User> findUserByIdAsync(Long id) {
    return CompletableFuture.completedFuture(
        userRepository.findById(id).orElse(null) // Query count point.
    );
}
```

<br>

## 🖥️ WebFlux/Reactor Environment

In a WebFlux environment, you can propagate the context as follows:

```java
@GetMapping("/users/reactive")
public Flux<User> getAllUsers() {
    return Flux.deferContextual(contextView -> {
        // Get information from the current context
        // Execute async operations
        return userRepository.findAllReactive();
    });
}
```

You can also automate context propagation using library like `reactor-core-micrometer`.
