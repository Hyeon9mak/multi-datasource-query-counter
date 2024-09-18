package com.example.multidatasourcequerycounter.learningmanagementservice.querylogger

import org.aopalliance.intercept.MethodInterceptor
import org.aopalliance.intercept.MethodInvocation
import org.springframework.web.context.request.RequestContextHolder
import java.lang.System.*

class PreparedStatementQueryLogger(
    private val queryLog: QueryLog,
) : MethodInterceptor {

    override fun invoke(invocation: MethodInvocation): Any? {
        if (QUERY_METHODS.contains(invocation.method.name) && isRequestScope()) {
            val startTime = currentTimeMillis()
            val result = invocation.proceed()
            val endTime = currentTimeMillis()

            queryLog.log(executionMilliSeconds = endTime - startTime)

            return result
        }

        return invocation.proceed()
    }

    private fun isRequestScope(): Boolean = RequestContextHolder.getRequestAttributes() != null

    companion object {
        private val QUERY_METHODS = listOf("executeQuery", "execute", "executeUpdate")
    }
}
