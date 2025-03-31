# 🖥️ 비동기 환경에서 스레드 간 QueryCountPerRequest 공유하기

**한국어** | [English](README-java-asynchronous-environments-EN.md)

## 🖥️ 개요

Multi-Datasource-Query-Counter 라이브러리는 API 요청별로 데이터베이스 쿼리 수를 계산합니다.
라이브러리는 기본적으로 `@RequestScope`를 활용하여 각 API 요청마다 카운터를 갖도록 하고 있습니다.
그러나 비동기 환경(`CompletableFuture`, `@Async`, WebFlux 등)에서 별다른 설정 없이 새 스레드로 전환이 진행되면 서로 다른 카운터를 갖게 되므로 올바른 쿼리 개수 측정이 불가능합니다.

이 가이드는 비동기 환경에서 스레드 간 `QueryCountPerRequest` 객체를 올바르게 공유하는 방법을 설명합니다.

<br>

## 🖥️ 문제점

일반적인 Spring 애플리케이션에서 API 요청은 단일 스레드 내에서 처리됩니다.
그러나 비동기 코드를 사용할 때는 아래와 같은 순서를 가집니다.

1. 새 스레드로 분기
2. 새 스레드는 새로운 컨텍스트를 갖는다.

때문에 이전 스레드의 컨텍스트에 접근할 수 없습니다.

<br>

## 🖥️ 해결책: TaskDecorator 사용하기

Spring 은 비동기 작업을 실행하기 전에 새 스레드에 컨텍스트를 복사할 수 있는 `TaskDecorator` 를 제공합니다.

### 1-1. ThreadPoolTaskExecutor 구성하기

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
        
        // 애플리케이션 요구사항에 맞게 스레드 풀 구성... (생략)

        executor.setTaskDecorator(task -> {
            // 현재 스레드의 QueryCountPerRequest 획득
            QueryCountPerRequest currentQueryCountPerRequest = QueryCountPerRequestHolder.INSTANCE.get();

            return () -> {
                try {
                    // 새로운 스레드에 QueryCountPerRequest 전파
                    if (currentQueryCountPerRequest != null) {
                        QueryCountPerRequestHolder.INSTANCE.set(currentQueryCountPerRequest);
                    }
                    task.run();
                } finally {
                    // 스레드 종료 시 QueryCountPerRequest 제거
                    QueryCountPerRequestHolder.INSTANCE.remove();
                }
            };
        });
        
        executor.initialize();
        return executor;
    }
}
```

### 1-2. CompletableFuture 사용하기

CompletableFuture 를 직접 사용할 때는 커스텀 Executor 를 활용합니다.

```java
@Autowired
@Qualifier("asyncExecutor")
private Executor asyncExecutor;

public CompletableFuture<List<User>> findAllUsersAsync() {
    return CompletableFuture.supplyAsync(() -> {
        return userRepository.findAll(); // 쿼리 카운트 지점.
    }, asyncExecutor);                   // 커스텀 Executor 지정.
}
```

### 1-3. @Async 어노테이션 사용하기

`@Async` 어노테이션을 사용할 때는 구성된 Executor 를 명시적으로 지정합니다.

```java
@Async("asyncExecutor")  // Bean 이름을 명시적으로 지정.
public CompletableFuture<User> findUserByIdAsync(Long id) {
    return CompletableFuture.completedFuture(
        userRepository.findById(id).orElse(null) // 쿼리 카운트 지점.
    );
}
```

<br>

## 🖥️ WebFlux/Reactor 환경

(WIP)
