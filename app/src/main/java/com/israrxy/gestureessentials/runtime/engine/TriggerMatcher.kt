package com.israrxy.gestureessentials.runtime.engine

import com.israrxy.gestureessentials.core.model.Trigger
import com.israrxy.gestureessentials.core.model.TriggerEvent

/** Decides whether a live event satisfies a saved trigger. Pure logic. */
class TriggerMatcher {

    fun matches(trigger: Trigger, event: TriggerEvent): Boolean = when (trigger) {
        is Trigger.Shake -> event is TriggerEvent.Shake && event.count >= trigger.count

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

    /**
     * A burst of N shakes satisfies every rule asking for N or fewer, so the
     * engine must keep only the most specific one. Without this, a three-shake
     * burst would also run the double-shake shortcut.
     */
    fun bestShakeCount(triggers: List<Trigger>, event: TriggerEvent.Shake): Int? =
        triggers.filterIsInstance<Trigger.Shake>()
            .filter { event.count >= it.count }
            .maxOfOrNull { it.count }
}
