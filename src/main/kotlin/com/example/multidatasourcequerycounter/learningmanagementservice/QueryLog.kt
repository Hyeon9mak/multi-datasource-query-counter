package com.example.multidatasourcequerycounter.learningmanagementservice

import org.springframework.context.annotation.Scope
import org.springframework.context.annotation.ScopedProxyMode
import org.springframework.stereotype.Component

@Scope(scopeName = "request", proxyMode = ScopedProxyMode.TARGET_CLASS)
@Component
data class QueryLog(
    var apiUrl: String = "",
    var totalQueryCount: Int = 0,
    var totalQueryMilliSeconds: Long = 0L,
) {
    fun log(executionMilliSeconds: Long) {
        totalQueryCount++
        totalQueryMilliSeconds += executionMilliSeconds
    }
}
