package hyeon9mak.multidatasourcequerycounter;

import org.springframework.stereotype.Component;

@Component
public class QueryCounterRequestContextScopeHolder {
    private final ThreadLocal<QueryCountPerRequest> threadLocal;

    public QueryCounterRequestContextScopeHolder(QueryCountPerRequest queryCountPerRequest) {
        this.threadLocal = ThreadLocal.withInitial(() -> queryCountPerRequest);
    }

    public QueryCountPerRequest get() {
        return threadLocal.get();
    }
}
