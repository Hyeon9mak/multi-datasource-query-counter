package hyeon9mak.multidatasourcequerycounter

import mu.KotlinLogging
import org.springframework.stereotype.Component

@Component
class QueryCountLogger(
    private val properties: QueryCounterLoggingLevelProperties,
) {
    private val logger = KotlinLogging.logger {}

    fun logQueryCount(queryCountPerRequest: QueryCountPerRequest) {
        if (properties.error.enable && queryCountPerRequest.totalQueryCount >= properties.error.count) {
            logger.error(
                LOG_MESSAGE_FORMAT,
                queryCountPerRequest.apiUrl,
                queryCountPerRequest.totalQueryCount,
                queryCountPerRequest.totalQueryMilliSeconds,
            )
        } else if (properties.warn.enable && queryCountPerRequest.totalQueryCount >= properties.warn.count) {
            logger.warn(
                LOG_MESSAGE_FORMAT,
                queryCountPerRequest.apiUrl,
                queryCountPerRequest.totalQueryCount,
                queryCountPerRequest.totalQueryMilliSeconds,
            )
        } else if (properties.info.enable && queryCountPerRequest.totalQueryCount >= properties.info.count) {
            logger.info(
                LOG_MESSAGE_FORMAT,
                queryCountPerRequest.apiUrl,
                queryCountPerRequest.totalQueryCount,
                queryCountPerRequest.totalQueryMilliSeconds,
            )
        }
    }

    companion object {
        private const val LOG_MESSAGE_FORMAT: String = "'{}' - totalQueryCount: {}, totalSpendTime: {}ms"
    }
}
