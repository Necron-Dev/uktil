@file:Suppress("NOTHING_TO_INLINE", "OVERRIDE_BY_INLINE")

package dev.necron.uktil.accessor.refs

import dev.necron.uktil.accessor.Ref
import dev.necron.uktil.annotation.UktilInternal

data class ThreadLocalRef<T>(
    @property:UktilInternal @JvmField val holder: ThreadLocal<T>,
) : Ref<T> {
    constructor(initializer: () -> T) : this(ThreadLocal.withInitial(initializer))

    @OptIn(UktilInternal::class)
    override inline fun get(): T = holder.get()

    @OptIn(UktilInternal::class)
    override inline fun set(value: T) = holder.set(value)
}

@OptIn(UktilInternal::class)
inline fun <T> threadLocal(noinline initializer: () -> T) = ThreadLocalRef(initializer)
