package hyeon9mak.multidatasourcequerycounter

import kotlinx.coroutines.ThreadContextElement
import kotlin.coroutines.CoroutineContext

/**
 * https://kotlinlang.org/api/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines/-thread-context-element/
 */
class CoroutineQueryCountContextElement : ThreadContextElement<QueryCountPerRequest?> {

    companion object Key : CoroutineContext.Key<CoroutineQueryCountContextElement>

    override val key: CoroutineContext.Key<CoroutineQueryCountContextElement>
        get() = Key

    override fun restoreThreadContext(context: CoroutineContext, oldState: QueryCountPerRequest?) {
        if (oldState == null) {
            QueryCountPerRequestHolder.remove()
        } else {
            QueryCountPerRequestHolder.set(oldState)
        }
    }

    override fun updateThreadContext(context: CoroutineContext): QueryCountPerRequest? {
        return QueryCountPerRequestHolder.get()
    }
}
