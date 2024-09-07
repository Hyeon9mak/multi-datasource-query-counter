package com.example.multidatasourcequerycounter.learningmanagementservice

import mu.KotlinLogging
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.After
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.springframework.stereotype.Component
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes

@Aspect
@Component
class QueryCounter(
    private val queryLog: QueryLog,
) {
    private val logger = KotlinLogging.logger {}

    @Around("execution( * javax.sql.DataSource.getConnection())")
    fun aroundConnection(joinPoint: ProceedingJoinPoint): Any {
        val connection = joinPoint.proceed()
        val connectionQueryLogger = ConnectionQueryLogger(queryLog, connection)
        return connectionQueryLogger.getProxy()
    }

    @After("within(@org.springframework.web.bind.annotation.RestController *)")
    fun afterApiFinished() {
        val attributes = RequestContextHolder.getRequestAttributes() as ServletRequestAttributes?

        if (attributes.isInRequestScope) {
            val request = attributes!!.request
            queryLog.apiUrl = "${request.method} ${request.requestURI}"
        }

        logger.info { queryLog }
    }

    private val ServletRequestAttributes?.isInRequestScope: Boolean
        get() = this != null
}
