package com.israrxy.gestureessentials.core.model

/**
 * Everything that can start a rule. The `type` discriminator is what gets
 * persisted, so keys are stable and must never be renamed.
 */
sealed interface Trigger {
    val type: TriggerType

    data class Shake(val count: Int = 2) : Trigger {
        override val type = TriggerType.SHAKE
    }

    data object FaceDown : Trigger {
        override val type = TriggerType.FACE_DOWN
    }

    data class Screen(val state: ScreenEvent = ScreenEvent.OFF) : Trigger {
        override val type = TriggerType.SCREEN_STATE
    }

    data object PowerDoublePress : Trigger {
        override val type = TriggerType.POWER_DOUBLE_PRESS
    }

    data class Charger(val plugged: Boolean = true) : Trigger {
        override val type = TriggerType.CHARGER
    }

    data class Headset(val plugged: Boolean = true) : Trigger {
        override val type = TriggerType.HEADSET
    }

    data class AppForeground(val packageName: String, val onEnter: Boolean = true) : Trigger {
        override val type = TriggerType.APP_FOREGROUND
    }

    data class Wifi(val ssid: String) : Trigger {
        override val type = TriggerType.WIFI
    }

    data class Letter(val letter: Char) : Trigger {
        override val type = TriggerType.LETTER
    }

    data class Manual(val source: ManualSource = ManualSource.TILE) : Trigger {
        override val type = TriggerType.MANUAL
    }

    enum class ScreenEvent(val key: String) {
        ON("on"),
        OFF("off"),
        UNLOCKED("unlocked");

        companion object {
            fun fromKey(key: String?): ScreenEvent = entries.firstOrNull { it.key == key } ?: OFF
        }
    }

    enum class ManualSource(val key: String) {
        TILE("tile"),
        BUBBLE("bubble"),
        NOTIFICATION("notification");

        companion object {
            fun fromKey(key: String?): ManualSource = entries.firstOrNull { it.key == key } ?: TILE
        }
    }
}
