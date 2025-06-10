@file:Suppress("NOTHING_TO_INLINE")
@file:OptIn(ExperimentalContracts::class)

package net.yqloss.uktil.scope

import net.yqloss.uktil.annotation.UktilInternal
import java.lang.AutoCloseable
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

@JvmInline
value class UsingScopeContext(
    @property:UktilInternal @JvmField val internalResourceList: ArrayDeque<AutoCloseable>,
) {
    @OptIn(UktilInternal::class)
    inline fun <T : AutoCloseable> using(resource: T) = resource.also(internalResourceList::add)

    @OptIn(UktilInternal::class)
    inline fun using(crossinline cleanup: () -> Unit) {
        internalResourceList += AutoCloseable { cleanup() }
    }

    inline val <T : AutoCloseable> T.using get() = using(this)

    @OptIn(UktilInternal::class)
    inline infix fun <T> T.using(crossinline cleanup: (T) -> Unit) = also { this@UsingScopeContext.using { cleanup(this) } }

    @OptIn(UktilInternal::class)
    inline fun <T : AutoCloseable> usingPre(resource: T) = resource.also(internalResourceList::addFirst)

    @OptIn(UktilInternal::class)
    inline fun usingPre(crossinline cleanup: () -> Unit) {
        internalResourceList.addFirst(AutoCloseable { cleanup() })
    }

    inline val <T : AutoCloseable> T.usingPre get() = usingPre(this)

    inline infix fun <T> T.usingPre(crossinline cleanup: (T) -> Unit) = also { this@UsingScopeContext.usingPre { cleanup(this) } }
}

data class ResourceClosureException(
    @JvmField val failures: List<Pair<Any?, Exception>>,
) : Exception()

inline fun <R> usingScope(function: UsingScopeContext.() -> R): R {
    contract {
        callsInPlace(function, InvocationKind.EXACTLY_ONCE)
    }

    val resourceList = ArrayDeque<AutoCloseable>()

    try {
        return UsingScopeContext(resourceList).function()
    } finally {
        val exceptionList = mutableListOf<Pair<Any?, Exception>>()
        resourceList.asReversed().forEach { resource ->
            noExcept({ exceptionList.add(resource to it) }) { resource.close() }
        }
        exceptionList.isEmpty() || throw ResourceClosureException(exceptionList)
    }
}
