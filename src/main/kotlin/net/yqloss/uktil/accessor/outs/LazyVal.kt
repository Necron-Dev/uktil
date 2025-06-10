@file:Suppress("NOTHING_TO_INLINE", "OVERRIDE_BY_INLINE")

package net.yqloss.uktil.accessor.outs

import net.yqloss.uktil.accessor.Out
import net.yqloss.uktil.accessor.value
import net.yqloss.uktil.annotation.UktilInternal

data class LazyVal<out T>(
    @property:UktilInternal @JvmField var internalInitializer: () -> Out<@UnsafeVariance T>,
    @property:UktilInternal @JvmField var internalWrapped: Out<@UnsafeVariance T>? = null,
) : Out<T> {
    @OptIn(UktilInternal::class)
    override inline fun get(): T {
        var wrapped = internalWrapped
        if (wrapped === null) {
            wrapped = internalInitializer()
            internalInitializer = internalThrowCallInitializerAfterInitialization
            this.internalWrapped = wrapped
        }
        return wrapped.value
    }

    companion object {
        @UktilInternal
        @JvmField
        val internalThrowCallInitializerAfterInitialization: () -> Nothing = {
            throw IllegalStateException("call initializer after initialization")
        }
    }
}

@OptIn(UktilInternal::class)
inline fun <T> lazyVal(noinline initializer: () -> Out<T>) = LazyVal(initializer)

@OptIn(UktilInternal::class)
inline fun <T> lazyValOf(noinline initializer: () -> T) = LazyVal({ initializer().inBox })
