@file:Suppress("NOTHING_TO_INLINE")
@file:OptIn(ExperimentalContracts::class)

package dev.necron.uktil.scope

import dev.necron.uktil.annotation.UktilInternal
import java.lang.AutoCloseable
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

@UktilInternal
@JvmInline
value class UsingScopeContext(
    @JvmField val resourceList: ArrayDeque<AutoCloseable>,
) {
    inline fun <T : AutoCloseable> using(resource: T) = resource.also(resourceList::add)

    inline fun using(crossinline cleanup: () -> Unit) {
        resourceList += AutoCloseable { cleanup() }
    }

    inline val <T : AutoCloseable> T.using get() = using(this)

    inline infix fun <T> T.using(crossinline cleanup: (T) -> Unit) = also { this@UsingScopeContext.using { cleanup(this) } }

    inline fun <T : AutoCloseable> usingPre(resource: T) = resource.also(resourceList::addFirst)

    inline fun usingPre(crossinline cleanup: () -> Unit) {
        resourceList.addFirst(AutoCloseable { cleanup() })
    }

    inline val <T : AutoCloseable> T.usingPre get() = usingPre(this)

    inline infix fun <T> T.usingPre(crossinline cleanup: (T) -> Unit) = also { this@UsingScopeContext.usingPre { cleanup(this) } }
}

data class ResourceClosureException(
    @JvmField val failures: List<Pair<Any?, Exception>>,
) : Exception()

@OptIn(UktilInternal::class)
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
