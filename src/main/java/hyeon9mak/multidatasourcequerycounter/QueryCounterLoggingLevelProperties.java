package hyeon9mak.multidatasourcequerycounter;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "query-counter.logging.level")
@Component
public class QueryCounterLoggingLevelProperties {

    private final QueryCounterLoggingLevelProperty info;
    private final QueryCounterLoggingLevelProperty warn;
    private final QueryCounterLoggingLevelProperty error;

    public QueryCounterLoggingLevelProperties() {
        this.info = new QueryCounterLoggingLevelProperty();
        this.warn = new QueryCounterLoggingLevelProperty();
        this.error = new QueryCounterLoggingLevelProperty();
    }

    public QueryCounterLoggingLevelProperty getInfo() {
        return info;
    }

    public QueryCounterLoggingLevelProperty getWarn() {
        return warn;
    }

    public QueryCounterLoggingLevelProperty getError() {
        return error;
    }
}
