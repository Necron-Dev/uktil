@file:Suppress("NOTHING_TO_INLINE", "OVERRIDE_BY_INLINE")

package net.yqloss.uktil.accessor.refs

import net.yqloss.uktil.accessor.Ref
import net.yqloss.uktil.accessor.value
import net.yqloss.uktil.annotation.UktilInternal

data class LazyVar<T>(
    @property:UktilInternal @JvmField var internalSetInitializer: (T) -> Ref<T>,
    @property:UktilInternal @JvmField var internalGetInitializer: () -> Ref<T>,
    @property:UktilInternal @JvmField var internalWrapped: Ref<T>? = null,
) : Ref<T> {
    @OptIn(UktilInternal::class)
    override inline fun get(): T {
        var wrapped = internalWrapped
        if (wrapped === null) {
            wrapped = internalGetInitializer()
            internalGetInitializer = internalThrowCallInitializerAfterInitialization
            internalSetInitializer = internalThrowCallInitializerAfterInitializationUnary
            this.internalWrapped = wrapped
        }
        return wrapped.value
    }

    @OptIn(UktilInternal::class)
    override inline fun set(value: T) {
        var wrapped = internalWrapped
        if (wrapped === null) {
            wrapped = internalSetInitializer(value)
            internalGetInitializer = internalThrowCallInitializerAfterInitialization
            internalSetInitializer = internalThrowCallInitializerAfterInitializationUnary
            this.internalWrapped = wrapped
        }
        wrapped.value = value
    }

    companion object {
        @UktilInternal
        @JvmField
        val internalThrowCallInitializerAfterInitialization: () -> Nothing = {
            throw IllegalStateException("call initializer after initialization")
        }

        @UktilInternal
        @JvmField
        val internalThrowCallInitializerAfterInitializationUnary: (Any?) -> Nothing = {
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
