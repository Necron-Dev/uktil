@file:Suppress("NOTHING_TO_INLINE")
@file:OptIn(ExperimentalContracts::class)

package dev.necron.uktil.scope

import dev.necron.uktil.accessor.outs.Box
import dev.necron.uktil.accessor.outs.inBox
import dev.necron.uktil.annotation.UktilDebug
import dev.necron.uktil.annotation.UktilInternal
import dev.necron.uktil.controlflow.loop
import dev.necron.uktil.extension.type.asFailure
import dev.necron.uktil.extension.type.asSuccess
import dev.necron.uktil.extension.type.boxedOrNull
import dev.necron.uktil.generic.castTo
import dev.necron.uktil.longReturnStacktrace
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

sealed interface Scope {
    class FrameIdentifier

    @JvmInline
    value class Count(
        @JvmField val count: Int,
    ) : Scope

    @JvmInline
    value class Frame(
        @JvmField val identifier: FrameIdentifier = FrameIdentifier(),
    ) : Scope

    companion object {
        @JvmField
        val LAST = Count(1)
    }
}

@UktilInternal
data class LongResult(
    @JvmField val scope: Scope,
    @JvmField val result: Result<Any?>,
) : Throwable() {
    inline val next
        get() = when (scope) {
            is Scope.Count -> LongResult(Scope.Count(scope.count - 1), result)
            is Scope.Frame -> this
        }

    @OptIn(UktilDebug::class)
    override fun fillInStackTrace(): Throwable = if (longReturnStacktrace) super.fillInStackTrace() else this
}

@JvmInline
value class LongResultContext(
    @JvmField val result: Result<*>,
) {

    inline fun <T> value() = result.getOrThrow().castTo<T>()

    inline fun <T> valueOrNull() = result.getOrNull().castTo<T?>()

    inline fun <reified T> valueChecked() = result.getOrThrow() as T

    inline fun <reified T> valueCheckedOrNull() = result.getOrNull() as? T

    inline fun <T> boxed() = value<T>().inBox

    inline fun <T> boxedOrNull() = result.boxedOrNull.castTo<Box<T>?>()

    inline fun <reified T> boxedChecked() = result.getOrThrow().inBox.castTo<Box<T>>()

    inline fun <reified T> boxedCheckedOrNull() = result.boxedOrNull.castTo<Box<T>?>()
}

@OptIn(UktilInternal::class)
inline fun longResult(
    scope: Scope = Scope.LAST,
    getter: () -> Result<*>,
): Nothing {
    contract {
        callsInPlace(getter, InvocationKind.EXACTLY_ONCE)
    }

    throw LongResult(scope, getter())
}

inline fun <T> longReturn(
    scope: Scope = Scope.LAST,
    getter: () -> T,
): Nothing {
    contract {
        callsInPlace(getter, InvocationKind.EXACTLY_ONCE)
    }

    longResult(scope) { getter().asSuccess }
}

inline fun longThrow(
    scope: Scope = Scope.LAST,
    getter: () -> Throwable,
): Nothing {
    contract {
        callsInPlace(getter, InvocationKind.EXACTLY_ONCE)
    }

    longResult(scope) { getter().asFailure }
}

@OptIn(UktilInternal::class)
inline fun <R> longScope(
    onResult: LongResultContext.() -> Nothing,
    function: (Scope.Frame) -> R,
): R {
    contract {
        callsInPlace(function, InvocationKind.EXACTLY_ONCE)
        callsInPlace(onResult, InvocationKind.AT_MOST_ONCE)
    }

    val frame = Scope.Frame()
    return try {
        function(frame)
    } catch (longResult: LongResult) {
        val scope = longResult.scope
        when {
            scope is Scope.Count && scope.count > 1 -> throw longResult.next
            scope is Scope.Frame && scope.identifier === frame.identifier -> throw longResult.next
        }
        onResult(LongResultContext(longResult.result))
    }
}

@OptIn(UktilInternal::class)
inline fun longScope(function: (Scope.Frame) -> Nothing): Result<*> {
    contract {
        callsInPlace(function, InvocationKind.AT_MOST_ONCE)
    }

    val frame = Scope.Frame()
    return try {
        function(frame)
    } catch (longResult: LongResult) {
        val scope = longResult.scope
        when {
            scope is Scope.Count && scope.count > 1 -> throw longResult.next
            scope is Scope.Frame && scope.identifier === frame.identifier -> throw longResult.next
        }
        LongResultContext(longResult.result).result
    }
}

inline fun <R> longRun(function: (Scope.Frame) -> Nothing): R {
    contract {
        callsInPlace(function, InvocationKind.AT_MOST_ONCE)
    }

    longScope({ return value() }) {
        function(it)
    }
}

inline fun <R> longLoop(function: (Scope.Frame) -> Unit): R {
    contract {
        callsInPlace(function, InvocationKind.AT_MOST_ONCE)
    }

    return longRun {
        loop {
            function(it)
        }
    }
}
