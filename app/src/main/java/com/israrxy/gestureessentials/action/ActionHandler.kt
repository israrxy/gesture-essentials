package com.israrxy.gestureessentials.action

import com.israrxy.gestureessentials.core.model.ActionSpec
import com.israrxy.gestureessentials.core.model.ActionType

/** One executable step. Handlers are stateless apart from hardware toggles. */
interface ActionHandler {
    val type: ActionType

    suspend fun execute(action: ActionSpec)
}
