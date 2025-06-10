@file:Suppress("NOTHING_TO_INLINE")

package net.yqloss.uktil.event

interface Event

interface CancelableEvent : Event {
    var canceled: Boolean
}

interface ProcessedEvent : CancelableEvent {
    var failure: Throwable?
}
