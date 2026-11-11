package com.israrxy.gestureessentials.domain

import com.israrxy.gestureessentials.core.model.Rule
import com.israrxy.gestureessentials.core.model.Trigger

/**
 * One gesture runs one rule. Enabling a rule therefore switches off any other
 * enabled rule that owns the same gesture, instead of refusing the switch:
 * the user's latest choice should take effect. The caller reports what changed.
 */
object RuleAssignments {

    data class Result(val rules: List<Rule>, val disabled: List<Rule>)

    /** What currently owns the gesture, for rendering and conflict messages. */
    fun conflictFor(candidate: Rule, rules: List<Rule>): Rule? = conflicts(candidate, rules).firstOrNull()

    fun save(rules: List<Rule>, candidate: Rule): Result {
        val displaced = if (candidate.enabled) conflicts(candidate, rules) else emptyList()
        val disabledIds = displaced.mapTo(HashSet()) { it.id }
        val updated = rules.map { rule ->
            if (rule.id in disabledIds) rule.copy(enabled = false) else rule
        }.filterNot { it.id == candidate.id } + candidate
        return Result(updated, displaced)
    }

    private fun conflicts(candidate: Rule, rules: List<Rule>): List<Rule> = rules.filter { other ->
        other.id != candidate.id && other.enabled &&
            sameGesture(candidate.trigger, other.trigger) &&
            (candidate.constraints.screenOn == null || other.constraints.screenOn == null ||
                candidate.constraints.screenOn == other.constraints.screenOn)
    }

    private fun sameGesture(first: Trigger, second: Trigger): Boolean =
        if (first is Trigger.AppForeground && second is Trigger.AppForeground) {
            first.packageName == second.packageName
        } else {
            first == second
        }
}
