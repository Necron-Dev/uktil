@file:Suppress("NOTHING_TO_INLINE", "OVERRIDE_BY_INLINE")

package dev.necron.uktil.accessor.refs

import dev.necron.uktil.accessor.Ref
import dev.necron.uktil.accessor.value
import dev.necron.uktil.annotation.UktilInternal

data class LateVar<T>(
    @property:UktilInternal @JvmField var initializer: (T) -> Ref<T>,
    @property:UktilInternal @JvmField var wrapped: Ref<T>? = null,
) : Ref<T> {
    @OptIn(UktilInternal::class)
    override inline fun get() = (wrapped ?: throw IllegalStateException("get value before initialization")).value

    @OptIn(UktilInternal::class)
    override inline fun set(value: T) {
        val wrapped = wrapped
        if (wrapped === null) {
            this.wrapped = initializer(value)
            initializer = throwCallInitializerAfterInitialization
        } else {
            wrapped.set(value)
        }
    }

    companion object {
        @UktilInternal
        @JvmField
        val throwCallInitializerAfterInitialization: (Any?) -> Nothing = {
            throw IllegalStateException("call initializer after initialization")
        }
    }
}

@OptIn(UktilInternal::class)
inline fun <T> lateVar(noinline initializer: (T) -> Ref<T> = { it.inMut }) = LateVar(initializer)
