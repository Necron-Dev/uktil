package dev.necron.uktil.event

import dev.necron.uktil.generic.castTo
import kotlin.reflect.KClass

interface EventDispatcher : (Event) -> Unit {
    fun <T : Event> getHandler(type: KClass<T>): EventHandler<T>

    fun <T : Event> getHandler(event: T) = getHandler(event::class.castTo<KClass<T>>())

    fun <T : Event> getHandlerOnly(type: KClass<T>): EventHandler<T>

    fun <T : Event> getHandlerOnly(event: T) = getHandlerOnly(event::class.castTo<KClass<T>>())

    override fun invoke(event: Event) = getHandler(event)(event)
}

inline fun <reified T : Event> EventDispatcher.getHandler() = getHandler(T::class)

inline fun <reified T : Event> EventDispatcher.getHandlerOnly() = getHandlerOnly(T::class)
