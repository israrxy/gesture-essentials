package com.israrxy.gestureessentials.runtime.sensor

/**
 * A shake burst is dispatched once, after a quiet gap. Waiting is necessary:
 * the second peak cannot yet tell us whether a third shake will follow.
 *
 * The wait is asymmetric. After the second peak the burst is almost certainly
 * a multi-shake gesture, so 300 ms is enough. After a single peak, the window
 * stays open for 650 ms so a slower second shake still joins the same burst
 * instead of becoming two unrecognisable single shakes.
 *
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

    /** @return false only while the burst is still waiting for its quiet gap. */
    fun finishIfQuiet(now: Long): Boolean {
        val previous = lastPeakAt ?: return true
        if (now - previous < quietWindow()) return false
        val completed = count
        reset()
        emit(completed)
        return true
    }

    /** The wait after the most recent peak, before the burst is dispatched. */
    fun quietWindow(): Long = if (count >= 2) MULTI_QUIET_MS else FIRST_PEAK_QUIET_MS

    fun reset() {
        lastPeakAt = null
        count = 0
    }

    companion object {
        const val MULTI_QUIET_MS = 300L
        const val FIRST_PEAK_QUIET_MS = 650L
        const val MIN_PEAK_GAP_MS = 180L
    }
}
