package hyeon9mak.multidatasourcequerycounter

import org.aopalliance.intercept.MethodInterceptor
import org.aopalliance.intercept.MethodInvocation
import java.lang.System.*

class PreparedStatementQueryMonitor(
    private val queryCountPerRequest: QueryCountPerRequest,
) : MethodInterceptor {

    override fun invoke(invocation: MethodInvocation): Any? {
        if (QUERY_METHODS.contains(invocation.method.name)) {
            val startTime = currentTimeMillis()
            val result = invocation.proceed()
            val endTime = currentTimeMillis()

            queryCountPerRequest.incrementQueryCount(executionMilliSeconds = endTime - startTime)

            return result
        }

        return invocation.proceed()
    }

    companion object {
        private val QUERY_METHODS = listOf("executeQuery", "execute", "executeUpdate")
    }
}
