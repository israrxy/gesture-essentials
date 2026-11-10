package com.israrxy.gestureessentials.runtime.sensor

import com.israrxy.gestureessentials.core.model.Trigger
import com.israrxy.gestureessentials.core.model.TriggerEvent
import com.israrxy.gestureessentials.runtime.engine.TriggerMatcher
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ShakeSequenceTest {
    private val counts = mutableListOf<Int>()
    private val sequence = ShakeSequence { counts += it }

    @Test
    fun doubleShakeWaitsForQuietAndFiresOnce() {
        sequence.onPeak(0)
        sequence.onPeak(200)
        sequence.finishIfQuiet(649)
        assertTrue(counts.isEmpty())
        sequence.finishIfQuiet(650)
        sequence.finishIfQuiet(2_000)
        assertEquals(listOf(2), counts)
    }

    @Test
    fun tripleShakeNeverRunsTheDoubleShakeAction() {
        sequence.onPeak(0)
        sequence.onPeak(200)
        sequence.onPeak(400)
        assertTrue(counts.isEmpty())
        sequence.finishIfQuiet(850)
        val event = TriggerEvent.Shake(counts.single())
        val matcher = TriggerMatcher()
        assertTrue(matcher.matches(Trigger.Shake(3), event))
        assertFalse(matcher.matches(Trigger.Shake(2), event))
    }

    @Test
    fun sensorBounceDoesNotCountOrExtendTheQuietPeriod() {
        assertTrue(sequence.onPeak(0))
        assertFalse(sequence.onPeak(50))
        assertFalse(sequence.onPeak(179))
        assertTrue(sequence.onPeak(200))
        assertFalse(sequence.onPeak(250))
        sequence.finishIfQuiet(650)
        assertEquals(listOf(2), counts)
    }

    @Test
    fun aLongerBurstIsNotMistakenForEitherPreset() {
        listOf(0L, 200L, 400L, 600L).forEach { sequence.onPeak(it) }
        sequence.finishIfQuiet(1_050)
        val event = TriggerEvent.Shake(counts.single())
        assertFalse(TriggerMatcher().matches(Trigger.Shake(2), event))
        assertFalse(TriggerMatcher().matches(Trigger.Shake(3), event))
    }

    @Test
    fun aLateTimerDoesNotMergeSeparateGestures() {
        sequence.onPeak(0)
        sequence.onPeak(200)
        // A new sensor event arrives before the overdue completion callback.
        sequence.onPeak(1_000)
        sequence.onPeak(1_200)
        sequence.finishIfQuiet(1_650)
        assertEquals(listOf(2, 2), counts)
    }

    @Test
    fun stoppingOrChangingSensitivityDropsThePartialGesture() {
        sequence.onPeak(0)
        sequence.onPeak(200)
        sequence.reset()
        sequence.finishIfQuiet(1_000)
        assertTrue(counts.isEmpty())
    }
}
