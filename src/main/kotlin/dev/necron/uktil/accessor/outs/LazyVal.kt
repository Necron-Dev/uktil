@file:Suppress("NOTHING_TO_INLINE", "OVERRIDE_BY_INLINE")

package dev.necron.uktil.accessor.outs

import dev.necron.uktil.accessor.Out
import dev.necron.uktil.accessor.value
import dev.necron.uktil.annotation.UktilInternal

@UktilInternal
data class LazyVal<out T>(
    @property:UktilInternal @JvmField var initializer: () -> Out<@UnsafeVariance T>,
    @property:UktilInternal @JvmField var wrapped: Out<@UnsafeVariance T>? = null,
) : Out<T> {
    @OptIn(UktilInternal::class)
    override inline fun get(): T {
        var wrapped = wrapped
        if (wrapped === null) {
            wrapped = initializer()
            initializer = throwCallInitializerAfterInitialization
            this.wrapped = wrapped
        }
        return wrapped.value
    }

    companion object {
        @UktilInternal
        @JvmField
        val throwCallInitializerAfterInitialization: () -> Nothing = {
            throw IllegalStateException("call initializer after initialization")
        }
    }
}

@OptIn(UktilInternal::class)
inline fun <T> lazyVal(noinline initializer: () -> Out<T>) = LazyVal(initializer)

@OptIn(UktilInternal::class)
inline fun <T> lazyValOf(noinline initializer: () -> T) = LazyVal({ initializer().inBox })
