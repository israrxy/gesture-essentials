package com.israrxy.gestureessentials.ui.navigation

object Routes {
    const val HOME = "home"
    const val RULES = "rules"

    /** Optional argument so one destination serves both "new" and "edit". */
    const val EDITOR_PATTERN = "editor?ruleId={ruleId}"

    fun editor(ruleId: String?): String =
        if (ruleId.isNullOrBlank()) "editor" else "editor?ruleId=$ruleId"
}
