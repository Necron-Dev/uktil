@file:Suppress("NOTHING_TO_INLINE")

package dev.necron.uktil.extension.type

inline infix fun <T> Collection<T>.prepend(element: T) = buildList(this.size + 1) {
    add(element)
    addAll(this@prepend)
}

inline infix fun <T> T.prependTo(collection: Collection<T>) = buildList(collection.size + 1) {
    add(this@prependTo)
    addAll(collection)
}
