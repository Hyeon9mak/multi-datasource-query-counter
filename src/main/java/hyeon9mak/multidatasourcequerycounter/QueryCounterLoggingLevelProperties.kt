package hyeon9mak.multidatasourcequerycounter

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@ConfigurationProperties(prefix = "query-counter.logging.level")
@Component
data class QueryCounterLoggingLevelProperties(
  val info: QueryCounterLoggingLevelProperty = QueryCounterLoggingLevelProperty(),
  val warn: QueryCounterLoggingLevelProperty = QueryCounterLoggingLevelProperty(),
  val error: QueryCounterLoggingLevelProperty = QueryCounterLoggingLevelProperty(),
) {
  data class QueryCounterLoggingLevelProperty(
    var enable: Boolean = false,
    var count: Int = 1
  )
}
