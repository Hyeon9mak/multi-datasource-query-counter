package com.example.multidatasourcequerycounter.learningmanagementservice

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
