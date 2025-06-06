@file:Suppress("NOTHING_TO_INLINE", "OVERRIDE_BY_INLINE")

package dev.necron.uktil.accessor.refs

import dev.necron.uktil.accessor.Out
import dev.necron.uktil.accessor.Ref
import dev.necron.uktil.accessor.outs.inBox
import dev.necron.uktil.accessor.value
import dev.necron.uktil.annotation.UktilInternal

@UktilInternal
data class LateVal<T>(
    @property:UktilInternal @JvmField var initializer: (T) -> Out<T>,
    @property:UktilInternal @JvmField var wrapped: Out<T>? = null,
) : Ref<T> {
    @OptIn(UktilInternal::class)
    override inline fun get() = (wrapped ?: throw IllegalStateException("get value before initialization")).value

    @OptIn(UktilInternal::class)
    override inline fun set(value: T) {
        wrapped?.let { throw IllegalStateException("set value after initialization") }
        wrapped = initializer(value)
        initializer = throwCallInitializerAfterInitialization
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
inline fun <T> lateVal(noinline initializer: (T) -> Out<T> = { it.inBox }) = LateVal(initializer)
