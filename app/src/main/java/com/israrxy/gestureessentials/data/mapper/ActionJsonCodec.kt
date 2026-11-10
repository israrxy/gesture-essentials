package com.israrxy.gestureessentials.data.mapper

import com.israrxy.gestureessentials.core.model.ActionSpec
import com.israrxy.gestureessentials.core.model.ActionType
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

internal object ActionJsonCodec {

    fun encode(action: ActionSpec): JsonObject = buildJsonObject {
        put("type", action.type.key)
        when (action) {
            is ActionSpec.Torch -> put("mode", action.mode.key)
            is ActionSpec.Media -> put("command", action.command.key)
            is ActionSpec.Vibrate -> put("pattern", action.pattern.key)
            is ActionSpec.Dnd -> put("enabled", action.enabled)
            is ActionSpec.OpenUrl -> put("url", action.url)
            is ActionSpec.Speak -> put("text", action.text)
            is ActionSpec.Delay -> put("millis", action.millis)
            is ActionSpec.LockScreen, is ActionSpec.Screenshot -> Unit
        }
    }

    fun decode(obj: JsonObject): ActionSpec? {
        val type = obj.string("type") ?: return null
        return when (type) {
            ActionType.TORCH.key -> ActionSpec.Torch(ActionSpec.TorchMode.fromKey(obj.string("mode")))
            ActionType.MEDIA.key -> ActionSpec.Media(ActionSpec.MediaCommand.fromKey(obj.string("command")))
            ActionType.LOCK_SCREEN.key -> ActionSpec.LockScreen
            ActionType.SCREENSHOT.key -> ActionSpec.Screenshot
            ActionType.VIBRATE.key -> ActionSpec.Vibrate(ActionSpec.VibratePattern.fromKey(obj.string("pattern")))
            ActionType.DND.key -> ActionSpec.Dnd(obj.bool("enabled", true))
            ActionType.OPEN_URL.key -> ActionSpec.OpenUrl(obj.string("url") ?: return null)
            ActionType.SPEAK.key -> ActionSpec.Speak(obj.string("text") ?: return null)
            ActionType.DELAY.key -> ActionSpec.Delay(obj.int("millis", 500))
            else -> null
        }
    }
}
