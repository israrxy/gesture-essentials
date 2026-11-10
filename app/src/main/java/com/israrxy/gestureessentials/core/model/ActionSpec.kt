package com.israrxy.gestureessentials.core.model

enum class ActionType(val key: String) {
    TORCH("torch"),
    MEDIA("media"),
    LOCK_SCREEN("lockScreen"),
    SCREENSHOT("screenshot"),
    VIBRATE("vibrate"),
    DND("dnd"),
    OPEN_URL("openUrl"),
    SPEAK("speak"),
    DELAY("delay"),
}

/** One step of a rule. Rules hold an ordered list of these. */
sealed interface ActionSpec {
    val type: ActionType

    data class Torch(val mode: TorchMode = TorchMode.TOGGLE) : ActionSpec {
        override val type = ActionType.TORCH
    }

    data class Media(val command: MediaCommand = MediaCommand.PLAY_PAUSE) : ActionSpec {
        override val type = ActionType.MEDIA
    }

    data object LockScreen : ActionSpec {
        override val type = ActionType.LOCK_SCREEN
    }

    data object Screenshot : ActionSpec {
        override val type = ActionType.SCREENSHOT
    }

    data class Vibrate(val pattern: VibratePattern = VibratePattern.SHORT) : ActionSpec {
        override val type = ActionType.VIBRATE
    }

    data class Dnd(val enabled: Boolean = true) : ActionSpec {
        override val type = ActionType.DND
    }

    data class OpenUrl(val url: String) : ActionSpec {
        override val type = ActionType.OPEN_URL
    }

    data class Speak(val text: String) : ActionSpec {
        override val type = ActionType.SPEAK
    }

    data class Delay(val millis: Int = 500) : ActionSpec {
        override val type = ActionType.DELAY
    }

    enum class TorchMode(val key: String) {
        TOGGLE("toggle"),
        ON("on"),
        OFF("off");

        companion object {
            fun fromKey(key: String?): TorchMode = entries.firstOrNull { it.key == key } ?: TOGGLE
        }
    }

    enum class MediaCommand(val key: String) {
        PLAY_PAUSE("playPause"),
        NEXT("next"),
        PREVIOUS("previous");

        companion object {
            fun fromKey(key: String?): MediaCommand = entries.firstOrNull { it.key == key } ?: PLAY_PAUSE
        }
    }

    enum class VibratePattern(val key: String) {
        SHORT("short"),
        DOUBLE("double"),
        LONG("long");

        companion object {
            fun fromKey(key: String?): VibratePattern = entries.firstOrNull { it.key == key } ?: SHORT
        }
    }
}
