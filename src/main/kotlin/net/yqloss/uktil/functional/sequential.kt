@file:Suppress("NOTHING_TO_INLINE")

package net.yqloss.uktil.functional

import net.yqloss.uktil.annotation.UktilInternal
import net.yqloss.uktil.generic.castTo

@UktilInternal
data class SequentialFunctionHolder(
    @property:UktilInternal @JvmField val functions: List<(Any?) -> Any?>,
) : () -> Any?,
    (Any?) -> Any? {
    @UktilInternal
    override fun invoke(): Any? {
        var result: Any? = Unit
        functions.forEach { result = it(Unit) }
        return result
    }

    @UktilInternal
    override fun invoke(arg: Any?): Any? {
        var result: Any? = Unit
        functions.forEach { result = it(arg) }
        return result
    }

    @UktilInternal
    inline fun append(noinline function: (Any?) -> Any?): SequentialFunctionHolder {
        return if (function is SequentialFunctionHolder) {
            SequentialFunctionHolder(functions + function.functions)
        } else {
            SequentialFunctionHolder(functions + function)
        }
    }

    @UktilInternal
    inline fun append(noinline function: () -> Any?): SequentialFunctionHolder {
        return if (function is SequentialFunctionHolder) {
            SequentialFunctionHolder(functions + function.functions)
        } else {
            SequentialFunctionHolder(functions + { function() })
        }
    }
}

@OptIn(UktilInternal::class)
inline operator fun <R1, R2> (() -> R1)?.plus(noinline other: () -> R2): () -> R2 {
    return (
        this as? SequentialFunctionHolder
            ?: SequentialFunctionHolder(listOfNotNull(this?.let { { this() } }))
        ).append(other).castTo()
}

@OptIn(UktilInternal::class)
inline operator fun <T, R1, R2> ((T) -> R1)?.plus(noinline other: (T) -> R2): (T) -> R2 {
    return (
        this as? SequentialFunctionHolder
            ?: SequentialFunctionHolder(listOfNotNull(this).castTo())
        ).append(other.castTo<(Any?) -> Any?>()).castTo()
}
