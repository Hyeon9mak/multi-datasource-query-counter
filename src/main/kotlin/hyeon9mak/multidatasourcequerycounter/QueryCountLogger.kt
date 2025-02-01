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
            logger.error { queryCountPerRequest }
        } else if (properties.warn.enable && queryCountPerRequest.totalQueryCount >= properties.warn.count) {
            logger.warn { queryCountPerRequest }
        } else if (properties.info.enable && queryCountPerRequest.totalQueryCount >= properties.info.count) {
            logger.info { queryCountPerRequest }
        }
    }
}
