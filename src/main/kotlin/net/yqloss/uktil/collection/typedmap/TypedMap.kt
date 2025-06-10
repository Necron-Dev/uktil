@file:Suppress("NOTHING_TO_INLINE")

package net.yqloss.uktil.collection.typedmap

import net.yqloss.uktil.extension.type.ifTake
import net.yqloss.uktil.functional.Transformer
import net.yqloss.uktil.generic.castTo
import kotlin.reflect.KClass

interface TypedMap : Iterable<TypedMap.Entry<*>> {
    val size: Int

    fun isEmpty(): Boolean

    fun containsKey(key: KClass<*>): Boolean

    fun containsValue(value: Any): Boolean

    operator fun <V : Any> get(key: KClass<V>): V?

    val keys: Set<KClass<*>>

    val values: Collection<Any>

    val entries: Set<Entry<*>>

    val map: Map<KClass<*>, Any>

    interface Entry<T : Any> {
        val type: KClass<T>

        val value: T
    }
}

data class TypedMapEntryPair<T : Any>(
    val type: KClass<T>,
    val value: T,
)

inline fun <reified V : Any> TypedMap.get() = get(V::class)

inline fun <reified V : Any> TypedMap.containsKey() = containsKey(V::class)

inline operator fun TypedMap.contains(key: KClass<*>) = containsKey(key)

inline infix fun <T : Any> KClass<T>.typedTo(value: T) = TypedMapEntryPair(this, value)

inline fun TypedMap.toMutableTypedMap(): MutableTypedMap = HashTypedMap(this)

inline fun TypedMap.toTypedMap(): TypedMap = toMutableTypedMap()

inline fun <reified T : Any> TypedMap.Entry<*>.typed(): TypedMap.Entry<T>? = (type === T::class).ifTake(::castTo)

inline val <T : Any> TypedMap.Entry<T>.asPair: TypedMapEntryPair<T> get() = type typedTo value

inline operator fun <T : Any> TypedMap.Entry<T>.component1() = type

inline operator fun <T : Any> TypedMap.Entry<T>.component2() = value

inline operator fun <T : Any> TypedMap.Entry<T>.component3() = anyValue

inline val TypedMap.Entry<*>.anyValue: Any get() = value

inline fun TypedMap.isNotEmpty() = !isEmpty()

inline fun <V : Any> TypedMap.getOrDefault(key: KClass<V>, defaultValue: V) = get(key) ?: defaultValue

inline fun <V : Any> TypedMap.getOrElse(key: KClass<V>, defaultValue: () -> V) = get(key) ?: defaultValue()

inline fun <V : Any> TypedMap.getValue(key: KClass<V>) = get(key)!!

inline fun TypedMap.mapValues(transformer: Transformer<Any>) = HashTypedMap(
    entries.map { (a, b) ->
        a.castTo<KClass<Any>>() typedTo transformer(b)
    },
)

inline fun TypedMap.filterKeys(predicate: (KClass<*>) -> Boolean) = HashTypedMap(
    entries.filter {
        predicate(it.type)
    }.map { it.asPair },
)

inline fun TypedMap.filterValues(predicate: (Any) -> Boolean) = HashTypedMap(
    entries.filter {
        predicate(it.value)
    }.map { it.asPair },
)

inline fun TypedMap.filter(predicate: (TypedMap.Entry<*>) -> Boolean) = HashTypedMap(
    entries.filter(predicate).map { it.asPair },
)

inline fun TypedMap.count(predicate: (TypedMap.Entry<*>) -> Boolean) = entries.count(predicate)

inline operator fun TypedMap.plus(typedMap: TypedMap): TypedMap = toMutableTypedMap().apply { plusAssign(typedMap) }

inline operator fun <V : Any> TypedMap.plus(entry: TypedMapEntryPair<V>): TypedMap = toMutableTypedMap().apply { plusAssign(entry) }

inline operator fun <V : Any> TypedMap.plus(entry: TypedMap.Entry<V>): TypedMap = toMutableTypedMap().apply { plusAssign(entry) }

inline operator fun <V : Any> TypedMap.plus(entries: Iterable<TypedMapEntryPair<V>>): TypedMap = toMutableTypedMap().apply { plusAssign(entries) }

inline operator fun <V : Any> TypedMap.plus(entries: Collection<TypedMapEntryPair<V>>): TypedMap = toMutableTypedMap().apply { plusAssign(entries) }

inline fun typedMapOf(vararg entries: TypedMapEntryPair<*>): TypedMap = HashTypedMap(entries.toList())
