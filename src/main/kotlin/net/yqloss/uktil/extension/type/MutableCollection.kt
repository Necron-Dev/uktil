@file:OptIn(ExperimentalContracts::class)

package net.yqloss.uktil.extension.type

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

inline fun <T, C : MutableCollection<T>> C.replace(function: C.() -> Collection<T>): C {
    contract {
        callsInPlace(function, InvocationKind.EXACTLY_ONCE)
    }

    return this.apply {
        val result = function()
        clear()
        addAll(result)
    }
}
