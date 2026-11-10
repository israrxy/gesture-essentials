package com.israrxy.gestureessentials.runtime.bus

import com.israrxy.gestureessentials.core.model.TriggerEvent
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

/**
 * Single in-process channel between the accessibility service (producer) and
 * the rule engine (consumer). Buffered so a burst of sensor peaks is not lost
 * while the engine is starting.
 */
class TriggerBus {

    private val _events = MutableSharedFlow<TriggerEvent>(
        extraBufferCapacity = 16,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )

    val events: SharedFlow<TriggerEvent> = _events.asSharedFlow()

    fun emit(event: TriggerEvent) {
        _events.tryEmit(event)
    }
}
