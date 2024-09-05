package com.example.multidatasourcequerycounter.learningmanagementservice

import org.aopalliance.intercept.MethodInterceptor
import org.aopalliance.intercept.MethodInvocation
import org.springframework.aop.framework.ProxyFactory

class ConnectionQueryLogger(
    private val queryLog: QueryLog,
    private val connection: Any,
) : MethodInterceptor {

    override fun invoke(invocation: MethodInvocation): Any? {
        val result = invocation.proceed()

        if (result != null && invocation.preparedStatementInvoked()) {
            val proxyFactory = ProxyFactory(result)
            proxyFactory.addAdvice(PreparedStatementQueryLogger(queryLog = queryLog))
            return proxyFactory.proxy
        }

        return result
    }

    fun getProxy(): Any {
        val proxyFactory = ProxyFactory(connection)
        proxyFactory.addAdvice(this)
        return proxyFactory.proxy
    }

    private fun MethodInvocation.preparedStatementInvoked(): Boolean = this.method.name == JDBC_PREPARE_STATEMENT_METHOD_NAME

    companion object {
        private const val JDBC_PREPARE_STATEMENT_METHOD_NAME = "prepareStatement"
    }
}
