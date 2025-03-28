package hyeon9mak.multidatasourcequerycounter

object QueryCountPerRequestHolder {

    private val queryCountPerRequestHolder = ThreadLocal<QueryCountPerRequest>()

    fun set(queryCountPerRequest: QueryCountPerRequest) {
        queryCountPerRequestHolder.set(queryCountPerRequest)
    }

    fun get(): QueryCountPerRequest = queryCountPerRequestHolder.get()

    fun remove() {
        queryCountPerRequestHolder.remove()
    }
}
