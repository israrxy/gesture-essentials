package com.israrxy.gestureessentials.action

import com.israrxy.gestureessentials.core.model.ActionSpec
import com.israrxy.gestureessentials.core.model.ActionType

/** Maps an action type to the handler that can perform it. */
class ActionRegistry(handlers: List<ActionHandler>) {

    private val byType: Map<ActionType, ActionHandler> = handlers.associateBy { it.type }

    fun handlerFor(action: ActionSpec): ActionHandler? = byType[action.type]
}
