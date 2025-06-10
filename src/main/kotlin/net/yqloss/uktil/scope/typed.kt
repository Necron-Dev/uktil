@file:Suppress("NOTHING_TO_INLINE")
@file:OptIn(ExperimentalContracts::class)

package net.yqloss.uktil.scope

import net.yqloss.uktil.annotation.UktilInternal
import net.yqloss.uktil.collection.typedmap.MutableTypedMap
import net.yqloss.uktil.collection.typedmap.containsKey
import net.yqloss.uktil.collection.typedmap.get
import net.yqloss.uktil.collection.typedmap.mutableTypedMapOf
import net.yqloss.uktil.generic.TypeCapturer
import net.yqloss.uktil.generic.castTo
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.reflect.KClass

interface TypedInterface

interface TypedClass<I : TypedInterface>

inline val <reified I : TypedInterface, C : TypedClass<I>> C.typed get() = this as I

sealed interface TypedScope

@UktilInternal
class TypedImpl(@JvmField val internalMap: MutableTypedMap) : TypedScope

@UktilInternal
inline val Any.internalMap get() = (this as TypedImpl).internalMap

@UktilInternal
inline fun <T : Any> TypedScope.put(type: KClass<T>, value: T) {
    internalMap.put(type, value)
}

@OptIn(UktilInternal::class)
inline infix fun <reified T : Any> TypedScope.it(value: T): T {
    contract {
        returns() implies (this@it is T)
    }

    put(T::class, value)

    return value
}

@OptIn(UktilInternal::class)
inline infix fun <reified I : TypedInterface, reified C : TypedClass<I>> TypedScope.it(value: C): C {
    contract {
        returns() implies (this@it is I)
    }

    put(I::class, value as I)

    return value
}

@OptIn(UktilInternal::class)
inline fun <reified C : Any, T : TypedScope> T.store(function: T.() -> C): C {
    contract {
        callsInPlace(function, InvocationKind.EXACTLY_ONCE)
        returns() implies (this@store is C)
    }

    val value = function()
    put(C::class, value)

    return value
}

@OptIn(UktilInternal::class)
inline fun <reified I : TypedInterface, C : TypedClass<I>, T : TypedScope> T.store(function: T.() -> C): C {
    contract {
        callsInPlace(function, InvocationKind.EXACTLY_ONCE)
        returns() implies (this@store is I)
    }

    val value = function()
    put(I::class, value as I)

    return value
}

@OptIn(UktilInternal::class)
inline fun <reified C : Any> C.load() = internalMap.get<C>()!!

@OptIn(UktilInternal::class)
inline fun <reified C : Any> TypedScope.peek() = internalMap.get<C>()

@OptIn(UktilInternal::class)
inline infix fun <reified I, reified C : TypedClass<I>> TypeCapturer<C>.load(env: I) where I : Any, I : TypedInterface = env.internalMap.get<I>()!! as C

@OptIn(UktilInternal::class)
inline infix fun <reified I, reified C : TypedClass<I>> TypeCapturer<C>.peek(env: I) where I : Any, I : TypedInterface = env.internalMap.get<I>() as C?

@OptIn(UktilInternal::class)
inline operator fun <reified I, reified C : TypedClass<I>> TypeCapturer<C>.rangeTo(env: I) where I : Any, I : TypedInterface = env.internalMap.get<I>()!! as C

@OptIn(UktilInternal::class)
inline operator fun <reified I, reified C : TypedClass<I>> TypeCapturer<C>.rangeUntil(env: I) where I : Any, I : TypedInterface = env.internalMap.get<I>() as C?

@OptIn(UktilInternal::class)
inline infix fun <reified I, reified C : TypedClass<I>> I.load(capturer: TypeCapturer<C>) where I : Any, I : TypedInterface = internalMap.get<I>()!! as C

@OptIn(UktilInternal::class)
inline infix fun <reified I, reified C : TypedClass<I>> I.peek(capturer: TypeCapturer<C>) where I : Any, I : TypedInterface = internalMap.get<I>() as C?

@OptIn(UktilInternal::class)
inline operator fun <reified I, reified C : TypedClass<I>> I.rangeTo(capturer: TypeCapturer<C>) where I : Any, I : TypedInterface = internalMap.get<I>()!! as C

@OptIn(UktilInternal::class)
inline operator fun <reified I, reified C : TypedClass<I>> I.rangeUntil(capturer: TypeCapturer<C>) where I : Any, I : TypedInterface = internalMap.get<I>() as C?

@OptIn(UktilInternal::class)
inline fun <reified C : Any, reified R : C> C.loadAs() = internalMap.get<C>()!! as R

@OptIn(UktilInternal::class)
inline fun <reified C : Any, reified R : C> TypedScope.peekAs() = internalMap.get<C>() as R?

@OptIn(UktilInternal::class)
inline fun <reified C : Any> TypedScope.has(): Boolean {
    contract {
        returns(true) implies (this@has is C)
        returns(false) implies (this@has !is C)
    }

    return internalMap.containsKey<C>()
}

@OptIn(UktilInternal::class)
inline fun <R> typedScope(function: TypedScope.() -> R): R {
    contract {
        callsInPlace(function, InvocationKind.EXACTLY_ONCE)
    }

    return TypedImpl(mutableTypedMapOf()).function()
}

@OptIn(UktilInternal::class)
inline infix fun <T : TypedScope, R> T.subTypedScope(function: T.() -> R): R {
    contract {
        callsInPlace(function, InvocationKind.EXACTLY_ONCE)
    }

    return TypedImpl(internalMap).castTo<T>().function()
}
