@file:Suppress("NOTHING_TO_INLINE", "OVERRIDE_BY_INLINE")

package dev.necron.uktil.accessor.refs

import dev.necron.uktil.accessor.Ref
import dev.necron.uktil.accessor.value
import dev.necron.uktil.annotation.UktilInternal

data class LazyVar<T>(
    @property:UktilInternal @JvmField var setInitializer: (T) -> Ref<T>,
    @property:UktilInternal @JvmField var getInitializer: () -> Ref<T>,
    @property:UktilInternal @JvmField var wrapped: Ref<T>? = null,
) : Ref<T> {
    @OptIn(UktilInternal::class)
    override inline fun get(): T {
        var wrapped = wrapped
        if (wrapped === null) {
            wrapped = getInitializer()
            getInitializer = throwCallInitializerAfterInitialization
            setInitializer = throwCallInitializerAfterInitializationUnary
            this.wrapped = wrapped
        }
        return wrapped.value
    }

    @OptIn(UktilInternal::class)
    override inline fun set(value: T) {
        var wrapped = wrapped
        if (wrapped === null) {
            wrapped = setInitializer(value)
            getInitializer = throwCallInitializerAfterInitialization
            setInitializer = throwCallInitializerAfterInitializationUnary
            this.wrapped = wrapped
        }
        wrapped.value = value
    }

    companion object {
        @UktilInternal
        @JvmField
        val throwCallInitializerAfterInitialization: () -> Nothing = {
            throw IllegalStateException("call initializer after initialization")
        }

        @UktilInternal
        @JvmField
        val throwCallInitializerAfterInitializationUnary: (Any?) -> Nothing = {
            throw IllegalStateException("call initializer after initialization")
        }
    }
}

@OptIn(UktilInternal::class)
inline fun <T> lazyVar(
    noinline setInitializer: (T) -> Ref<T> = { it.inMut },
    noinline getInitializer: () -> Ref<T>,
) = LazyVar(setInitializer, getInitializer)

@OptIn(UktilInternal::class)
inline fun <T> lazyVarOf(noinline initializer: () -> T) = LazyVar(
    { it.inMut },
    { initializer().inMut },
)
