package com.israrxy.gestureessentials.domain

import com.israrxy.gestureessentials.core.model.ActionSpec
import com.israrxy.gestureessentials.core.model.Constraints
import com.israrxy.gestureessentials.core.model.Rule
import com.israrxy.gestureessentials.core.model.ShortcutPreset
import com.israrxy.gestureessentials.core.model.Trigger
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertThrows
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
    fun enablingThePresetReportsTheExistingCustomRule() {
        val rules = listOf(custom)
        val error = assertThrows(RuleConflictException::class.java) {
            RuleAssignments.save(rules, flashlight.copy(enabled = true))
        }
        assertTrue(error.message.orEmpty().contains(custom.name))
        assertEquals(listOf(custom), rules)
    }

    @Test
    fun turningOffACustomRuleFreesThePresetWithoutDeletingTheCustomRule() {
        val disabled = RuleAssignments.save(listOf(custom), custom.copy(enabled = false))
        val result = RuleAssignments.save(disabled, flashlight.copy(enabled = true))
        assertEquals(custom.copy(enabled = false), result.first { it.id == custom.id })
        assertTrue(result.first { it.id == flashlight.id }.enabled)
    }

    @Test
    fun disabledRulesCanBeSavedForAnAlreadyAssignedGesture() {
        val result = RuleAssignments.save(listOf(custom), flashlight)
        assertEquals(2, result.size)
        assertFalse(result.last().enabled)
    }

    @Test
    fun differentShakeCountsCanCoexist() {
        val screenshot = ShortcutPreset.SHAKE_SCREENSHOT.newRule(1_000, 0).copy(enabled = true)
        val result = RuleAssignments.save(listOf(flashlight.copy(enabled = true)), screenshot)
        assertEquals(2, result.size)
    }

    @Test
    fun editingTheSameRuleDoesNotConflictWithItself() {
        val edited = custom.copy(name = "My screenshot")
        assertEquals(listOf(edited), RuleAssignments.save(listOf(custom), edited))
    }

    @Test
    fun mutuallyExclusiveScreenConstraintsCanShareAGesture() {
        val awake = custom.copy(constraints = Constraints(screenOn = true))
        val asleep = flashlight.copy(enabled = true, constraints = Constraints(screenOn = false))
        assertNull(RuleAssignments.conflictFor(asleep, listOf(awake)))
        assertEquals(2, RuleAssignments.save(listOf(awake), asleep).size)
    }

    @Test
    fun anUnconstrainedRuleConflictsWithTheConditionalAssignment() {
        val charging = custom.copy(constraints = Constraints(chargingOnly = true))
        assertEquals(charging, RuleAssignments.conflictFor(flashlight, listOf(charging)))
    }
}
