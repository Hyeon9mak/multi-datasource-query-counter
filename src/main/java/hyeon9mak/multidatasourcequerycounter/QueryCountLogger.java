package hyeon9mak.multidatasourcequerycounter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class QueryCountLogger {

    private static final String LOG_MESSAGE_FORMAT = "'{}' - totalQueryCount: {}, totalSpendTime: {}ms";

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final QueryCounterLoggingLevelProperties properties;

    public QueryCountLogger(QueryCounterLoggingLevelProperties properties) {
        this.properties = properties;
    }

    public void logQueryCount(QueryCountPerRequest queryCountPerRequest) {
        if (properties.getError().isEnable() && queryCountPerRequest.getTotalQueryCount() >= properties.getError().getCount()) {
            logger.error(LOG_MESSAGE_FORMAT, queryCountPerRequest.getApiUrl(), queryCountPerRequest.getTotalQueryCount(), queryCountPerRequest.getTotalQueryMilliSeconds());
        } else if (properties.getWarn().isEnable() && queryCountPerRequest.getTotalQueryCount() >= properties.getWarn().getCount()) {
            logger.warn(LOG_MESSAGE_FORMAT, queryCountPerRequest.getApiUrl(), queryCountPerRequest.getTotalQueryCount(), queryCountPerRequest.getTotalQueryMilliSeconds());
        } else if (properties.getInfo().isEnable() && queryCountPerRequest.getTotalQueryCount() >= properties.getInfo().getCount()) {
            logger.info(LOG_MESSAGE_FORMAT, queryCountPerRequest.getApiUrl(), queryCountPerRequest.getTotalQueryCount(), queryCountPerRequest.getTotalQueryMilliSeconds());
        }
    }
}
