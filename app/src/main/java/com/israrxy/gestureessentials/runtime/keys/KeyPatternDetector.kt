package com.israrxy.gestureessentials.runtime.keys

import android.os.SystemClock
import android.view.KeyEvent
import com.israrxy.gestureessentials.core.model.Trigger
import com.israrxy.gestureessentials.core.model.TriggerEvent

/**
 * Turns volume-key presses into patterns. Events are observed, never consumed,
 * so the hardware volume keys keep working exactly as before.
 *
 * Long press is detected from the system's own key repeat (the first repeated
 * ACTION_DOWN), with a duration fallback for devices that do not repeat.
 */
class KeyPatternDetector(
    private val emit: (TriggerEvent) -> Unit,
) {

    private val downAt = mutableMapOf<Trigger.Direction, Long>()
    private val longPressSeen = mutableMapOf<Trigger.Direction, Boolean>()
    private val presses = mutableMapOf<Trigger.Direction, ArrayDeque<Long>>()

    fun onKeyEvent(event: KeyEvent) {
        val direction = when (event.keyCode) {
            KeyEvent.KEYCODE_VOLUME_UP -> Trigger.Direction.UP
            KeyEvent.KEYCODE_VOLUME_DOWN -> Trigger.Direction.DOWN
            else -> return
        }

        when (event.action) {
            KeyEvent.ACTION_DOWN -> {
                if (event.repeatCount == 0) {
                    downAt[direction] = SystemClock.elapsedRealtime()
                    longPressSeen[direction] = false
                } else {
                    longPressSeen[direction] = true
                }
            }

            KeyEvent.ACTION_UP -> {
                val startedAt = downAt.remove(direction) ?: return
                val heldLong = longPressSeen.remove(direction) ?: false
                val now = SystemClock.elapsedRealtime()

                if (heldLong || now - startedAt >= LONG_PRESS_MS) {
                    emit(TriggerEvent.VolumeKey(direction, presses = 1, longPress = true))
                    return
                }

                val chain = presses.getOrPut(direction) { ArrayDeque() }
                chain.addLast(now)
                while (chain.isNotEmpty() && now - chain.first() > WINDOW_MS) {
                    chain.removeFirst()
                }
                emit(TriggerEvent.VolumeKey(direction, presses = chain.size, longPress = false))
            }
        }
    }

    private companion object {
        const val WINDOW_MS = 800L
        const val LONG_PRESS_MS = 600L
    }
}
