# 🖥️ Multi-DataSource Query Counter

**한국어** | [English](README-EN.md)

## 🖥️ 소개

이 프로젝트는 JDBC API(Spring Data JPA(Hibernate), MyBatis, ...) 환경에서 쿼리 개수를 측정하는 간단한 도구입니다.  
Multi-DataSource 환경에서도 원활하게 API 요청당 쿼리 개수를 측정할 수 있습니다.

```
Hibernate: 
    select
        id,
        name
    from
        example 
        
Hibernate: 
    select
        count(id) 
    from
        example
                 
ERROR --- 'GET /examples' - totalQueryCount: 2, totalSpendTime: 7ms
```

<br>

## 🖥️ 사용 방법

### 1. 라이브러리 의존성 추가

> [!IMPORTANT]   
> Spring Boot 3.x는 `jakarta.*` 패키지를 사용하고 Spring Boot 2.x는 `javax.*` 패키지를 사용합니다. 프로젝트와 호환되는 라이브러리 버전을 선택해야 합니다.

사용중인 Spring Boot 버전에 따라 적절한 라이브러리 버전을 선택하세요:

| Spring Boot Version | Library Version     |
|---------------------|---------------------|
| Spring Boot 2.x     | 2.x.x-spring-boot-2 |
| Spring Boot 3.x     | 2.x.x-spring-boot-3 |

### Java Gradle(Groovy DSL)

```groovy
repositories {
    mavenCentral()
    maven { url 'https://jitpack.io' }
}

dependencies {
    // For Spring Boot 2.x
    implementation 'com.github.Hyeon9mak:multi-datasource-query-counter:2.0.2-spring-boot-2'

    // For Spring Boot 3.x
    // implementation 'com.github.Hyeon9mak:multi-datasource-query-counter:2.0.2-spring-boot-3'
}
```

### Kotlin Gradle(Kotlin DSL)

```kotlin
repositories {
    mavenCentral()
    maven { url = uri("https://jitpack.io") }
}

dependencies {
    // For Spring Boot 2.x
    implementation("com.github.Hyeon9mak:multi-datasource-query-counter:2.0.2-spring-boot-2")

    // For Spring Boot 3.x
    // implementation("com.github.Hyeon9mak:multi-datasource-query-counter:2.0.2-spring-boot-3")
}
```

### 2. 로깅 옵션 설정

우선순위는 다음과 같습니다: `error` > `warn` > `info`.  
`enable` 의 기본값은 `false` 이고, `count` 의 기본값은 `1` 입니다.

```yaml
query-counter.logging.level:
  error:
    enable: true
    count: 5
  warn:
    enable: true
    count: 2
  info:
    enable: false
```

### 3. 대상 API 지정

`@CountQueries` 어노테이션을 사용하여 측정하고 싶은 API에 지정하세요.

```java
@CountQueries
@GetMapping("/examples")
public List<Example> getExamples() {
    return repository.getExamples();
}
```

### 4. 로깅 결과 확인

애플리케이션을 시작하고 로그를 확인하세요.

```
ERROR --- 'GET /examples' - totalQueryCount: 2, totalSpendTime: 7ms
```

이제부터 마음껏 즐기시면 됩니다! 🎉

<br>

## 🖥️ 비동기 환경 지원

- [Java 비동기 환경에 대한 가이드를 읽으려면 여기를 클릭하세요.](README-java-asynchronous-environments.md)
- [Kotlin 코루틴 환경에 대한 원리를 확인하고 싶다면 여기를 클릭하세요.](README-kotlin-coroutine-environments.md)

<br>

## 🖥️ 핵심 원리

![image](core_concept.png)

JDBC API와 Spring AOP, CGLib 프록시를 기반으로 합니다.  
... 이게 전부입니다!

<br>

## 🖥️ 추천 문서

- [API 요청 당 쿼리 개수를 알고 싶어 라이브러리까지 만든 이야기 — 라이브러리 제작](https://medium.com/@hyeon9mak/api-%EC%9A%94%EC%B2%AD-%EB%8B%B9-%EC%BF%BC%EB%A6%AC-%EA%B0%9C%EC%88%98%EB%A5%BC-%EC%95%8C%EA%B3%A0-%EC%8B%B6%EC%96%B4-%EB%9D%BC%EC%9D%B4%EB%B8%8C%EB%9F%AC%EB%A6%AC%EA%B9%8C%EC%A7%80-%EB%A7%8C%EB%93%A0-%EC%9D%B4%EC%95%BC%EA%B8%B0-%EB%9D%BC%EC%9D%B4%EB%B8%8C%EB%9F%AC%EB%A6%AC-%EC%A0%9C%EC%9E%91-de39f0d27351)
- [Hibernate 의 ‘불편한’ 편의 기능들](https://medium.com/monday-9-pm/hibernate-%EC%9D%98-%EB%B6%88%ED%8E%B8%ED%95%9C-%ED%8E%B8%EC%9D%98-%EA%B8%B0%EB%8A%A5%EB%93%A4-06a1fbc7492a)
