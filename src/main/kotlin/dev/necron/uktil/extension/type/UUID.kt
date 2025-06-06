@file:Suppress("NOTHING_TO_INLINE")

package dev.necron.uktil.extension.type

import java.util.*

inline val UUID.dashedLowerString get() = toString().lowercase()

inline val UUID.dashedUpperString get() = toString().uppercase()

inline val UUID.undashedLowerString get() = toString().replace("-", "").lowercase()

inline val UUID.undashedUpperString get() = toString().replace("-", "").uppercase()

inline fun UUID.parseLenient(string: String): UUID {
    val normalized = string.replace("-", "").trim()
    if (normalized.length != 32) throw IllegalArgumentException("invalid uuid string: $string")
    return UUID.fromString(
        normalized.substring(0..<8) +
            "-${normalized.substring(8..<12)}" +
            "-${normalized.substring(12..<16)}" +
            "-${normalized.substring(16..<20)}" +
            "-${normalized.substring(20..<32)}",
    )
}
