@file:Suppress("NOTHING_TO_INLINE")
@file:OptIn(ExperimentalContracts::class)

package dev.necron.uktil.extension

import dev.necron.uktil.functional.eval
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

inline fun <T> T.invertNull(): Unit? {
    contract {
        returnsNotNull() implies (this@invertNull === null)
        returns(null) implies (this@invertNull !== null)
    }

    return if (this === null) Unit else null
}

inline infix fun <T1, T2> T1.sameNotNull(other: T2): Boolean {
    contract {
        returns(true) implies (this@sameNotNull !== null && other !== null)
        returns(false) implies (this@sameNotNull === null || other === null)
    }

    return this !== null && this === other
}

inline fun <T> T?.isNull(): Boolean {
    contract {
        returns(true) implies (this@isNull === null)
        returns(false) implies (this@isNull !== null)
    }

    return this === null
}

inline fun <T> T?.notNull(): Boolean {
    contract {
        returns(true) implies (this@notNull !== null)
        returns(false) implies (this@notNull === null)
    }

    return this !== null
}

inline infix fun <T> T?.or(function: () -> T): T {
    contract {
        callsInPlace(function, InvocationKind.AT_MOST_ONCE)
    }

    return this ?: function()
}

inline infix fun <I, T> I?.and(function: () -> T): T? {
    contract {
        callsInPlace(function, InvocationKind.AT_MOST_ONCE)
    }

    return this?.eval(function)
}
