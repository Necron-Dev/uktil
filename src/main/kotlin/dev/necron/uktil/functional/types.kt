package dev.necron.uktil.functional

interface Transformer {
    operator fun <T> invoke(arg: T): T
}
