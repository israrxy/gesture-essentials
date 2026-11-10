package com.israrxy.gestureessentials.action.handlers

import com.israrxy.gestureessentials.action.ActionHandler
import com.israrxy.gestureessentials.core.model.ActionSpec
import com.israrxy.gestureessentials.core.model.ActionType
import kotlinx.coroutines.delay

/** Pauses the chain so a following action lands after the device settles. */
class DelayHandler : ActionHandler {

    override val type = ActionType.DELAY

    override suspend fun execute(action: ActionSpec) {
        val spec = action as? ActionSpec.Delay ?: return
        delay(spec.millis.toLong().coerceIn(0L, 60_000L))
    }
}
