@file:Suppress("NOTHING_TO_INLINE", "OVERRIDE_BY_INLINE")

package dev.necron.uktil.accessor.refs

import dev.necron.uktil.accessor.Out
import dev.necron.uktil.accessor.outs.Box
import dev.necron.uktil.accessor.outs.inBox
import dev.necron.uktil.accessor.outs.value
import dev.necron.uktil.annotation.UktilInternal
import dev.necron.uktil.functional.eval
import dev.necron.uktil.functional.once
import dev.necron.uktil.functional.onceUnary

@UktilInternal
data class Trigger<out T, TV>(
    @property:UktilInternal @JvmField val function: (TV) -> T,
    @property:UktilInternal @JvmField val versionGetter: () -> TV,
    @property:UktilInternal var holder: Box<@UnsafeVariance T>?,
    @property:UktilInternal var version: Box<TV>? = null,
) : Out<T> {
    @OptIn(UktilInternal::class)
    override inline fun get(): T {
        val currentVersion = versionGetter().inBox
        val holder = holder
        return if (holder?.eval { version == currentVersion } == true) {
            holder.value
        } else {
            val newValue = function(currentVersion.value)
            version = currentVersion
            this.holder = newValue.inBox
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
