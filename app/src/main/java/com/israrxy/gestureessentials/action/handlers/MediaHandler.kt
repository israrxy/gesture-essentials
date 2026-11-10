package com.israrxy.gestureessentials.action.handlers

import android.content.Context
import android.media.AudioManager
import android.view.KeyEvent
import com.israrxy.gestureessentials.action.ActionHandler
import com.israrxy.gestureessentials.core.model.ActionSpec
import com.israrxy.gestureessentials.core.model.ActionType

/** Sends media buttons through AudioManager, the same path a headset uses. */
class MediaHandler(context: Context) : ActionHandler {

    override val type = ActionType.MEDIA

    private val audioManager = context.getSystemService(AudioManager::class.java)

    override suspend fun execute(action: ActionSpec) {
        val spec = action as? ActionSpec.Media ?: return

        val keyCode = when (spec.command) {
            ActionSpec.MediaCommand.PLAY_PAUSE -> KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE
            ActionSpec.MediaCommand.NEXT -> KeyEvent.KEYCODE_MEDIA_NEXT
            ActionSpec.MediaCommand.PREVIOUS -> KeyEvent.KEYCODE_MEDIA_PREVIOUS
        }

        audioManager.dispatchMediaKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, keyCode))
        audioManager.dispatchMediaKeyEvent(KeyEvent(KeyEvent.ACTION_UP, keyCode))
    }
}
