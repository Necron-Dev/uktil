@file:Suppress("NOTHING_TO_INLINE")

package net.yqloss.uktil.accessor

import kotlin.reflect.KMutableProperty0
import kotlin.reflect.KProperty

interface Ref<T> :
    In<T>,
    Out<T>

inline var <T> Ref<T>.value
    get() = get()
    set(value) = set(value)

inline operator fun <X, T> Ref<T>.getValue(
    thisRef: X,
    property: KProperty<*>,
) = value

inline operator fun <X, T> Ref<T>.setValue(
    thisRef: X,
    property: KProperty<*>,
    value: T,
) = set(value)

inline infix fun <T> Ref<T>.swap(ref: Ref<T>) {
    val v1 = get()
    val v2 = ref.get()
    set(v2)
    ref.set(v1)
}

inline fun <T> makeRef(
    noinline getter: () -> T,
    noinline setter: (T) -> Unit,
) = object : Ref<T> {
    override fun get() = getter()

    override fun set(value: T) = setter(value)
}

inline val <T> Pair<() -> T, (T) -> Unit>.asRef get() = makeRef(first, second)

inline val <T> KMutableProperty0<T>.asRef get() = makeRef(this, this::set)

inline val <T> Out<T>.asReadOnlyRef: Ref<T>
    get() = makeRef(this) {
        throw UnsupportedOperationException("get value from a read-only ref")
    }

inline val <T> In<T>.asWriteOnlyRef: Ref<T>
    get() = makeRef(
        { throw UnsupportedOperationException("set value into a write-only ref") },
        { this(it) },
    )

inline fun <T> nullRef(): Ref<T> = makeRef(
    { throw UnsupportedOperationException("get value from a null ref") },
    { throw UnsupportedOperationException("set value into a null ref") },
)
