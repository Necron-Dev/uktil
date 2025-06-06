@file:Suppress("NOTHING_TO_INLINE")

package dev.necron.uktil.math

import dev.necron.uktil.annotation.UktilInternal
import dev.necron.uktil.extension.bigInt
import dev.necron.uktil.extension.double
import java.math.BigInteger

data class Fraction @UktilInternal constructor(
    @JvmField val num: BigInteger,
    @JvmField val den: BigInteger,
) {
    inline operator fun plus(other: Fraction) = (num * other.den + den * other.num) over den * other.den

    inline operator fun minus(other: Fraction) = (num * other.den - den * other.num) over den * other.den

    inline operator fun times(other: Fraction) = num * other.num over den * other.den

    inline operator fun times(other: BigInteger) = num * other over den

    inline operator fun div(other: Fraction) = num * other.den over den * other.num

    inline operator fun div(other: BigInteger) = num over den * other

    inline operator fun rem(other: Fraction) = this - (this / other).bigInt.frac

    inline operator fun unaryPlus() = this

    @OptIn(UktilInternal::class)
    inline operator fun unaryMinus() = Fraction(-num, den)

    inline operator fun compareTo(other: Fraction) = (num * other.den) compareTo (den * other.num)

    inline val bigInt get() = num / den

    inline val double get() = num.double / den.double

    companion object {
        @JvmField
        val NEG_ONE: Fraction = -1 over 1

        @JvmField
        val ZERO: Fraction = 0 over 1

        @JvmField
        val ONE: Fraction = 1 over 1

        @JvmField
        val TWO: Fraction = 2 over 1
    }
}

@OptIn(UktilInternal::class)
inline infix fun BigInteger.over(den: BigInteger): Fraction {
    val gcd = gcd(den)
    return when {
        den > BigInteger.ZERO -> Fraction(this / gcd, den / gcd)
        den < BigInteger.ZERO -> Fraction(-this / gcd, -den / gcd)
        else -> throw ArithmeticException("denominator cannot be zero")
    }
}

inline infix fun Long.over(den: Long) = bigInt over den.bigInt

inline infix fun Int.over(den: Int) = bigInt over den.bigInt

inline infix fun String.over(den: String) = BigInteger(this) over BigInteger(den)

@OptIn(UktilInternal::class)
inline val BigInteger.frac: Fraction get() = Fraction(this, BigInteger.ONE)

@OptIn(UktilInternal::class)
inline val Long.frac: Fraction get() = Fraction(this.bigInt, BigInteger.ONE)

@OptIn(UktilInternal::class)
inline val Int.frac: Fraction get() = Fraction(this.bigInt, BigInteger.ONE)
