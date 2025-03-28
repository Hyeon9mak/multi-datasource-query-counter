package hyeon9mak.multidatasourcequerycounter

object QueryCountPerRequestHolder {

    private val queryCountPerRequestHolder = ThreadLocal<QueryCountPerRequest?>()
    private val coroutineContextElement = CoroutineQueryCountContextElement()

    fun set(queryCountPerRequest: QueryCountPerRequest) {
        queryCountPerRequestHolder.set(queryCountPerRequest)
        coroutineContextElement.queryCountPerRequest = queryCountPerRequest
    }

    fun get(): QueryCountPerRequest? = queryCountPerRequestHolder.get()
        ?: coroutineContextElement.queryCountPerRequest

    fun remove() {
        queryCountPerRequestHolder.remove()
        coroutineContextElement.queryCountPerRequest = null
    }
}
