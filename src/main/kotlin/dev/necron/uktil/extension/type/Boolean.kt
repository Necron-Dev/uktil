@file:Suppress("NOTHING_TO_INLINE")
@file:OptIn(ExperimentalContracts::class)

package dev.necron.uktil.extension.type

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

inline infix fun <R> Boolean.ifTake(supplier: () -> R): R? {
    contract {
        callsInPlace(supplier, InvocationKind.AT_MOST_ONCE)
    }

    return if (this) supplier() else null
}

inline infix fun <R> Boolean.unlessTake(supplier: () -> R): R? {
    contract {
        callsInPlace(supplier, InvocationKind.AT_MOST_ONCE)
    }

    return if (this) null else supplier()
}

inline fun Boolean.takeTrue(): Boolean? {
    contract {
        returns(true) implies (this@takeTrue)
        returns(null) implies (!this@takeTrue)
    }

    return if (this) true else null
}

inline fun Boolean.takeFalse(): Boolean? {
    contract {
        returns(null) implies (this@takeFalse)
        returns(false) implies (!this@takeFalse)
    }

    return if (this) null else false
}

inline fun Boolean.unitTrue(): Unit? {
    contract {
        returnsNotNull() implies (this@unitTrue)
        returns(null) implies (!this@unitTrue)
    }

    return if (this) Unit else null
}

inline fun Boolean.unitFalse(): Unit? {
    contract {
        returns(null) implies (this@unitFalse)
        returnsNotNull() implies (!this@unitFalse)
    }

    return if (this) null else Unit
}
