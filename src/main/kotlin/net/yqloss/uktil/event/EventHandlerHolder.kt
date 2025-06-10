package net.yqloss.uktil.event

class EventHandlerHolder<in T : Event>(
    private val handlers: List<EventHandler<T>>,
) : EventHandler<T> {
    override fun invoke(event: T) {
        handlers.forEach { it(event) }
    }
}
