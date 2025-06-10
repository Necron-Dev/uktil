package net.yqloss.uktil.event

import net.yqloss.uktil.functional.UnaryTransformer
import kotlin.reflect.KClass

interface ManagerEventRegistry<in TK> {
    // reusing handler objects is not allowed

    fun <T : Event> register(
        key: TK,
        type: KClass<T>,
        priority: Int = 0,
        executionPolicy: (UnaryTransformer<Event, Unit>) -> UnaryTransformer<Event, Unit> = { it },
        handler: EventHandler<T>,
    )

    fun <T : Event> registerOnly(
        key: TK,
        type: KClass<T>,
        priority: Int = 0,
        executionPolicy: (UnaryTransformer<Event, Unit>) -> UnaryTransformer<Event, Unit> = { it },
        handler: EventHandler<T>,
    )

    fun unregister(handler: EventHandler<*>)

    fun unregisterOnly(handler: EventHandler<*>)

    fun unregisterAll(handler: EventHandler<*>)

    fun unregisterKey(key: TK)

    fun clear()
}
