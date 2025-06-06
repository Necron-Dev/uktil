package dev.necron.uktil.algorithm.hash

import java.util.*

class UniqueHash<in T> : (T) -> Long {
    private var counter = 0L

    private val uniqueHash = WeakHashMap<Any, Long>()

    override fun invoke(obj: T): Long {
        return obj?.let {
            uniqueHash.getOrPut(obj) { ++counter }
        } ?: 0L
    }
}
