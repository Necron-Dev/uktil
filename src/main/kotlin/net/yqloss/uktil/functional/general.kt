@file:OptIn(ExperimentalContracts::class)

package net.yqloss.uktil.functional

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

inline fun <T> T.exec(action: () -> Unit) {
    contract {
        callsInPlace(action, InvocationKind.EXACTLY_ONCE)
    }

    action()
}

inline fun exec(action: () -> Unit) {
    contract {
        callsInPlace(action, InvocationKind.EXACTLY_ONCE)
    }

    action()
}

inline fun <T, R> T.eval(action: () -> R): R {
    contract {
        callsInPlace(action, InvocationKind.EXACTLY_ONCE)
    }

    return action()
}

inline fun <R> eval(action: () -> R): R {
    contract {
        callsInPlace(action, InvocationKind.EXACTLY_ONCE)
    }

    return action()
}
