package com.example.multidatasourcequerycounter

import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.After
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.springframework.stereotype.Component
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes

@Aspect
@Component
class HikariDataSourceQueryCounter(
    private val queryCountPerRequest: QueryCountPerRequest,
    private val queryCountLogger: QueryCountLogger,
) {

    @Around("execution( * com.zaxxer.hikari.HikariDataSource.getConnection())")
    fun aroundConnection(joinPoint: ProceedingJoinPoint): Any {
        val connection = joinPoint.proceed()
        val connectionQueryMonitor = ConnectionQueryMonitor(queryCountPerRequest, connection)
        return connectionQueryMonitor.getProxy()
    }

    @After("within(@org.springframework.web.bind.annotation.RestController *)")
    fun afterApiFinished() {
        val attributes = RequestContextHolder.getRequestAttributes() as ServletRequestAttributes?

        if (attributes.isInRequestScope) {
            val request = attributes!!.request
            queryCountPerRequest.apiUrl = "${request.method} ${request.requestURI}"
        }

        queryCountLogger.logQueryCount(queryCountPerRequest)
    }

    private val ServletRequestAttributes?.isInRequestScope: Boolean
        get() = this != null
}
