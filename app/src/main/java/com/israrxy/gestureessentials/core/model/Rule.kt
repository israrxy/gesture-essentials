package com.israrxy.gestureessentials.core.model

/**
 * A single automation: when [trigger] fires and [constraints] hold, run every
 * action in order. Cooldowns prevent a gesture from firing twice.
 */
data class Rule(
    val id: String,
    val name: String,
    val trigger: Trigger,
    val actions: List<ActionSpec>,
    val enabled: Boolean = true,
    val constraints: Constraints = Constraints(),
    val cooldownMs: Long = 1_000L,
    val createdAt: Long = 0L,
)

/** Optional guards evaluated before a rule runs. `null` means "don't care". */
data class Constraints(
    val screenOn: Boolean? = null,
    val chargingOnly: Boolean = false,
)
