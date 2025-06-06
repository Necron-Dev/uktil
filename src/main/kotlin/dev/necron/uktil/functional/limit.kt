package dev.necron.uktil.functional

import dev.necron.uktil.accessor.Out
import dev.necron.uktil.accessor.outs.inBox
import dev.necron.uktil.accessor.value

inline fun <R> limit(
    count: Int,
    crossinline function: () -> R,
): () -> R {
    var remaining = count
    lateinit var result: Out<R>
    return {
        if (remaining > 0) {
            --remaining
            result = function().inBox
        }
        result.value
    }
}

inline fun <R> once(crossinline function: () -> R) = limit(1, function)

inline fun <T, R> limitUnary(
    count: Int,
    crossinline function: (T) -> R,
): (T) -> R {
    var remaining = count
    lateinit var result: Out<R>
    return {
        if (remaining > 0) {
            --remaining
            result = function(it).inBox
        }
        result.value
    }
}

inline fun <T, R> onceUnary(crossinline function: (T) -> R) = limitUnary(1, function)
