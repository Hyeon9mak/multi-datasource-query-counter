package hyeon9mak.multidatasourcequerycounter

@Target(AnnotationTarget.FUNCTION, AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class CountQueries(
    /**
     * prefix for query count log message.
     */
    val prefix: String = ""
)
