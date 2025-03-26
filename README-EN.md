# 🖥️ Multi-DataSource Query Counter

[한국어](README.md) | **English**

## 🖥️ Introduction

This project is a simple tool to measuring query counts in a JDBC API(Spring Data JPA(Hibernate), MyBatis, ...) environment.  
You can seamlessly measure query counts also in a Multi-DataSource environment.

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

## 🖥️ How to use?

### 1. Add dependency

> [!IMPORTANT]   
> Spring Boot 3.x uses `jakarta.*` packages while Spring Boot 2.x uses `javax.*` packages. Make sure to select the compatible library version for your project.

Choose the appropriate version based on your Spring Boot version:

| Spring Boot Version | Library Version     |
|---------------------|---------------------|
| Spring Boot 2.x     | 2.x.x-spring-boot-2 |
| Spring Boot 3.x     | 2.x.x-spring-boot-3 |

### In Java Gradle(Groovy DSL)

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

### In Kotlin Gradle(Kotlin DSL)

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

### 2. Add logging options

The priority order is as follows: `error` > `warn` > `info`.  
The default value for `enable` is `false`, and the default value for `count` is `1`.

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

### 3. Specify the target API

`@CountQueries` annotation is used to specify the API you want to measure.

```java
@CountQueries
@GetMapping("/examples")
public List<Example> getExamples() {
    return repository.getExamples();
}
```

### 4. Check the log

Start the application and check the log.

```
ERROR --- 'GET /examples' - totalQueryCount: 2, totalSpendTime: 7ms
```

Enjoy it! 🎉

<br>

## 🖥️ In asynchronous environments

- [Click here to read the guide for Java Asynchronous environments.](README-java-asynchronous-environments-EN.md)
- [Click here to read the guide for Kotlin Coroutine environments.](README-kotlin-coroutine-environments-EN.md)

<br>

## 🖥️ Core concepts

![image](core_concept.png)

It's based on the JDBC API and Spring AOP, CGLib proxy.  
... That's it!

<br>

## 🖥️ Recommended articles

- [API 요청 당 쿼리 개수를 알고 싶어 라이브러리까지 만든 이야기 — 라이브러리 제작](https://medium.com/@hyeon9mak/api-%EC%9A%94%EC%B2%AD-%EB%8B%B9-%EC%BF%BC%EB%A6%AC-%EA%B0%9C%EC%88%98%EB%A5%BC-%EC%95%8C%EA%B3%A0-%EC%8B%B6%EC%96%B4-%EB%9D%BC%EC%9D%B4%EB%B8%8C%EB%9F%AC%EB%A6%AC%EA%B9%8C%EC%A7%80-%EB%A7%8C%EB%93%A0-%EC%9D%B4%EC%95%BC%EA%B8%B0-%EB%9D%BC%EC%9D%B4%EB%B8%8C%EB%9F%AC%EB%A6%AC-%EC%A0%9C%EC%9E%91-de39f0d27351)
- [Hibernate 의 ‘불편한’ 편의 기능들](https://medium.com/monday-9-pm/hibernate-%EC%9D%98-%EB%B6%88%ED%8E%B8%ED%95%9C-%ED%8E%B8%EC%9D%98-%EA%B8%B0%EB%8A%A5%EB%93%A4-06a1fbc7492a)
