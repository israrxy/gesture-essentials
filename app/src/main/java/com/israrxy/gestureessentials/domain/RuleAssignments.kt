package com.israrxy.gestureessentials.domain

import com.israrxy.gestureessentials.core.model.Rule
import com.israrxy.gestureessentials.core.model.Trigger

/** Check inside the repository's transaction as well as when rendering switches. */
object RuleAssignments {
    fun conflictFor(candidate: Rule, rules: List<Rule>): Rule? = rules.firstOrNull { other ->
        other.id != candidate.id && other.enabled &&
            sameGesture(candidate.trigger, other.trigger) &&
            (candidate.constraints.screenOn == null || other.constraints.screenOn == null ||
                candidate.constraints.screenOn == other.constraints.screenOn)
    }

    fun save(rules: List<Rule>, candidate: Rule): List<Rule> {
        if (candidate.enabled) {
            conflictFor(candidate, rules)?.let { throw RuleConflictException(it.name) }
        }
        return rules.filterNot { it.id == candidate.id } + candidate
    }

    private fun sameGesture(first: Trigger, second: Trigger): Boolean =
        if (first is Trigger.AppForeground && second is Trigger.AppForeground) {
            first.packageName == second.packageName
        } else {
            first == second
        }
}

class RuleConflictException(ruleName: String) : IllegalStateException(
    "This gesture is used by “$ruleName”. Turn that rule off first, or save this rule disabled.",
)
