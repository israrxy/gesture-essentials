package com.israrxy.gestureessentials.core.model

/** Stable rule IDs keep repeated switches and app updates from creating duplicates. */
enum class ShortcutPreset(
    val ruleId: String,
    val title: String,
    val description: String,
    val trigger: Trigger,
    val action: ActionSpec,
) {
    SHAKE_FLASHLIGHT(
        "preset.shake_flashlight",
        "Shake twice → flashlight",
        "Turn the flashlight on or off",
        Trigger.Shake(count = 2),
        ActionSpec.Torch(),
    ),
    SHAKE_SCREENSHOT(
        "preset.shake_screenshot",
        "Shake three times → screenshot",
        "Save a screenshot to Pictures/GestureEssentials",
        Trigger.Shake(count = 3),
        ActionSpec.Screenshot,
    ),
    SCREEN_OFF_FLASHLIGHT(
        "preset.screen_off_flashlight",
        "Screen off → flashlight off",
        "Turn off the flashlight when the screen turns off",
        Trigger.Screen(Trigger.ScreenEvent.OFF),
        ActionSpec.Torch(ActionSpec.TorchMode.OFF),
    );

    fun newRule(cooldownMs: Long, createdAt: Long): Rule = Rule(
        id = ruleId,
        name = title,
        trigger = trigger,
        actions = listOf(action),
        enabled = false,
        cooldownMs = cooldownMs,
        createdAt = createdAt,
    )

    companion object {
        fun fromRuleId(id: String): ShortcutPreset? = entries.firstOrNull { it.ruleId == id }
    }
}
