package com.israrxy.gestureessentials.action.handlers

import android.app.NotificationManager
import android.content.Context
import com.israrxy.gestureessentials.action.ActionHandler
import com.israrxy.gestureessentials.core.model.ActionSpec
import com.israrxy.gestureessentials.core.model.ActionType

/**
 * Toggles Do Not Disturb. Requires ACCESS_NOTIFICATION_POLICY, which is a
 * normal manifest permission, but the user can still revoke DND access in
 * system settings, hence the safe call.
 */
class DndHandler(context: Context) : ActionHandler {

    override val type = ActionType.DND

    private val notificationManager = context.getSystemService(NotificationManager::class.java)

    override suspend fun execute(action: ActionSpec) {
        val spec = action as? ActionSpec.Dnd ?: return

        val filter = if (spec.enabled) {
            NotificationManager.INTERRUPTION_FILTER_PRIORITY
        } else {
            NotificationManager.INTERRUPTION_FILTER_ALL
        }

        runCatching { notificationManager.setInterruptionFilter(filter) }
    }
}
