package hyeon9mak.multidatasourcequerycounter;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.web.context.request.RequestContextHolder;

import java.util.Arrays;
import java.util.List;

public class PreparedStatementQueryMonitor implements MethodInterceptor {

    private static final List<String> QUERY_METHODS = Arrays.asList("execute", "executeQuery", "executeUpdate");

    private final QueryCounterRequestContextScopeHolder queryCounterRequestContextScopeHolder;

    public PreparedStatementQueryMonitor(QueryCounterRequestContextScopeHolder queryCounterRequestContextScopeHolder) {
        this.queryCounterRequestContextScopeHolder = queryCounterRequestContextScopeHolder;
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        if (QUERY_METHODS.contains(invocation.getMethod().getName()) && isRequestScope()) {
            long startTime = System.currentTimeMillis();
            Object result = invocation.proceed();
            long endTime = System.currentTimeMillis();

            queryCounterRequestContextScopeHolder.get().incrementQueryCount(endTime - startTime);

            return result;
        }

        return invocation.proceed();
    }

    private boolean isRequestScope() {
        return RequestContextHolder.getRequestAttributes() != null;
    }
}
