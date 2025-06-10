@file:OptIn(ExperimentalContracts::class)

package net.yqloss.uktil.scope

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

inline fun <R> noExcept(
    function: () -> R,
): R? {
    contract {
        callsInPlace(function, InvocationKind.AT_MOST_ONCE)
    }

    return try {
        function()
    } catch (_: Exception) {
        null
    }
}

inline fun <R> noExcept(
    exceptionHandler: (Exception) -> R,
    function: () -> R,
): R {
    contract {
        callsInPlace(function, InvocationKind.AT_MOST_ONCE)
        callsInPlace(exceptionHandler, InvocationKind.AT_MOST_ONCE)
    }

    return try {
        function()
    } catch (exception: Exception) {
        exceptionHandler(exception)
    }
}

inline fun <R> noThrow(
    function: () -> R,
): R? {
    contract {
        callsInPlace(function, InvocationKind.AT_MOST_ONCE)
    }

    return try {
        function()
    } catch (exception: Throwable) {
        null
    }
}

inline fun <R> noThrow(
    throwableHandler: (Throwable) -> R,
    function: () -> R,
): R {
    contract {
        callsInPlace(function, InvocationKind.AT_MOST_ONCE)
        callsInPlace(throwableHandler, InvocationKind.AT_MOST_ONCE)
    }

    return try {
        function()
    } catch (exception: Throwable) {
        throwableHandler(exception)
    }
}
