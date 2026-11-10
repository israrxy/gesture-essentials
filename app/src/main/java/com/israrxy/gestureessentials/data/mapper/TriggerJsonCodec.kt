package com.israrxy.gestureessentials.data.mapper

import com.israrxy.gestureessentials.core.model.Trigger
import com.israrxy.gestureessentials.core.model.TriggerType
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

internal object TriggerJsonCodec {

    fun encode(trigger: Trigger): JsonObject = buildJsonObject {
        put("type", trigger.type.key)
        when (trigger) {
            is Trigger.Shake -> put("count", trigger.count)

            is Trigger.Screen -> put("state", trigger.state.key)
            is Trigger.Charger -> put("plugged", trigger.plugged)
            is Trigger.Headset -> put("plugged", trigger.plugged)
            is Trigger.AppForeground -> {
                put("package", trigger.packageName)
                put("onEnter", trigger.onEnter)
            }

            is Trigger.Wifi -> put("ssid", trigger.ssid)
            is Trigger.Letter -> put("letter", trigger.letter.toString())
            is Trigger.Manual -> put("source", trigger.source.key)
            is Trigger.FaceDown, is Trigger.PowerDoublePress -> Unit
        }
    }

    fun decode(obj: JsonObject): Trigger? {
        val type = obj.string("type") ?: return null
        return when (type) {
            TriggerType.SHAKE.key -> Trigger.Shake(count = obj.int("count", 2))

            TriggerType.FACE_DOWN.key -> Trigger.FaceDown
            TriggerType.SCREEN_STATE.key -> Trigger.Screen(Trigger.ScreenEvent.fromKey(obj.string("state")))
            TriggerType.POWER_DOUBLE_PRESS.key -> Trigger.PowerDoublePress
            TriggerType.CHARGER.key -> Trigger.Charger(obj.bool("plugged", true))
            TriggerType.HEADSET.key -> Trigger.Headset(obj.bool("plugged", true))

            TriggerType.APP_FOREGROUND.key -> Trigger.AppForeground(
                packageName = obj.string("package") ?: return null,
                onEnter = obj.bool("onEnter", true),
            )

            TriggerType.WIFI.key -> Trigger.Wifi(obj.string("ssid") ?: return null)

            TriggerType.LETTER.key -> Trigger.Letter(
                obj.string("letter")?.firstOrNull() ?: return null,
            )

            TriggerType.MANUAL.key -> Trigger.Manual(Trigger.ManualSource.fromKey(obj.string("source")))
            else -> null
        }
    }
}
