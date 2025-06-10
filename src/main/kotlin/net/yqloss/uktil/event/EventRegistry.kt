package net.yqloss.uktil.event

import net.yqloss.uktil.functional.UnaryTransformer
import kotlin.reflect.KClass

interface EventRegistry {
    // reusing handler objects is not allowed

    fun <T : Event> register(
        type: KClass<T>,
        priority: Int = 0,
        executionPolicy: (UnaryTransformer<Event, Unit>) -> UnaryTransformer<Event, Unit> = { it },
        handler: EventHandler<T>,
    )

    fun <T : Event> registerOnly(
        type: KClass<T>,
        priority: Int = 0,
        executionPolicy: (UnaryTransformer<Event, Unit>) -> UnaryTransformer<Event, Unit> = { it },
        handler: EventHandler<T>,
    )

    fun unregister(handler: EventHandler<*>)

    fun unregisterOnly(handler: EventHandler<*>)

    fun unregisterAll(handler: EventHandler<*>)

    fun clear()
}

inline fun <reified T : Event> EventRegistry.register(
    priority: Int = 0,
    noinline executionPolicy: (UnaryTransformer<Event, Unit>) -> UnaryTransformer<Event, Unit> = { it },
    noinline handler: EventHandler<T>,
) = register(T::class, priority, executionPolicy, handler)

inline fun <reified T : Event> EventRegistry.registerOnly(
    priority: Int = 0,
    noinline executionPolicy: (UnaryTransformer<Event, Unit>) -> UnaryTransformer<Event, Unit> = { it },
    noinline handler: EventHandler<T>,
) = registerOnly(T::class, priority, executionPolicy, handler)
