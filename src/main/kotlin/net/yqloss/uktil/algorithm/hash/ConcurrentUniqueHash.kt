package net.yqloss.uktil.algorithm.hash

import java.util.*
import java.util.concurrent.atomic.AtomicLong

class ConcurrentUniqueHash<in T> : (T) -> Long {
    private val counter = AtomicLong(0L)

    private val uniqueHash = WeakHashMap<Any, Long>()

    override fun invoke(obj: T): Long {
        return obj?.let {
            synchronized(this) {
                uniqueHash.getOrPut(obj) { counter.addAndGet(1L) }
            }
        } ?: 0L
    }
}
