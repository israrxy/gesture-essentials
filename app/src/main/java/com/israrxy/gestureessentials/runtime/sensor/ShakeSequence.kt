package com.israrxy.gestureessentials.runtime.sensor

/**
 * A shake burst is dispatched once, after a quiet gap. Waiting is necessary:
 * the second peak cannot yet tell us whether a third shake will follow.
 * Times are monotonic milliseconds; this class has no Android dependencies.
 */
class ShakeSequence(private val emit: (Int) -> Unit) {
    private var lastPeakAt: Long? = null
    private var count = 0

    /** Returns true only for an accepted peak, so noise cannot extend the timer. */
    fun onPeak(now: Long): Boolean {
        finishIfQuiet(now)
        val previous = lastPeakAt
        if (previous != null && now - previous < MIN_PEAK_GAP_MS) return false
        lastPeakAt = now
        count += 1
        return true
    }

    fun finishIfQuiet(now: Long) {
        val previous = lastPeakAt ?: return
        if (now - previous < QUIET_MS) return
        val completed = count
        reset()
        emit(completed)
    }

    fun reset() {
        lastPeakAt = null
        count = 0
    }

    companion object {
        const val QUIET_MS = 450L
        const val MIN_PEAK_GAP_MS = 180L
    }
}
