package com.israrxy.gestureessentials.runtime.screen

import android.os.SystemClock
import com.israrxy.gestureessentials.core.model.Trigger
import com.israrxy.gestureessentials.core.model.TriggerEvent

/**
 * Screen on/off/unlock events plus the double-press-power heuristic.
 *
 * Android exposes no power-button callback to apps. This only fires when the
 * screen goes off and comes back on inside a short window, which is what a
 * double press looks like from the outside. The UI labels it as a heuristic.
 */
class ScreenToggleDetector(
    private val emit: (TriggerEvent) -> Unit,
) {

    private var lastOffAt = 0L

    fun onScreenOff() {
        lastOffAt = SystemClock.elapsedRealtime()
        emit(TriggerEvent.Screen(Trigger.ScreenEvent.OFF))
    }

    fun onScreenOn() {
        emit(TriggerEvent.Screen(Trigger.ScreenEvent.ON))

        val offAt = lastOffAt
        if (offAt != 0L && SystemClock.elapsedRealtime() - offAt <= DOUBLE_PRESS_WINDOW_MS) {
            lastOffAt = 0L
            emit(TriggerEvent.PowerDoublePress)
        }
    }

    fun onUnlocked() {
        emit(TriggerEvent.Screen(Trigger.ScreenEvent.UNLOCKED))
    }

    private companion object {
        const val DOUBLE_PRESS_WINDOW_MS = 1_200L
    }
}
