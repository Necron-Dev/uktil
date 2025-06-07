package dev.necron.uktil.event

import dev.necron.uktil.generic.castTo
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

    override fun <T : Event> getHandler(type: KClass<T>): EventHandler<T> {
        return handlerCache.getOrPut(type) { dispatcher.getHandler(type) }.castTo()
    }

    override fun <T : Event> getHandlerOnly(type: KClass<T>): EventHandler<T> {
        return handlerOnlyCache.getOrPut(type) { dispatcher.getHandlerOnly(type) }.castTo()
    }
}
