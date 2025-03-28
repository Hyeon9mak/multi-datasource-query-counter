package hyeon9mak.multidatasourcequerycounter

import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.reflect.MethodSignature
import org.springframework.stereotype.Component
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes

@Aspect
@Component
class HikariDataSourceQueryCounter(
    private val queryCountLogger: QueryCountLogger,
) {
    @Around("execution( * com.zaxxer.hikari.HikariDataSource.getConnection())")
    fun aroundConnection(joinPoint: ProceedingJoinPoint): Any {
        val connection = joinPoint.proceed()
        val connectionQueryMonitor = ConnectionQueryMonitor(QueryCountPerRequestHolder.get(), connection)
        return connectionQueryMonitor.getProxy()
    }

    @Around("@annotation(countQueries)")
    fun aroundCountQueriesMethod(joinPoint: ProceedingJoinPoint, countQueries: CountQueries): Any {
        val queryCountPerRequest = QueryCountPerRequest()
        val attributes = RequestContextHolder.getRequestAttributes() as ServletRequestAttributes?

        if (attributes.isInRequestScope) {
            val request = attributes!!.request
            queryCountPerRequest.apiUrl = request.method + " " + request.requestURI
        } else {
            val methodSignature = joinPoint.signature as MethodSignature
            val methodName = methodSignature.method.name
            val className = methodSignature.declaringType.simpleName
            queryCountPerRequest.apiUrl = countQueries.prefix + className + "." + methodName
        }

        QueryCountPerRequestHolder.set(queryCountPerRequest)
        val result = joinPoint.proceed()
        queryCountLogger.logQueryCount(queryCountPerRequest)
        QueryCountPerRequestHolder.remove()
        return result
    }

    private val ServletRequestAttributes?.isInRequestScope: Boolean
        get() = this != null
}
