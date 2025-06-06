@file:Suppress("NOTHING_TO_INLINE")

package dev.necron.uktil.algorithm.partialorder

interface PartialComparable<in T> {
    infix fun partialCompareTo(other: T): Int
}

inline val <T> Comparable<T>.asPartialComparable
    get() = object : PartialComparable<T> {
        override fun partialCompareTo(other: T) = compareTo(other)
    }
