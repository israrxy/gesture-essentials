package com.israrxy.gestureessentials.ui.catalog

import com.israrxy.gestureessentials.core.model.ActionSpec
import com.israrxy.gestureessentials.core.model.ActionType

enum class ParamKind { NONE, TEXT, NUMBER }

/** UI metadata plus a factory for each action the app can run. */
data class ActionEntry(
    val type: ActionType,
    val label: String,
    val description: String,
    val paramKind: ParamKind = ParamKind.NONE,
    val paramLabel: String = "",
    val create: (String) -> ActionSpec,
)

object ActionCatalog {

    val entries: List<ActionEntry> = listOf(
        ActionEntry(ActionType.TORCH, "Flashlight", "Toggle the torch") { ActionSpec.Torch() },
        ActionEntry(ActionType.MEDIA, "Media play/pause", "Play or pause the current track") { ActionSpec.Media() },
        ActionEntry(ActionType.LOCK_SCREEN, "Lock screen", "Turn the screen off and lock") { ActionSpec.LockScreen },
        ActionEntry(ActionType.SCREENSHOT, "Screenshot", "Capture the screen") { ActionSpec.Screenshot },
        ActionEntry(ActionType.VIBRATE, "Vibrate", "Short haptic confirmation") { ActionSpec.Vibrate() },
        ActionEntry(ActionType.DND, "Do Not Disturb", "Turn Do Not Disturb on") { ActionSpec.Dnd() },
        ActionEntry(
            ActionType.OPEN_URL,
            "Open URL",
            "Open a link in the browser",
            ParamKind.TEXT,
            "https://example.com",
        ) { input -> ActionSpec.OpenUrl(input) },
        ActionEntry(
            ActionType.SPEAK,
            "Speak",
            "Say a phrase out loud",
            ParamKind.TEXT,
            "Text to speak",
        ) { input -> ActionSpec.Speak(input) },
        ActionEntry(
            ActionType.DELAY,
            "Wait",
            "Pause before the next action",
            ParamKind.NUMBER,
            "Milliseconds",
        ) { input -> ActionSpec.Delay(input.toIntOrNull()?.coerceIn(0, 60_000) ?: 500) },
    )

    fun entry(type: ActionType): ActionEntry = entries.first { it.type == type }

    fun summary(action: ActionSpec): String = when (action) {
        is ActionSpec.Torch -> "Flashlight (${action.mode.key})"
        is ActionSpec.Media -> "Media: ${action.command.key}"
        is ActionSpec.OpenUrl -> "Open ${action.url}"
        is ActionSpec.Speak -> "Say \"${action.text}\""
        is ActionSpec.Delay -> "Wait ${action.millis} ms"
        is ActionSpec.Dnd -> if (action.enabled) "Do Not Disturb on" else "Do Not Disturb off"
        is ActionSpec.Vibrate -> "Vibrate"
        is ActionSpec.LockScreen -> "Lock screen"
        is ActionSpec.Screenshot -> "Screenshot"
    }
}
