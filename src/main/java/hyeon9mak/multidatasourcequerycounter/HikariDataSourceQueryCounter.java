package hyeon9mak.multidatasourcequerycounter;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

@Aspect
@Component
public class HikariDataSourceQueryCounter {

    private final QueryCountPerRequest queryCountPerRequest;
    private final QueryCountLogger queryCountLogger;

    public HikariDataSourceQueryCounter(QueryCountPerRequest queryCountPerRequest, QueryCountLogger queryCountLogger) {
        this.queryCountPerRequest = queryCountPerRequest;
        this.queryCountLogger = queryCountLogger;
    }

    @Around("execution( * com.zaxxer.hikari.HikariDataSource.getConnection())")
    public Object aroundConnection(ProceedingJoinPoint joinPoint) throws Throwable {
        Object connection = joinPoint.proceed();
        ConnectionQueryMonitor connectionQueryMonitor = new ConnectionQueryMonitor(queryCountPerRequest, connection);
        return connectionQueryMonitor.getProxy();
    }

    @Around("@annotation(countQueries)")
    public Object aroundCountQueriesMethod(ProceedingJoinPoint joinPoint, CountQueries countQueries) throws Throwable {
        Object result = joinPoint.proceed();

        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        if (isInRequestScope(attributes)) {
            HttpServletRequest request = attributes.getRequest();
            queryCountPerRequest.updateApiUrl(request.getMethod() + " " + request.getRequestURI());
        } else {
            MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
            String methodName = methodSignature.getMethod().getName();
            String className = methodSignature.getDeclaringType().getSimpleName();
            queryCountPerRequest.updateApiUrl(countQueries.prefix() + className + "." + methodName);
        }

        queryCountLogger.logQueryCount(queryCountPerRequest);

        return result;
    }

    private boolean isInRequestScope(ServletRequestAttributes attributes) {
        return attributes != null;
    }
}
