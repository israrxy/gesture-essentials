package com.israrxy.gestureessentials

import android.content.Context
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.israrxy.gestureessentials.core.model.AppInfo
import com.israrxy.gestureessentials.ui.home.HomeViewModel

/**
 * Manual dependency injection. Deliberately no Hilt/KSP: this keeps the CI
 * build fast and removes a whole class of annotation-processing failures that
 * are expensive to debug when builds only run on GitHub Actions.
 */
class AppGraph(
    context: Context,
    val appInfo: AppInfo,
) {
    // Retained for the services/repositories added in the next milestones.
    @Suppress("unused")
    private val appContext: Context = context.applicationContext

    val homeViewModelFactory = viewModelFactory {
        initializer { HomeViewModel(appInfo) }
    }
}
