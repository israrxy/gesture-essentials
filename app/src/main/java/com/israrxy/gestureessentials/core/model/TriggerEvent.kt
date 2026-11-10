package com.israrxy.gestureessentials.core.model

/**
 * Something that just happened on the device. Kept separate from [Trigger]:
 * a trigger is saved rule configuration, an event is one live occurrence.
 */
sealed interface TriggerEvent {
    data class Shake(val count: Int) : TriggerEvent

    data class VolumeKey(
        val direction: Trigger.Direction,
        val presses: Int,
        val longPress: Boolean,
    ) : TriggerEvent

    data object FaceDown : TriggerEvent

    data class Screen(val state: Trigger.ScreenEvent) : TriggerEvent

    data object PowerDoublePress : TriggerEvent

    data class Charger(val plugged: Boolean) : TriggerEvent

    data class Headset(val plugged: Boolean) : TriggerEvent

    data class AppForeground(val packageName: String) : TriggerEvent

    data class Wifi(val ssid: String) : TriggerEvent

    data class Letter(val letter: Char) : TriggerEvent

    data class Manual(val source: Trigger.ManualSource) : TriggerEvent
}
