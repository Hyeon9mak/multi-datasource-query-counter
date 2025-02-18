package hyeon9mak.multidatasourcequerycounter;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.framework.ProxyFactory;

public class ConnectionQueryMonitor implements MethodInterceptor {

    private static final String JDBC_PREPARE_STATEMENT_METHOD_NAME = "prepareStatement";

    private final QueryCountPerRequest queryCountPerRequest;
    private final Object connection;

    public ConnectionQueryMonitor(QueryCountPerRequest queryCountPerRequest, Object connection) {
        this.queryCountPerRequest = queryCountPerRequest;
        this.connection = connection;
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Object result = invocation.proceed();

        if (result != null && wasPreparedStatementInvoked(invocation)) {
            ProxyFactory proxyFactory = new ProxyFactory(result);
            proxyFactory.addAdvice(new PreparedStatementQueryMonitor(queryCountPerRequest));
            return proxyFactory.getProxy();
        }

        return result;
    }

    public Object getProxy() {
        ProxyFactory proxyFactory = new ProxyFactory(connection);
        proxyFactory.addAdvice(this);
        return proxyFactory.getProxy();
    }

    private boolean wasPreparedStatementInvoked(MethodInvocation invocation) {
        return invocation.getMethod().getName().equals(JDBC_PREPARE_STATEMENT_METHOD_NAME);
    }
}
