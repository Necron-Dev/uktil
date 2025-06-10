@file:Suppress("NOTHING_TO_INLINE", "OVERRIDE_BY_INLINE")

package net.yqloss.uktil.accessor.refs

import net.yqloss.uktil.accessor.Ref
import net.yqloss.uktil.accessor.value
import net.yqloss.uktil.annotation.UktilInternal

data class LateVar<T>(
    @property:UktilInternal @JvmField var internalInitializer: (T) -> Ref<T>,
    @property:UktilInternal @JvmField var internalWrapped: Ref<T>? = null,
) : Ref<T> {
    @OptIn(UktilInternal::class)
    override inline fun get() = (internalWrapped ?: throw IllegalStateException("get value before initialization")).value

    @OptIn(UktilInternal::class)
    override inline fun set(value: T) {
        val wrapped = internalWrapped
        if (wrapped === null) {
            this.internalWrapped = internalInitializer(value)
            internalInitializer = internalThrowCallInitializerAfterInitialization
        } else {
            wrapped.set(value)
        }
    }

    companion object {
        @UktilInternal
        @JvmField
        val internalThrowCallInitializerAfterInitialization: (Any?) -> Nothing = {
            throw IllegalStateException("call initializer after initialization")
        }
    }
}

@OptIn(UktilInternal::class)
inline fun <T> lateVar(noinline initializer: (T) -> Ref<T> = { it.inMut }) = LateVar(initializer)
