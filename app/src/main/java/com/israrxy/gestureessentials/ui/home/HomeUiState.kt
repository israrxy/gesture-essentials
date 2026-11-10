package com.israrxy.gestureessentials.ui.home

/** Everything the Home screen renders. Immutable, produced by [HomeViewModel]. */
data class HomeUiState(
    val versionName: String = "",
    val serviceEnabled: Boolean = false,
    val ruleCount: Int = 0,
    val activeRuleCount: Int = 0,
)

/** One-shot user intents. The screen never mutates state directly. */
sealed interface HomeAction {
    data object EnableService : HomeAction
    data object OpenRules : HomeAction
}
