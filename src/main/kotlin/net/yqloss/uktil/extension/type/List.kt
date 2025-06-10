@file:Suppress("NOTHING_TO_INLINE")

package net.yqloss.uktil.extension.type

inline infix fun <T> List<T>.equalTo(list: List<T>): Boolean {
    return this === list || (size == list.size && indices.all { this[it] == list[it] })
}

inline infix fun <T> List<T>.notEqualTo(list: List<T>) = !(this equalTo list)
