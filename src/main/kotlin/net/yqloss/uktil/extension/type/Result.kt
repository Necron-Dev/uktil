package net.yqloss.uktil.extension.type

import net.yqloss.uktil.accessor.outs.inBox

inline val <T> Result<T>.boxedOrNull get() = if (isSuccess) getOrThrow().inBox else null

inline val <T> T.asSuccess get() = Result.success(this)

inline val Throwable.asFailure get() = Result.failure<Nothing>(this)
