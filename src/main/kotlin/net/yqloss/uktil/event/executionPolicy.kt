package net.yqloss.uktil.event

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import net.yqloss.uktil.functional.UnaryTransformer
import net.yqloss.uktil.generic.castTo
import net.yqloss.uktil.scope.longRun
import net.yqloss.uktil.scope.noExcept
import kotlin.coroutines.CoroutineContext

class LongExecutionPolicy : UnaryTransformer<Event, Unit> {
    override fun <A : Event, R : Unit> invoke(value: (A) -> R) = lambda@{ event: A ->
        longRun<R> {
            return@lambda value(event)
        }
    }
}

class SupervisorExecutionPolicy(private val logger: (Exception) -> Unit = {}) : UnaryTransformer<Event, Unit> {
    override fun <A : Event, R : Unit> invoke(value: (A) -> R) = lambda@{ event: A ->
        noExcept({
            logger(it)
            Unit.castTo()
        }) { value(event) }
    }
}

class EarlyCancelExecutionPolicy : UnaryTransformer<Event, Unit> {
    override fun <A : Event, R : Unit> invoke(value: (A) -> R) = lambda@{ event: A ->
        event is CancelableEvent && event.canceled && return@lambda Unit.castTo<R>()
        value(event)
    }
}

class LaunchExecutionPolicy(val context: CoroutineContext) : UnaryTransformer<Event, Unit> {
    override fun <A : Event, R : Unit> invoke(value: (A) -> R) = lambda@{ event: A ->
        CoroutineScope(context).launch {
            value(event)
        }
        Unit.castTo<R>()
    }
}
