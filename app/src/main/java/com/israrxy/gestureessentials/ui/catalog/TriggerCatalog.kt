package com.israrxy.gestureessentials.ui.catalog

import com.israrxy.gestureessentials.core.model.Trigger
import com.israrxy.gestureessentials.core.model.TriggerType
import com.israrxy.gestureessentials.domain.summary

/** UI metadata for every trigger the app understands. No Android imports. */
data class TriggerEntry(
    val type: TriggerType,
    val label: String,
    val description: String,
    val create: () -> Trigger,
)

object TriggerCatalog {

    val entries: List<TriggerEntry> = listOf(
        TriggerEntry(TriggerType.SHAKE, "Shake", "Shake the phone two or three times") { Trigger.Shake() },
        TriggerEntry(TriggerType.FACE_DOWN, "Face down", "Place the phone screen-down") { Trigger.FaceDown },
        TriggerEntry(TriggerType.SCREEN_STATE, "Screen", "Screen turns off or is unlocked") { Trigger.Screen() },
        TriggerEntry(
            TriggerType.POWER_DOUBLE_PRESS,
            "Power double-press",
            "Heuristic: screen off then on within ~1.2 s",
        ) { Trigger.PowerDoublePress },
        TriggerEntry(TriggerType.CHARGER, "Charger", "Plug or unplug the charger") { Trigger.Charger() },
        TriggerEntry(TriggerType.HEADSET, "Headset", "Plug or unplug a headset") { Trigger.Headset() },
        TriggerEntry(TriggerType.APP_FOREGROUND, "App opened", "A specific app comes to the front") {
            Trigger.AppForeground(packageName = "")
        },
        TriggerEntry(TriggerType.WIFI, "Wi-Fi network", "A specific network connects") { Trigger.Wifi(ssid = "") },
        TriggerEntry(TriggerType.LETTER, "Letter drawn", "Draw a letter on the gesture pad") { Trigger.Letter('O') },
        TriggerEntry(TriggerType.MANUAL, "Manual", "Quick settings tile or floating bubble") { Trigger.Manual() },
    )

    fun entry(type: TriggerType): TriggerEntry = entries.first { it.type == type }

    /** Delegates to the domain helper so UI and ViewModel never disagree. */
    fun summary(trigger: Trigger): String = trigger.summary()
}
