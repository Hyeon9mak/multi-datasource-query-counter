package hyeon9mak.multidatasourcequerycounter

import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicLong

data class QueryCountPerRequest(
    var apiUrl: String = "",
    private var _totalQueryCount: AtomicInteger = AtomicInteger(0),
    private var _totalQueryMilliSeconds: AtomicLong = AtomicLong(0L),
) {
    val totalQueryCount: Int
        get() = _totalQueryCount.get()

    val totalQueryMilliSeconds: Long
        get() = _totalQueryMilliSeconds.get()

    fun incrementQueryCount(executionMilliSeconds: Long) {
        _totalQueryCount.incrementAndGet()
        _totalQueryMilliSeconds.addAndGet(executionMilliSeconds)
    }
}
