package net.yqloss.uktil.event

import net.yqloss.uktil.functional.UnaryTransformer
import net.yqloss.uktil.generic.castTo
import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass

class CachedEventDispatcher(
    private val dispatcher: EventDispatcher,
) : EventDispatcher by dispatcher {
    private val handlerCache = ConcurrentHashMap<KClass<*>, EventHandler<*>>()
    private val handlerOnlyCache = ConcurrentHashMap<KClass<*>, EventHandler<*>>()

    fun clearCache() {
        handlerCache.clear()
        handlerOnlyCache.clear()
    }

    override fun <T : Event> getHandler(
        type: KClass<T>,
        executionPolicy: (UnaryTransformer<Event, Unit>) -> UnaryTransformer<Event, Unit>,
    ): EventHandler<T> {
        return handlerCache.getOrPut(type) { dispatcher.getHandler(type, executionPolicy) }.castTo()
    }

    override fun <T : Event> getHandlerOnly(
        type: KClass<T>,
        executionPolicy: (UnaryTransformer<Event, Unit>) -> UnaryTransformer<Event, Unit>,
    ): EventHandler<T> {
        return handlerOnlyCache.getOrPut(type) { dispatcher.getHandlerOnly(type, executionPolicy) }.castTo()
    }
}
