@file:Suppress("NOTHING_TO_INLINE")
@file:OptIn(ExperimentalContracts::class)

package dev.necron.uktil.scope

import dev.necron.uktil.annotation.UktilInternal
import dev.necron.uktil.collection.typedmap.MutableTypedMap
import dev.necron.uktil.collection.typedmap.containsKey
import dev.necron.uktil.collection.typedmap.get
import dev.necron.uktil.collection.typedmap.mutableTypedMapOf
import dev.necron.uktil.generic.TypeCapturer
import dev.necron.uktil.generic.castTo
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.reflect.KClass

interface TypedInterface

interface TypedClass<I : TypedInterface>

inline val <reified I : TypedInterface, C : TypedClass<I>> C.typed get() = this as I

sealed interface TypedScope

@UktilInternal
class TypedImpl(@JvmField val backingTypedMap: MutableTypedMap) : TypedScope

@UktilInternal
inline val Any.implMap get() = (this as TypedImpl).backingTypedMap

@UktilInternal
inline fun <T : Any> TypedScope.put(type: KClass<T>, value: T) {
    implMap.put(type, value)
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
inline fun <reified C : Any> C.load() = implMap.get<C>()!!

@OptIn(UktilInternal::class)
inline fun <reified C : Any> TypedScope.peek() = implMap.get<C>()

@OptIn(UktilInternal::class)
inline infix fun <reified I, reified C : TypedClass<I>> TypeCapturer<C>.load(env: I) where I : Any, I : TypedInterface = env.implMap.get<I>()!! as C

@OptIn(UktilInternal::class)
inline infix fun <reified I, reified C : TypedClass<I>> TypeCapturer<C>.peek(env: I) where I : Any, I : TypedInterface = env.implMap.get<I>() as C?

@OptIn(UktilInternal::class)
inline operator fun <reified I, reified C : TypedClass<I>> TypeCapturer<C>.rangeTo(env: I) where I : Any, I : TypedInterface = env.implMap.get<I>()!! as C

@OptIn(UktilInternal::class)
inline operator fun <reified I, reified C : TypedClass<I>> TypeCapturer<C>.rangeUntil(env: I) where I : Any, I : TypedInterface = env.implMap.get<I>() as C?

@OptIn(UktilInternal::class)
inline infix fun <reified I, reified C : TypedClass<I>> I.load(capturer: TypeCapturer<C>) where I : Any, I : TypedInterface = implMap.get<I>()!! as C

@OptIn(UktilInternal::class)
inline infix fun <reified I, reified C : TypedClass<I>> I.peek(capturer: TypeCapturer<C>) where I : Any, I : TypedInterface = implMap.get<I>() as C?

@OptIn(UktilInternal::class)
inline operator fun <reified I, reified C : TypedClass<I>> I.rangeTo(capturer: TypeCapturer<C>) where I : Any, I : TypedInterface = implMap.get<I>()!! as C

@OptIn(UktilInternal::class)
inline operator fun <reified I, reified C : TypedClass<I>> I.rangeUntil(capturer: TypeCapturer<C>) where I : Any, I : TypedInterface = implMap.get<I>() as C?

@OptIn(UktilInternal::class)
inline fun <reified C : Any, reified R : C> C.loadAs() = implMap.get<C>()!! as R

@OptIn(UktilInternal::class)
inline fun <reified C : Any, reified R : C> TypedScope.peekAs() = implMap.get<C>() as R?

@OptIn(UktilInternal::class)
inline fun <reified C : Any> TypedScope.has(): Boolean {
    contract {
        returns(true) implies (this@has is C)
        returns(false) implies (this@has !is C)
    }

    return implMap.containsKey<C>()
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

    return TypedImpl(implMap).castTo<T>().function()
}
