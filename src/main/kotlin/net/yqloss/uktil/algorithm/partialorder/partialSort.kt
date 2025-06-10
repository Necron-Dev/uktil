@file:Suppress("NOTHING_TO_INLINE")

package net.yqloss.uktil.algorithm.partialorder

inline fun <T, V> partialSorted(values: List<V>, ordering: List<T>, comparator: (T, T) -> Int): List<V> {
    val size = values.size

    size == ordering.size || throw IllegalArgumentException("values and ordering have different sizes")

    val lessCount = IntArray(size)
    val moreGraph = arrayOfNulls<MutableList<Int>>(size)

    ordering.forEachIndexed { i1, v1 ->
        (i1 + 1..<size).forEach { i2 ->
            val result = comparator(v1, ordering[i2])
            result == 0 && return@forEach
            val less: Int
            val more: Int
            if (result < 0) {
                less = i1
                more = i2
            } else {
                less = i2
                more = i1
            }
            ++lessCount[more]
            moreGraph[less] ?: ArrayList<Int>(size).also { moreGraph[less] = it } += more
        }
    }

    val sorted = ArrayList<V>(size)
    val queue = ArrayDeque<Int>(size)

    lessCount.forEachIndexed { i, count -> if (count == 0) queue += i }

    while (queue.isNotEmpty()) {
        val current = queue.removeFirst()
        sorted += values[current]
        moreGraph[current]?.forEach { if (--lessCount[it] == 0) queue += it }
    }

    sorted.size == size || throw IllegalArgumentException("comparison method violates its general contract")

    return sorted
}

inline fun <T> List<T>.partialSorted(crossinline comparator: (T, T) -> Int) = partialSorted(
    this,
    this,
) { a, b -> comparator(a, b) }

inline fun <T> List<T>.partialSortedDescending(crossinline comparator: (T, T) -> Int) = partialSorted(
    this,
    this,
) { a, b -> comparator(b, a) }

inline fun <T : PartialComparable<T>> List<T>.partialSorted() = partialSorted(
    this,
    this,
) { a, b -> a partialCompareTo b }

inline fun <T : PartialComparable<T>> List<T>.partialSortedDescending() = partialSorted(
    this,
    this,
) { a, b -> b partialCompareTo a }

inline fun <T : Comparable<T>> List<T>.partialSortedComparable() = partialSorted(
    this,
    this,
) { a, b -> a compareTo b }

inline fun <T : Comparable<T>> List<T>.partialSortedComparableDescending() = partialSorted(
    this,
    this,
) { a, b -> b compareTo a }

inline fun <T, K : PartialComparable<K>> List<T>.partialSortedBy(selector: (T) -> K) = partialSorted(
    this,
    this.map(selector),
) { a, b -> a partialCompareTo b }

inline fun <T, K : PartialComparable<K>> List<T>.partialSortedByDescending(selector: (T) -> K) = partialSorted(
    this,
    this.map(selector),
) { a, b -> b partialCompareTo a }

inline fun <T, K : Comparable<K>> List<T>.partialSortedByComparable(selector: (T) -> K) = partialSorted(
    this,
    this.map(selector),
) { a, b -> a compareTo b }

inline fun <T, K : Comparable<K>> List<T>.partialSortedByComparableDescending(selector: (T) -> K) = partialSorted(
    this,
    this.map(selector),
) { a, b -> b compareTo a }

inline fun <T> Iterable<T>.partialSorted(crossinline comparator: (T, T) -> Int) = toList().partialSorted(comparator)

inline fun <T> Iterable<T>.partialSortedDescending(crossinline comparator: (T, T) -> Int) = toList().partialSortedDescending(comparator)

inline fun <T : PartialComparable<T>> Iterable<T>.partialSorted() = toList().partialSorted()

inline fun <T : PartialComparable<T>> Iterable<T>.partialSortedDescending() = toList().partialSortedDescending()

inline fun <T : Comparable<T>> Iterable<T>.partialSortedComparable() = toList().partialSortedComparable()

inline fun <T : Comparable<T>> Iterable<T>.partialSortedComparableDescending() = toList().partialSortedComparableDescending()

inline fun <T, K : PartialComparable<K>> Iterable<T>.partialSortedBy(selector: (T) -> K) = toList().partialSortedBy(selector)

inline fun <T, K : PartialComparable<K>> Iterable<T>.partialSortedByDescending(selector: (T) -> K) = toList().partialSortedByDescending(selector)

inline fun <T, K : Comparable<K>> Iterable<T>.partialSortedByComparable(selector: (T) -> K) = toList().partialSortedByComparable(selector)

inline fun <T, K : Comparable<K>> Iterable<T>.partialSortedByComparableDescending(selector: (T) -> K) = toList().partialSortedByComparableDescending(selector)
