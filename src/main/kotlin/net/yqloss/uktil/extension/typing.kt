@file:Suppress("NOTHING_TO_INLINE")
@file:OptIn(ExperimentalContracts::class)

package net.yqloss.uktil.extension

import java.math.BigInteger
import kotlin.contracts.ExperimentalContracts
import kotlin.math.ceil
import kotlin.math.floor

inline val Number.byte get() = toByte()
inline val Number.short get() = toShort()
inline val Number.int get() = toInt()
inline val Number.long get() = toLong()
inline val Number.float get() = toFloat()
inline val Number.double get() = toDouble()
inline val Number.floorInt get() = floor(toDouble()).toInt()
inline val Number.ceilInt get() = ceil(toDouble()).toInt()
inline val Long.bigInt: BigInteger get() = BigInteger.valueOf(this)
inline val Int.bigInt: BigInteger get() = BigInteger.valueOf(this.long)
