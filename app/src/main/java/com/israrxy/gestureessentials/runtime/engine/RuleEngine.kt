package com.israrxy.gestureessentials.runtime.engine

import android.os.SystemClock
import com.israrxy.gestureessentials.action.ActionExecutor
import com.israrxy.gestureessentials.action.Haptics
import com.israrxy.gestureessentials.core.model.Rule
import com.israrxy.gestureessentials.core.model.TriggerEvent
import com.israrxy.gestureessentials.domain.usecase.ObserveRulesUseCase
import com.israrxy.gestureessentials.runtime.bus.TriggerBus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/**
 * Joins live events to saved rules. Rules are streamed from DataStore, so
 * enabling/disabling or editing a rule takes effect without a restart.
 */
class RuleEngine(
    private val observeRules: ObserveRulesUseCase,
    private val actionExecutor: ActionExecutor,
    private val constraints: ConstraintEvaluator,
    private val scope: CoroutineScope,
    private val haptics: Haptics,
    private val hapticsEnabled: () -> Boolean,
    private val matcher: TriggerMatcher = TriggerMatcher(),
) {

    @Volatile
    private var rules: List<Rule> = emptyList()

    /** Per-rule last run, in elapsed-realtime millis. Event loop only. */
    private val lastRunAt = mutableMapOf<String, Long>()

    private var rulesJob: Job? = null
    private var eventsJob: Job? = null

    fun start(bus: TriggerBus) {
        if (rulesJob?.isActive == true) return

        rulesJob = scope.launch {
            observeRules().collect { latest -> rules = latest }
        }
        eventsJob = scope.launch {
            bus.events.collect { event -> handle(event) }
        }
    }

    private fun handle(event: TriggerEvent) {
        val now = SystemClock.elapsedRealtime()
        val due = rules.filter { rule -> canRun(rule, event, now) }
        if (due.isEmpty()) return

        // One tick per event, not per rule, so overlapping rules do not buzz twice.
        if (hapticsEnabled()) haptics.tap()

        due.forEach { rule ->
            lastRunAt[rule.id] = now
            scope.launch { runCatching { actionExecutor.execute(rule.actions) } }
        }
    }

    private fun canRun(rule: Rule, event: TriggerEvent, now: Long): Boolean {
        if (!rule.enabled) return false
        if (!matcher.matches(rule.trigger, event)) return false
        if (!constraints.allows(rule.constraints)) return false

        val previous = lastRunAt[rule.id] ?: return true
        return now - previous >= rule.cooldownMs
    }
}
