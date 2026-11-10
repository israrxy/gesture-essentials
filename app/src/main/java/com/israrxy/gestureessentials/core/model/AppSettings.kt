package com.israrxy.gestureessentials.core.model

/**
 * Preferences that apply to the whole app rather than to a single rule.
 * Defaults are intentionally the safe, least surprising choice.
 */
data class AppSettings(
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val dynamicColor: Boolean = true,
    /** 0 = only violent shakes, 100 = very light shakes. */
    val shakeSensitivity: Int = 55,
    val hapticsEnabled: Boolean = true,
    /** Cooldown applied to newly created rules. */
    val defaultCooldownMs: Long = 1_000L,
) {
    enum class ThemeMode(val key: String) {
        SYSTEM("system"),
        LIGHT("light"),
        DARK("dark");

        companion object {
            fun fromKey(key: String?): ThemeMode =
                entries.firstOrNull { it.key == key } ?: SYSTEM
        }
    }
}
