@file:OptIn(ExperimentalContracts::class)

package dev.necron.uktil.generic

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

inline fun <reified T> Any?.intersects(): T {
    contract {
        returns() implies (this@intersects is T)
    }

    return castTo()
}

inline infix fun <reified T> Any?.intersects(unused: T): T {
    contract {
        returns() implies (this@intersects is T)
    }

    return castTo()
}

inline infix fun <T, reified C> T.intersectsBy(function: T.() -> C): C {
    contract {
        returns() implies (this@intersectsBy is C)
    }

    return function()
}

inline fun <reified T> intersect(value: Any?): T {
    contract {
        returns() implies (value is T)
    }

    return value.castTo()
}

inline fun <reified T> intersect(value: Any?, unused: T): T {
    contract {
        returns() implies (value is T)
    }

    return value.castTo()
}

inline fun <T, reified C> T.intersectBy(value: T, function: T.() -> C): C {
    contract {
        returns() implies (this@intersectBy is C)
    }

    return function()
}
