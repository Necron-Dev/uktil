@file:OptIn(ExperimentalContracts::class)

package net.yqloss.uktil.controlflow

import net.yqloss.uktil.accessor.Out
import net.yqloss.uktil.accessor.value
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

inline fun loop(function: () -> Unit): Nothing {
    contract {
        callsInPlace(function, InvocationKind.UNKNOWN)
    }

    while (true) {
        function()
    }
}

inline fun <R> loop(function: () -> Out<R>?): R {
    contract {
        callsInPlace(function, InvocationKind.AT_LEAST_ONCE)
    }

    while (true) {
        function()?.let { return it.value }
    }
}
