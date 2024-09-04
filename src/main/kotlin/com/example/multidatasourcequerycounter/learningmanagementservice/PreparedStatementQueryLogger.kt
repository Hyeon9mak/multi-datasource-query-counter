package com.example.multidatasourcequerycounter.learningmanagementservice

import org.aopalliance.intercept.MethodInterceptor
import org.aopalliance.intercept.MethodInvocation
import java.lang.System.*

class PreparedStatementQueryLogger(
    private val queryLog: QueryLog,
) : MethodInterceptor {

    override fun invoke(invocation: MethodInvocation): Any? {
        if (QUERY_METHODS.contains(invocation.method.name)) {
            val startTime = currentTimeMillis()
            val result = invocation.proceed()
            val endTime = currentTimeMillis()

            queryLog.log(executionMilliSeconds = endTime - startTime)

            return result
        }

        return invocation.proceed()
    }

    companion object {
        private val QUERY_METHODS = listOf("executeQuery", "execute", "executeUpdate")
    }
}
