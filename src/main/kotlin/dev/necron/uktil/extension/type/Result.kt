package dev.necron.uktil.extension.type

import dev.necron.uktil.accessor.outs.inBox

inline val <T> Result<T>.boxedOrNull get() = if (isSuccess) getOrThrow().inBox else null

inline val <T> T.asSuccess get() = Result.success(this)

inline val Throwable.asFailure get() = Result.failure<Nothing>(this)
