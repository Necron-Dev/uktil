package dev.necron.uktil.event

import kotlin.reflect.KClass

class SubEventRegistry(
    private val parent: ManagerEventRegistry<Any?>,
    private val key: Any?,
) : EventRegistry {
    override fun <T : Event> register(
        type: KClass<T>,
        priority: Int,
        handler: EventHandler<T>,
    ) = parent.register(key, type, priority, handler)

    override fun <T : Event> registerOnly(
        type: KClass<T>,
        priority: Int,
        handler: EventHandler<T>,
    ) = parent.registerOnly(key, type, priority, handler)

    override fun unregister(handler: EventHandler<*>) = parent.unregister(handler)

    override fun unregisterOnly(handler: EventHandler<*>) = parent.unregisterOnly(handler)

    override fun unregisterAll(handler: EventHandler<*>) = parent.unregisterAll(handler)

    override fun clear() = parent.unregisterKey(key)
}
