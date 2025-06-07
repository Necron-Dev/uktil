@file:Suppress("NOTHING_TO_INLINE")

package dev.necron.uktil.event

import dev.necron.uktil.event.EventRegistration.Entry
import kotlin.reflect.KClass

interface EventRegistration {
    sealed interface Entry : (EventRegistry) -> Unit {
        val handler: EventHandler<*>
    }

    val eventEntries: List<Entry> get() = buildEventEntries(registerEvents)

    val registerEvents: EventRegistry.() -> Unit get() = {}
}

class OperationEventRegistry(
    private val list: MutableList<Entry>,
) : EventRegistry {
    private data class Register<T : Event>(
        val type: KClass<T>,
        val priority: Int,
        override val handler: EventHandler<T>,
    ) : Entry {
        override fun invoke(registry: EventRegistry) {
            registry.register(type, priority, handler)
        }
    }

    private data class RegisterOnly<T : Event>(
        val type: KClass<T>,
        val priority: Int,
        override val handler: EventHandler<T>,
    ) : Entry {
        override fun invoke(registry: EventRegistry) {
            registry.registerOnly(type, priority, handler)
        }
    }

    override fun <T : Event> register(
        type: KClass<T>,
        priority: Int,
        handler: EventHandler<T>,
    ) {
        list.add(Register(type, priority, handler))
    }

    override fun <T : Event> registerOnly(
        type: KClass<T>,
        priority: Int,
        handler: EventHandler<T>,
    ) {
        list.add(RegisterOnly(type, priority, handler))
    }

    override fun unregister(handler: EventHandler<*>) = throw NotImplementedError()

    override fun unregisterOnly(handler: EventHandler<*>) = throw NotImplementedError()

    override fun unregisterAll(handler: EventHandler<*>) = throw NotImplementedError()

    override fun clear() = throw NotImplementedError()
}

inline fun buildEventEntries(function: OperationEventRegistry.() -> Unit): List<Entry> {
    return mutableListOf<Entry>().also { function(OperationEventRegistry(it)) }
}

inline fun EventRegistration.registerEventEntries(registry: EventRegistry) {
    eventEntries.registerEventEntries(registry)
}

inline fun EventRegistration.unregisterEventEntries(registry: EventRegistry) {
    eventEntries.unregisterEventEntries(registry)
}

inline fun List<Entry>.registerEventEntries(registry: EventRegistry) {
    forEach { it(registry) }
}

inline fun List<Entry>.unregisterEventEntries(registry: EventRegistry) {
    val set = mutableSetOf<EventHandler<*>>()
    forEach { set.add(it.handler) }
    set.forEach(registry::unregisterAll)
}
