package com.example.multidatasourcequerycounter.learningmanagementservice.querylogger

import org.springframework.stereotype.Component
import org.springframework.web.context.annotation.RequestScope

@RequestScope
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
