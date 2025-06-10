package net.yqloss.uktil.algorithm.hash

class HashComparator<in T>(
    private val hash: (T) -> Long,
) : Comparator<@UnsafeVariance T> {
    override fun compare(
        a: T,
        b: T,
    ) = hash(a) compareTo hash(b)
}
