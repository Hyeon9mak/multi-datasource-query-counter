package hyeon9mak.multidatasourcequerycounter

import kotlinx.coroutines.ThreadContextElement
import org.springframework.web.context.request.RequestAttributes
import org.springframework.web.context.request.RequestContextHolder
import kotlin.coroutines.CoroutineContext

/**
 * https://kotlinlang.org/api/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines/-thread-context-element/
 */
class CoroutineQueryCountContextElement(
    private val requestAttributes: RequestAttributes = RequestContextHolder.currentRequestAttributes(),
) : ThreadContextElement<RequestAttributes> {

    companion object Key : CoroutineContext.Key<CoroutineQueryCountContextElement>

    override val key: CoroutineContext.Key<CoroutineQueryCountContextElement>
        get() = Key

    override fun updateThreadContext(context: CoroutineContext): RequestAttributes {
        RequestContextHolder.setRequestAttributes(requestAttributes)
        return requestAttributes
    }

    override fun restoreThreadContext(context: CoroutineContext, oldState: RequestAttributes) {
        RequestContextHolder.setRequestAttributes(oldState)
    }
}
