package dev.necron.uktil.event

interface CancelableEvent : Event {
    var canceled: Boolean
}
