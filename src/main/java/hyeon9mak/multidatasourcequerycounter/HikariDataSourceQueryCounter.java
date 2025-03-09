package hyeon9mak.multidatasourcequerycounter;

import javax.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

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

    @After("within(@org.springframework.web.bind.annotation.RestController *)")
    public void afterApiFinished() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        if (isInRequestScope(attributes)) {
            HttpServletRequest request = attributes.getRequest();
            queryCountPerRequest.updateApiUrl(request.getMethod() + request.getRequestURI());
        }

        queryCountLogger.logQueryCount(queryCountPerRequest);
    }

    private boolean isInRequestScope(ServletRequestAttributes attributes) {
        return attributes != null;
    }
}
