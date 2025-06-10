@file:Suppress("NOTHING_TO_INLINE")

package net.yqloss.uktil.collection.typedmap

import kotlin.reflect.KClass

interface MutableTypedMap : TypedMap {
    fun <V : Any> put(key: KClass<V>, value: V): V?

    fun <V : Any> remove(key: KClass<V>): V?

    fun <V : Any> remove(key: KClass<V>, value: V): Boolean

    fun putAll(from: TypedMap)

    fun clear()

    override val entries: Set<MutableEntry<*>>

    interface MutableEntry<T : Any> : TypedMap.Entry<T> {
        fun setValue(newValue: T): T
    }
}

inline fun <reified V : Any> MutableTypedMap.put(value: V) {
    put(V::class, value)
}

inline operator fun <V : Any> MutableTypedMap.set(key: KClass<V>, value: V) {
    put(key, value)
}

inline fun <V : Any> MutableTypedMap.getOrPut(key: KClass<V>, value: () -> V) = get(key) ?: value().also { put(key, it) }

inline operator fun MutableTypedMap.plusAssign(typedMap: TypedMap) = putAll(typedMap)

inline operator fun <V : Any> MutableTypedMap.plusAssign(entry: TypedMapEntryPair<V>) {
    put(entry.type, entry.value)
}

inline operator fun <V : Any> MutableTypedMap.plusAssign(entry: TypedMap.Entry<V>) {
    put(entry.type, entry.value)
}

inline operator fun <V : Any> MutableTypedMap.plusAssign(entries: Iterable<TypedMapEntryPair<V>>) {
    putAll(HashTypedMap(entries))
}

inline operator fun <V : Any> MutableTypedMap.plusAssign(entries: Collection<TypedMapEntryPair<V>>) {
    putAll(HashTypedMap(entries))
}

inline operator fun MutableTypedMap.minusAssign(key: KClass<*>) {
    remove(key)
}

inline operator fun <V : Any> MutableTypedMap.minusAssign(entry: TypedMapEntryPair<V>) {
    remove(entry.type, entry.value)
}

inline fun <V : Any> MutableTypedMap.compute(key: KClass<V>, function: (KClass<V>, V?) -> V?): V? {
    val result = function(key, get(key))
    if (result === null) {
        remove(key)
    } else {
        put(key, result)
    }
    return result
}

inline fun <V : Any> MutableTypedMap.computeIfAbsent(key: KClass<V>, function: (KClass<V>) -> V): V {
    return get(key) ?: function(key).also { put(key, it) }
}

inline fun <V : Any> MutableTypedMap.computeIfPresent(key: KClass<V>, function: (KClass<V>, V) -> V?): V? {
    val last = get(key)
    return if (last === null) {
        null
    } else {
        val remapped = function(key, last)
        if (remapped === null) {
            remove(key)
        } else {
            put(key, remapped)
        }
        remapped
    }
}

inline fun <V : Any> MutableTypedMap.merge(key: KClass<V>, value: V, remap: (V, V) -> V?): V? {
    val last = get(key)
    return if (last === null) {
        put(key, value)
        value
    } else {
        val remapped = remap(last, value)
        if (remapped === null) {
            remove(key)
        } else {
            put(key, remapped)
        }
        remapped
    }
}

inline fun mutableTypedMapOf(vararg entries: TypedMapEntryPair<*>): MutableTypedMap = HashTypedMap(entries.toList())
