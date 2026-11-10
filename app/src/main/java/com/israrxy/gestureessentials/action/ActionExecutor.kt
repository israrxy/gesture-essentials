package com.israrxy.gestureessentials.action

import com.israrxy.gestureessentials.core.model.ActionSpec

/**
 * Runs a rule's steps in order. A failing step (no torch, denied DND access)
 * must never abort the remaining steps; each handler already swallows its own
 * platform exceptions, and the runner keeps a second safety net.
 */
class ActionExecutor(private val registry: ActionRegistry) {

    suspend fun execute(actions: List<ActionSpec>) {
        actions.forEach { action ->
            val handler = registry.handlerFor(action) ?: return@forEach
            runCatching { handler.execute(action) }
        }
    }
}
