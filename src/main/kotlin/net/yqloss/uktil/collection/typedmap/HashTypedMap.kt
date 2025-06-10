package net.yqloss.uktil.collection.typedmap

import net.yqloss.uktil.generic.castTo
import kotlin.reflect.KClass

class HashTypedMap : MutableTypedMap {
    private val backingMap: MutableMap<KClass<*>, Any>

    constructor() {
        backingMap = mutableMapOf()
    }

    constructor(initialCapacity: Int) {
        backingMap = HashMap(initialCapacity)
    }

    constructor(typedMap: TypedMap) {
        backingMap = HashMap(typedMap.map)
    }

    constructor(entries: Collection<TypedMapEntryPair<*>>) {
        backingMap = HashMap(entries.size)
        entries.forEach { backingMap.put(it.type, it.value) }
    }

    constructor(entries: Iterable<TypedMapEntryPair<*>>) : this(entries.toList())

    override val map get() = backingMap

    override val size get() = backingMap.size

    override fun isEmpty() = backingMap.isEmpty()

    override fun containsKey(key: KClass<*>) = backingMap.containsKey(key)

    override fun containsValue(value: Any) = backingMap.containsValue(value)

    override fun <V : Any> get(key: KClass<V>) = backingMap[key].castTo<V?>()

    override fun <V : Any> put(key: KClass<V>, value: V) = backingMap.put(key, value).castTo<V?>()

    override fun <V : Any> remove(key: KClass<V>) = backingMap.remove(key).castTo<V?>()

    override fun <V : Any> remove(key: KClass<V>, value: V) = backingMap.remove(key, value)

    override fun putAll(from: TypedMap) = backingMap.putAll(from.map)

    override fun clear() = backingMap.clear()

    override val keys get() = backingMap.keys

    override val values get() = backingMap.values

    override val entries
        get() = HashSet<MutableTypedMap.MutableEntry<*>>(size).apply {
            backingMap.keys.forEach { key ->
                add(object : MutableTypedMap.MutableEntry<Any> {
                    override val type = key.castTo<KClass<Any>>()

                    override val value get() = backingMap[key]!!

                    override fun setValue(newValue: Any) = backingMap.put(key, newValue)!!
                })
            }
        }

    override fun iterator(): Iterator<MutableTypedMap.MutableEntry<*>> = iterator {
        backingMap.keys.forEach { key ->
            yield(object : MutableTypedMap.MutableEntry<Any> {
                override val type = key.castTo<KClass<Any>>()

                override val value get() = backingMap[key]!!

                override fun setValue(newValue: Any) = backingMap.put(key, newValue)!!
            })
        }
    }
}
