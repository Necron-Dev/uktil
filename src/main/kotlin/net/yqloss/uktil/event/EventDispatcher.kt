@file:Suppress("NOTHING_TO_INLINE")

package net.yqloss.uktil.event

import net.yqloss.uktil.functional.UnaryTransformer
import net.yqloss.uktil.generic.castTo
import kotlin.reflect.KClass

interface EventDispatcher :
    (Event) -> Unit,
    (Event, (UnaryTransformer<Event, Unit>) -> UnaryTransformer<Event, Unit>) -> Unit {
    fun <T : Event> getHandler(
        type: KClass<T>,
        executionPolicy: (UnaryTransformer<Event, Unit>) -> UnaryTransformer<Event, Unit> = { it },
    ): EventHandler<T>

    fun <T : Event> getHandlerOnly(
        type: KClass<T>,
        executionPolicy: (UnaryTransformer<Event, Unit>) -> UnaryTransformer<Event, Unit> = { it },
    ): EventHandler<T>

    override fun invoke(event: Event) = getHandler(event)(event)

    override fun invoke(
        event: Event,
        executionPolicy: (UnaryTransformer<Event, Unit>) -> UnaryTransformer<Event, Unit>,
    ) = getHandler(event, executionPolicy)(event)
}

inline fun <reified T : Event> EventDispatcher.getHandler(
    noinline executionPolicy: (UnaryTransformer<Event, Unit>) -> UnaryTransformer<Event, Unit> = { it },
) = getHandler(T::class, executionPolicy)

inline fun <reified T : Event> EventDispatcher.getHandlerOnly(
    noinline executionPolicy: (UnaryTransformer<Event, Unit>) -> UnaryTransformer<Event, Unit> = { it },
) = getHandlerOnly(T::class, executionPolicy)

inline fun <T : Event> EventDispatcher.getHandler(
    event: T,
    noinline executionPolicy: (UnaryTransformer<Event, Unit>) -> UnaryTransformer<Event, Unit> = { it },
) = getHandler(event::class.castTo<KClass<T>>(), executionPolicy)

inline fun <T : Event> EventDispatcher.getHandlerOnly(
    event: T,
    noinline executionPolicy: (UnaryTransformer<Event, Unit>) -> UnaryTransformer<Event, Unit> = { it },
) = getHandlerOnly(event::class.castTo<KClass<T>>(), executionPolicy)
