@file:Suppress("NOTHING_TO_INLINE", "OVERRIDE_BY_INLINE")

package net.yqloss.uktil.accessor.refs

import net.yqloss.uktil.accessor.Out
import net.yqloss.uktil.accessor.outs.Box
import net.yqloss.uktil.accessor.outs.inBox
import net.yqloss.uktil.accessor.outs.value
import net.yqloss.uktil.annotation.UktilInternal
import net.yqloss.uktil.functional.eval
import net.yqloss.uktil.functional.once
import net.yqloss.uktil.functional.onceUnary

data class Trigger<out T, TV>(
    @property:UktilInternal @JvmField val internalFunction: (TV) -> T,
    @property:UktilInternal @JvmField val internalVersionGetter: () -> TV,
    @property:UktilInternal var internalHolder: Box<@UnsafeVariance T>?,
    @property:UktilInternal var internalVersion: Box<TV>? = null,
) : Out<T> {
    @OptIn(UktilInternal::class)
    override inline fun get(): T {
        val currentVersion = internalVersionGetter().inBox
        val holder = internalHolder
        return if (holder?.eval { internalVersion == currentVersion } == true) {
            holder.value
        } else {
            val newValue = internalFunction(currentVersion.value)
            internalVersion = currentVersion
            this.internalHolder = newValue.inBox
            newValue
        }
    }
}

@OptIn(UktilInternal::class)
inline fun <T, TV> trigger(
    noinline versionGetter: () -> TV,
    noinline function: (TV) -> T,
) = Trigger(function, versionGetter, null)

@OptIn(UktilInternal::class)
inline fun <T, TV> trigger(
    initialValue: TV,
    initialVersion: TV,
    noinline versionGetter: () -> TV,
    noinline function: (TV) -> T,
) = Trigger(function, versionGetter, initialValue.inBox, initialVersion.inBox)

@OptIn(UktilInternal::class)
inline fun <T, TV> triggerOnce(
    noinline versionGetter: () -> TV,
    noinline function: () -> T,
) = Trigger({ once(function) }, versionGetter, null)

@OptIn(UktilInternal::class)
inline fun <TA, T, TV> triggerOnceUnary(
    noinline versionGetter: () -> TV,
    noinline function: (TA) -> T,
) = Trigger({ onceUnary<() -> TA, T> { function(it()) } }, versionGetter, null)
