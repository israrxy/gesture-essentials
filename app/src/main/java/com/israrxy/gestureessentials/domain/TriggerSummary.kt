package com.israrxy.gestureessentials.domain

import com.israrxy.gestureessentials.core.model.Trigger

/**
 * Human-readable one-liner for a trigger. Lives in the domain layer so both the
 * UI and the ViewModel can name a rule without depending on Compose.
 */
fun Trigger.summary(): String = when (this) {
    is Trigger.Shake -> "Shake x$count"
    is Trigger.Screen -> "Screen ${state.key}"
    is Trigger.Charger -> if (plugged) "Charger plugged" else "Charger unplugged"
    is Trigger.Headset -> if (plugged) "Headset plugged" else "Headset unplugged"
    is Trigger.Letter -> "Draw \"$letter\""
    is Trigger.AppForeground -> "Open ${packageName.ifBlank { "an app" }}"
    is Trigger.Wifi -> "Wi-Fi ${ssid.ifBlank { "network" }}"
    is Trigger.Manual -> "Manual (${source.key})"
    is Trigger.FaceDown -> "Face down"
    is Trigger.PowerDoublePress -> "Power double-press"
}
