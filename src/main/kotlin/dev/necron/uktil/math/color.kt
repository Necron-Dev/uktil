@file:Suppress("NOTHING_TO_INLINE")

package dev.necron.uktil.math

import dev.necron.uktil.extension.int
import kotlin.math.abs
import kotlin.math.min

inline fun convertRGBToHSL(vararg rgb: Double): DoubleArray {
    val hsl = DoubleArray(if (rgb.size >= 4) 4 else 3)
    val maxRGB = maxOf(0.0, *rgb)
    val minRGB = minOf(1.0, *rgb)
    val delta = maxRGB - minRGB
    val (r, g, b) = rgb
    hsl[2] = (maxRGB + minRGB) / 2.0
    if (delta > 0.0) {
        hsl[1] = delta / min(maxRGB + minRGB, 2 - maxRGB - minRGB)
        hsl[0] =
            when (maxRGB) {
                r -> (g - b) / delta + 6.0
                g -> (b - r) / delta + 2.0
                b -> (r - g) / delta + 4.0
                else -> 0.0
            }.let { it / 6.0 % 1.0 }
    }
    if (rgb.size >= 4) {
        hsl[3] = rgb[3]
    }
    return hsl.normalizeColor
}

inline fun convertHSLToRGB(vararg hsl: Double): DoubleArray {
    val (h, s, l) = hsl
    val c = (1.0 - abs(2.0 * l - 1.0)) * s
    val x = c * (1.0 - abs(h * 6.0 % 2.0 - 1.0))
    val m = l - c / 2.0

    val (rp, gp, bp) =
        when (h) {
            in 0.0 / 6.0..<1.0 / 6.0 -> Triple(c, x, 0.0)
            in 1.0 / 6.0..<2.0 / 6.0 -> Triple(x, c, 0.0)
            in 2.0 / 6.0..<3.0 / 6.0 -> Triple(0.0, c, x)
            in 3.0 / 6.0..<4.0 / 6.0 -> Triple(0.0, x, c)
            in 4.0 / 6.0..<5.0 / 6.0 -> Triple(x, 0.0, c)
            else -> Triple(c, 0.0, x)
        }

    return if (hsl.size >= 4) {
        doubleArrayOf(rp + m, gp + m, bp + m, hsl[3])
    } else {
        doubleArrayOf(rp + m, gp + m, bp + m)
    }.normalizeColor
}

inline fun convertARGBToDoubleArray(argb: Int): DoubleArray {
    return doubleArrayOf(
        (argb ushr 16 and 0xFF) / 255.0,
        (argb ushr 8 and 0xFF) / 255.0,
        (argb and 0xFF) / 255.0,
        (argb ushr 24 and 0xFF) / 255.0,
    ).normalizeColor
}

inline fun convertRGBToDoubleArray(argb: Int) = convertARGBToDoubleArray(argb or 0xFF000000.int)

inline fun convertDoubleArrayToARGB(vararg rgb: Double): Int {
    val norm = rgb.normalizeColor
    return ((norm[0] * 255.0).int shl 16) or
        ((norm[1] * 255.0).int shl 8) or
        (norm[2] * 255.0).int or
        if (rgb.size >= 4) (norm[3] * 255.0).int shl 24 else 0xFF
}

inline fun convertDoubleArrayToRGB(vararg rgb: Double) = convertDoubleArrayToARGB(rgb[0], rgb[1], rgb[2], 0.0)

inline fun convertDoubleArrayToFFRGB(vararg rgb: Double) = convertDoubleArrayToARGB(rgb[0], rgb[1], rgb[2], 1.0)

inline val DoubleArray.normalizeColor get() = DoubleArray(size) { get(it).coerceIn(0.0..1.0) }

inline infix fun DoubleArray.blendColor(rgbaSrc: DoubleArray): DoubleArray {
    val (srcR, srcG, srcB, srcA) = rgbaSrc
    val (dstR, dstG, dstB) = this
    val dstA = if (this.size >= 4) this[3] else 1.0
    val resA = srcA + dstA * (1.0 - srcA)
    val srcM = srcA / resA
    val dstM = dstA * (1.0 - srcA) / resA
    return doubleArrayOf(
        srcR * srcM + dstR * dstM,
        srcG * srcM + dstG * dstM,
        srcB * srcM + dstB * dstM,
        resA,
    ).normalizeColor
}

inline infix fun Int.blendColor(argbSrc: Int): Int = convertDoubleArrayToARGB(
    *convertARGBToDoubleArray(this) blendColor convertARGBToDoubleArray(argbSrc),
)
