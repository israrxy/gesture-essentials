package com.israrxy.gestureessentials.core.model

/**
 * Immutable facts about the installed build, read once at startup so that
 * ViewModels never need a Context.
 */
data class AppInfo(
    val versionName: String,
    val versionCode: Long,
)
