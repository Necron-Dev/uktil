@file:Suppress("NOTHING_TO_INLINE")

package net.yqloss.uktil.algorithm.partialorder

import net.yqloss.uktil.algorithm.hash.HashComparator
import net.yqloss.uktil.algorithm.hash.UniqueHash
import net.yqloss.uktil.annotation.UktilInternal
import net.yqloss.uktil.generic.castTo

typealias PartialComparator<T> = (T, T) -> Int

@UktilInternal
val ASCENDING_PARTIAL_COMPARATOR: PartialComparator<PartialComparable<Any?>> =
    { a, b -> a.partialCompareTo(b.castTo()) }

@UktilInternal
val DESCENDING_PARTIAL_COMPARATOR: PartialComparator<PartialComparable<Any?>> =
    { a, b -> b.partialCompareTo(a.castTo()) }

@OptIn(UktilInternal::class)
inline fun <T : PartialComparable<T>> ascendingPartialComparator(): PartialComparator<T> = ASCENDING_PARTIAL_COMPARATOR.castTo()

@OptIn(UktilInternal::class)
inline fun <T : PartialComparable<T>> descendingPartialComparator(): PartialComparator<T> = DESCENDING_PARTIAL_COMPARATOR.castTo()

inline fun <T> PartialComparator<T>.toAscendingComparator(noinline hash: (T) -> Long = UniqueHash()): Comparator<T> {
    val comparator = HashComparator(hash)
    return Comparator { a, b ->
        val result = this(a, b)
        result != 0 && return@Comparator result
        comparator.compare(a, b)
    }
}

inline fun <T> PartialComparator<T>.toDescendingComparator(noinline hash: (T) -> Long = UniqueHash()): Comparator<T> {
    val comparator = HashComparator(hash)
    return Comparator { a, b ->
        val result = this(b, a)
        result != 0 && return@Comparator result
        comparator.compare(b, a)
    }
}
