package com.example.multidatasourcequerycounter.learningmanagementservice

import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.After
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes


@Aspect
class QueryCounter {
    private val currentQueryLog: ThreadLocal<QueryLog> = ThreadLocal<QueryLog>()

    @Around("execution( * javax.sql.DataSource.getConnection())")
    fun aroundConnection(joinPoint: ProceedingJoinPoint): Any {
        val connection = joinPoint.proceed()
        val queryLog = getCurrentQueryLog()
        val connectionQueryLogger = ConnectionQueryLogger(queryLog, connection)
        return connectionQueryLogger.getProxy()
    }

    @After("within(@org.springframework.web.bind.annotation.RestController *)")
    fun afterApiFinished() {
        val attributes = RequestContextHolder.getRequestAttributes() as ServletRequestAttributes?

        if (attributes.isInRequestScope) {
            val loggingForm = getCurrentQueryLog()
            val request = attributes!!.request
            loggingForm.apiUrl = "${request.method} ${request.requestURI}"
        }

        currentQueryLog.remove()
    }

    private fun getCurrentQueryLog(): QueryLog {
        if (currentQueryLog.get() == null) {
            currentQueryLog.set(QueryLog())
        }

        return currentQueryLog.get()
    }

    private val ServletRequestAttributes?.isInRequestScope: Boolean
        get() = this != null
}
