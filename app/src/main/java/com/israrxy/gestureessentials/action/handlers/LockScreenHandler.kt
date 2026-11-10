package com.israrxy.gestureessentials.action.handlers

import android.accessibilityservice.AccessibilityService
import com.israrxy.gestureessentials.action.ActionHandler
import com.israrxy.gestureessentials.core.model.ActionSpec
import com.israrxy.gestureessentials.core.model.ActionType

/** Locks the screen with the accessibility global action (API 28+). */
class LockScreenHandler(
    private val serviceProvider: () -> AccessibilityService?,
) : ActionHandler {

    override val type = ActionType.LOCK_SCREEN

    override suspend fun execute(action: ActionSpec) {
        serviceProvider()?.performGlobalAction(AccessibilityService.GLOBAL_ACTION_LOCK_SCREEN)
    }
}
