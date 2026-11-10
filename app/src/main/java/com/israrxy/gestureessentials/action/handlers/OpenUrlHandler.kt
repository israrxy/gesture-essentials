package com.israrxy.gestureessentials.action.handlers

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.israrxy.gestureessentials.action.ActionHandler
import com.israrxy.gestureessentials.core.model.ActionSpec
import com.israrxy.gestureessentials.core.model.ActionType

/**
 * Opens a link in the user's browser. The app itself has no INTERNET
 * permission; the browser that handles the intent has its own.
 */
class OpenUrlHandler(private val context: Context) : ActionHandler {

    override val type = ActionType.OPEN_URL

    override suspend fun execute(action: ActionSpec) {
        val spec = action as? ActionSpec.OpenUrl ?: return
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(spec.url))
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        runCatching { context.startActivity(intent) }
    }
}
