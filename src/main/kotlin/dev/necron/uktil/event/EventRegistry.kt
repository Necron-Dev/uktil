package dev.necron.uktil.event

import kotlin.reflect.KClass

interface EventRegistry {
    // reusing handler objects is not allowed

    fun <T : Event> register(
        type: KClass<T>,
        priority: Int = 0,
        handler: EventHandler<T>,
    )

    fun <T : Event> registerOnly(
        type: KClass<T>,
        priority: Int = 0,
        handler: EventHandler<T>,
    )

    fun unregister(handler: EventHandler<*>)

    fun unregisterOnly(handler: EventHandler<*>)

    fun unregisterAll(handler: EventHandler<*>)

    fun clear()
}

inline fun <reified T : Event> EventRegistry.register(
    priority: Int = 0,
    noinline handler: EventHandler<T>,
) = register(T::class, priority, handler)

inline fun <reified T : Event> EventRegistry.registerOnly(
    priority: Int = 0,
    noinline handler: EventHandler<T>,
) = registerOnly(T::class, priority, handler)
