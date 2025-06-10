@file:Suppress("NOTHING_TO_INLINE", "OVERRIDE_BY_INLINE")

package net.yqloss.uktil.accessor.refs

import net.yqloss.uktil.accessor.Out
import net.yqloss.uktil.accessor.Ref
import net.yqloss.uktil.accessor.outs.inBox
import net.yqloss.uktil.accessor.value
import net.yqloss.uktil.annotation.UktilInternal

data class LateVal<T>(
    @property:UktilInternal @JvmField var internalInitializer: (T) -> Out<T>,
    @property:UktilInternal @JvmField var internalWrapped: Out<T>? = null,
) : Ref<T> {
    @OptIn(UktilInternal::class)
    override inline fun get() = (internalWrapped ?: throw IllegalStateException("get value before initialization")).value

    @OptIn(UktilInternal::class)
    override inline fun set(value: T) {
        internalWrapped?.let { throw IllegalStateException("set value after initialization") }
        internalWrapped = internalInitializer(value)
        internalInitializer = internalThrowCallInitializerAfterInitialization
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
inline fun <T> lateVal(noinline initializer: (T) -> Out<T> = { it.inBox }) = LateVal(initializer)
