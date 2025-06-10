@file:Suppress("NOTHING_TO_INLINE")

package net.yqloss.uktil.functional

import net.yqloss.uktil.annotation.UktilInternal
import net.yqloss.uktil.generic.castTo

interface Transformer<in B> {
    operator fun <T : B> invoke(value: T): T
}

@UktilInternal
@JvmField
val NO_TRANSFORMER = object : Transformer<Any?> {
    override fun <T> invoke(value: T) = value
}

@OptIn(UktilInternal::class)
inline fun <B> noTransformer(): Transformer<B> = NO_TRANSFORMER.castTo()

inline operator fun <B> Transformer<B>.div(other: Transformer<B>) = object : Transformer<B> {
    override fun <T : B> invoke(value: T): T = other(this@div(value))
}

interface UnaryTransformer<in BA, in BR> {
    operator fun <A : BA, R : BR> invoke(value: (A) -> R): (A) -> R
}

@UktilInternal
@JvmField
val NO_UNARY_TRANSFORMER = object : UnaryTransformer<Any?, Any?> {
    override fun <A, R> invoke(value: (A) -> R): (A) -> R = value
}

@OptIn(UktilInternal::class)
inline fun <BA, BR> noUnaryTransformer(): UnaryTransformer<BA, BR> = NO_UNARY_TRANSFORMER.castTo()

inline operator fun <BA, BR> UnaryTransformer<BA, BR>.div(other: UnaryTransformer<BA, BR>) = object : UnaryTransformer<BA, BR> {
    override fun <A : BA, R : BR> invoke(value: (A) -> R) = other(this@div(value))
}
