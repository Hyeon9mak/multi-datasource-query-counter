package com.example.multidatasourcequerycounter.learningmanagementservice

data class QueryLog(
    val apiUrl: String,
    var totalQueryCount: Int,
    var totalQueryMilliSeconds: Long,
) {
    fun log(executionMilliSeconds: Long) {
        totalQueryCount++
        totalQueryMilliSeconds += executionMilliSeconds
    }
}
