@file:Suppress("NOTHING_TO_INLINE")

package net.yqloss.uktil.controlflow

import net.yqloss.uktil.annotation.UktilInternal

@UktilInternal
class UnreachableError : Throwable()

@OptIn(UktilInternal::class)
inline val unreachable: Nothing get() = throw UnreachableError()

@OptIn(UktilInternal::class)
inline fun unreachable(): Nothing = throw UnreachableError()
