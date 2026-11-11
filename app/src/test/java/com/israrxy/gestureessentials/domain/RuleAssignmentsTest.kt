package com.israrxy.gestureessentials.domain

import com.israrxy.gestureessentials.core.model.ActionSpec
import com.israrxy.gestureessentials.core.model.Constraints
import com.israrxy.gestureessentials.core.model.Rule
import com.israrxy.gestureessentials.core.model.ShortcutPreset
import com.israrxy.gestureessentials.core.model.Trigger
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class RuleAssignmentsTest {
    private val custom = Rule(
        id = "existing-screenshot",
        name = "Shake x2",
        trigger = Trigger.Shake(2),
        actions = listOf(ActionSpec.Screenshot),
        cooldownMs = 5_000,
        createdAt = 123,
    )
    private val flashlight = ShortcutPreset.SHAKE_FLASHLIGHT.newRule(1_000, 456)

    @Test
    fun enablingThePresetTakesTheGestureFromTheCustomRule() {
        val result = RuleAssignments.save(listOf(custom), flashlight.copy(enabled = true))

        val savedCustom = result.rules.single { it.id == custom.id }
        assertFalse(savedCustom.enabled)
        assertTrue(result.rules.single { it.id == flashlight.id }.enabled)
        assertEquals(listOf(custom.name), result.disabled.map { it.name })
    }

    @Test
    fun anUnassignedGestureReportsNothingSwitchedOff() {
        val result = RuleAssignments.save(listOf(custom.copy(enabled = false)), flashlight.copy(enabled = true))
        assertTrue(result.disabled.isEmpty())
        assertEquals(2, result.rules.size)
    }

    @Test
    fun savingADisabledRuleNeverTakesAGestureFromAnyone() {
        val result = RuleAssignments.save(listOf(custom), flashlight)
        assertTrue(result.disabled.isEmpty())
        assertFalse(result.rules.single { it.id == flashlight.id }.enabled)
        assertTrue(result.rules.single { it.id == custom.id }.enabled)
    }

    @Test
    fun differentShakeCountsCanCoexist() {
        val screenshot = ShortcutPreset.SHAKE_SCREENSHOT.newRule(1_000, 0).copy(enabled = true)
        val result = RuleAssignments.save(listOf(flashlight.copy(enabled = true)), screenshot)
        assertEquals(2, result.rules.count { it.enabled })
        assertTrue(result.disabled.isEmpty())
    }

    @Test
    fun editingTheSameRuleDoesNotConflictWithItself() {
        val edited = custom.copy(name = "My screenshot")
        val result = RuleAssignments.save(listOf(custom), edited)
        assertEquals(listOf(edited), result.rules)
        assertTrue(result.disabled.isEmpty())
    }

    @Test
    fun mutuallyExclusiveScreenConstraintsCanShareAGesture() {
        val awake = custom.copy(constraints = Constraints(screenOn = true))
        val asleep = flashlight.copy(enabled = true, constraints = Constraints(screenOn = false))
        assertNull(RuleAssignments.conflictFor(asleep, listOf(awake)))
        assertTrue(RuleAssignments.save(listOf(awake), asleep).disabled.isEmpty())
    }

    @Test
    fun anUnconstrainedRuleConflictsWithTheConditionalAssignment() {
        val charging = custom.copy(constraints = Constraints(chargingOnly = true))
        assertEquals(charging, RuleAssignments.conflictFor(flashlight, listOf(charging)))
    }
}
