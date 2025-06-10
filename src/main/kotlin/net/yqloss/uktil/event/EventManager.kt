package net.yqloss.uktil.event

import net.yqloss.uktil.algorithm.hash.ConcurrentUniqueHash
import net.yqloss.uktil.algorithm.partialorder.PartialComparable
import net.yqloss.uktil.algorithm.partialorder.ascendingPartialComparator
import net.yqloss.uktil.algorithm.partialorder.toAscendingComparator
import net.yqloss.uktil.extension.type.prepend
import net.yqloss.uktil.functional.UnaryTransformer
import net.yqloss.uktil.functional.noUnaryTransformer
import net.yqloss.uktil.generic.castTo
import java.util.*
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.read
import kotlin.concurrent.write
import kotlin.reflect.KClass
import kotlin.reflect.full.allSuperclasses
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.full.isSuperclassOf

class EventManager(
    private val executionPolicy: UnaryTransformer<Event, Unit> = noUnaryTransformer(),
) : EventDispatcher,
    EventRegistry {
    private val lock = ReentrantReadWriteLock()

    private class RegistryEntry(
        val priority: Int,
        val handler: EventHandler<*>,
        val executionPolicy: UnaryTransformer<Event, Unit>,
    ) : PartialComparable<RegistryEntry> {
        override fun partialCompareTo(other: RegistryEntry): Int {
            return if (handler === other.handler) {
                0
            } else {
                priority compareTo other.priority
            }
        }

        override fun equals(other: Any?): Boolean {
            return (other as? RegistryEntry)?.let {
                handler === other.handler
            } == true
        }

        override fun hashCode() = handler.hashCode()
    }

    private val parentTypeHandlerMap = mutableMapOf<KClass<*>, TreeSet<RegistryEntry>>()

    private val onlyTypeHandlerMap = mutableMapOf<KClass<*>, TreeSet<RegistryEntry>>()

    private val parentHandlerCache = mutableMapOf<KClass<*>, EventHandler<*>>()

    private val onlyHandlerCache = mutableMapOf<KClass<*>, EventHandler<*>>()

    private fun newTreeSet() = TreeSet(
        ascendingPartialComparator<RegistryEntry>().toAscendingComparator(ConcurrentUniqueHash()),
    )

    private fun clearAllCache() {
        parentHandlerCache.clear()
        onlyHandlerCache.clear()
    }

    private fun clearCache(type: KClass<*>) {
        parentHandlerCache.entries.removeIf { type.isSuperclassOf(it.key) }
        onlyHandlerCache.entries.removeIf { type.isSuperclassOf(it.key) }
    }

    override fun <T : Event> register(
        type: KClass<T>,
        priority: Int,
        executionPolicy: (UnaryTransformer<Event, Unit>) -> UnaryTransformer<Event, Unit>,
        handler: EventHandler<T>,
    ) {
        lock.write {
            clearCache(type)
            unregister(handler)
            parentTypeHandlerMap
                .getOrPut(type, ::newTreeSet)
                .add(RegistryEntry(priority, handler, executionPolicy(this.executionPolicy)))
        }
    }

    override fun <T : Event> registerOnly(
        type: KClass<T>,
        priority: Int,
        executionPolicy: (UnaryTransformer<Event, Unit>) -> UnaryTransformer<Event, Unit>,
        handler: EventHandler<T>,
    ) {
        lock.write {
            clearCache(type)
            unregisterOnly(handler)
            onlyTypeHandlerMap
                .getOrPut(type, ::newTreeSet)
                .add(RegistryEntry(priority, handler, executionPolicy(this.executionPolicy)))
        }
    }

    private fun unregisterIn(
        handler: EventHandler<*>,
        registry: () -> Sequence<Map.Entry<KClass<*>, TreeSet<RegistryEntry>>>,
    ) {
        lock.write {
            registry().forEach { (type, entries) ->
                if (entries.removeIf { it.handler === handler }) {
                    clearCache(type)
                }
            }
        }
    }

    override fun unregister(handler: EventHandler<*>) {
        unregisterIn(handler) { parentTypeHandlerMap.entries.asSequence() }
    }

    override fun unregisterOnly(handler: EventHandler<*>) {
        unregisterIn(handler) { onlyTypeHandlerMap.entries.asSequence() }
    }

    override fun unregisterAll(handler: EventHandler<*>) {
        unregisterIn(handler) { parentTypeHandlerMap.entries.asSequence() + onlyTypeHandlerMap.entries.asSequence() }
    }

    override fun clear() {
        lock.write {
            clearAllCache()
            parentTypeHandlerMap.clear()
            onlyTypeHandlerMap.clear()
        }
    }

    override fun <T : Event> getHandler(
        type: KClass<T>,
        executionPolicy: (UnaryTransformer<Event, Unit>) -> UnaryTransformer<Event, Unit>,
    ): EventHandler<T> {
        lock.read {
            return parentHandlerCache
                .getOrPut(type) {
                    lock.write {
                        val set = newTreeSet()
                        type.allSuperclasses
                            .prepend(type)
                            .filter { it.isSubclassOf(Event::class) }
                            .forEach { parentTypeHandlerMap[it]?.let(set::addAll) }
                        onlyTypeHandlerMap[type]?.let(set::addAll)
                        EventHandlerHolder(set.toList().map { executionPolicy(it.executionPolicy)(it.handler) })
                    }
                }.castTo()
        }
    }

    override fun <T : Event> getHandlerOnly(
        type: KClass<T>,
        executionPolicy: (UnaryTransformer<Event, Unit>) -> UnaryTransformer<Event, Unit>,
    ): EventHandler<T> {
        lock.read {
            return onlyHandlerCache
                .getOrPut(type) {
                    lock.write {
                        val set = newTreeSet()
                        parentTypeHandlerMap[type]?.let(set::addAll)
                        onlyTypeHandlerMap[type]?.let(set::addAll)
                        EventHandlerHolder(set.toList().map { executionPolicy(it.executionPolicy)(it.handler) })
                    }
                }.castTo()
        }
    }
}
