@file:Suppress("NOTHING_TO_INLINE")

package dev.necron.uktil.generic

import dev.necron.uktil.annotation.UktilInternal

sealed interface TypeCapturer<T>

private object TypeCapturerImpl : TypeCapturer<Any?>

@UktilInternal
@JvmField
val TYPE_CAPTURER: TypeCapturer<Any?> = TypeCapturerImpl

@OptIn(UktilInternal::class)
inline fun <T> capture(): TypeCapturer<T> = TYPE_CAPTURER.castTo()
