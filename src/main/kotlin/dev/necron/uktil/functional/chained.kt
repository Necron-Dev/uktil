@file:Suppress("NOTHING_TO_INLINE")

package dev.necron.uktil.functional

import dev.necron.uktil.annotation.UktilInternal
import dev.necron.uktil.generic.castTo

@UktilInternal
data class ChainedFunctionHolder(
    @property:UktilInternal @JvmField val functions: List<(Any?) -> Any?>,
) : () -> Any?,
    (Any?) -> Any? {
    @UktilInternal
    override fun invoke(): Any? {
        var result: Any? = Unit
        functions.forEach { result = it(result) }
        return result
    }

    @UktilInternal
    override fun invoke(arg: Any?): Any? {
        var result: Any? = arg
        functions.forEach { result = it(result) }
        return result
    }

    @UktilInternal
    inline fun append(noinline function: (Any?) -> Any?): ChainedFunctionHolder {
        return if (function is ChainedFunctionHolder) {
            ChainedFunctionHolder(functions + function.functions)
        } else {
            ChainedFunctionHolder(functions + function)
        }
    }

    @UktilInternal
    inline fun append(noinline function: () -> Any?): ChainedFunctionHolder {
        return if (function is ChainedFunctionHolder) {
            ChainedFunctionHolder(functions + function.functions)
        } else {
            ChainedFunctionHolder(functions + { function() })
        }
    }
}

@OptIn(UktilInternal::class)
inline operator fun <R1, R2> (() -> R1).div(noinline other: (R1) -> R2): () -> R2 {
    return (
        this as? ChainedFunctionHolder
            ?: ChainedFunctionHolder(listOf { this() })
        ).append(other.castTo<(Any?) -> Any?>()).castTo()
}

@OptIn(UktilInternal::class)
inline operator fun <T, R1, R2> ((T) -> R1).div(noinline other: (R1) -> R2): (T) -> R2 {
    return (
        this as? ChainedFunctionHolder
            ?: ChainedFunctionHolder(listOf(this).castTo())
        ).append(other.castTo<(Any?) -> Any?>()).castTo()
}
