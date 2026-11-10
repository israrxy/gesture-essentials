package com.israrxy.gestureessentials.runtime.engine

import com.israrxy.gestureessentials.core.model.Trigger
import com.israrxy.gestureessentials.core.model.TriggerEvent

/** Decides whether a live event satisfies a saved trigger. Pure logic. */
class TriggerMatcher {

    fun matches(trigger: Trigger, event: TriggerEvent): Boolean = when (trigger) {
        is Trigger.Shake -> event is TriggerEvent.Shake && event.count >= trigger.count

        is Trigger.VolumeKey -> event is TriggerEvent.VolumeKey &&
            event.direction == trigger.direction &&
            (
                if (trigger.longPress) {
                    event.longPress
                } else {
                    !event.longPress && event.presses >= trigger.presses
                }
                )

        is Trigger.Screen -> event is TriggerEvent.Screen && event.state == trigger.state
        is Trigger.Charger -> event is TriggerEvent.Charger && event.plugged == trigger.plugged
        is Trigger.Headset -> event is TriggerEvent.Headset && event.plugged == trigger.plugged

        is Trigger.AppForeground -> event is TriggerEvent.AppForeground &&
            trigger.packageName.isNotBlank() &&
            event.packageName == trigger.packageName

        is Trigger.Manual -> event is TriggerEvent.Manual && event.source == trigger.source
        is Trigger.PowerDoublePress -> event is TriggerEvent.PowerDoublePress

        // Not wired yet: these need a proximity sensor, a drawn letter pad and
        // a Wi-Fi callback. Rules using them stay inert instead of guessing.
        is Trigger.FaceDown, is Trigger.Wifi, is Trigger.Letter -> false
    }
}
