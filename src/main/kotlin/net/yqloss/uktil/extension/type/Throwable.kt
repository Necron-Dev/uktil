@file:Suppress("NOTHING_TO_INLINE")

package net.yqloss.uktil.extension.type

import java.io.PrintWriter
import java.io.StringWriter

inline val Throwable.stackTraceMessage
    get() = StringWriter()
        .also { sw ->
            PrintWriter(sw).use { pw ->
                printStackTrace(pw)
                pw.flush()
            }
        }.toString()
