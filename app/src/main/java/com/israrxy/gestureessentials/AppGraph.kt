package com.israrxy.gestureessentials

import android.content.Context
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.israrxy.gestureessentials.core.model.AppInfo
import com.israrxy.gestureessentials.data.datastore.RulesDataSource
import com.israrxy.gestureessentials.data.repository.RulesRepositoryImpl
import com.israrxy.gestureessentials.domain.repository.RulesRepository
import com.israrxy.gestureessentials.domain.usecase.DeleteRuleUseCase
import com.israrxy.gestureessentials.domain.usecase.GetRuleUseCase
import com.israrxy.gestureessentials.domain.usecase.ObserveRulesUseCase
import com.israrxy.gestureessentials.domain.usecase.SaveRuleUseCase
import com.israrxy.gestureessentials.domain.usecase.SetRuleEnabledUseCase
import com.israrxy.gestureessentials.ui.editor.EditorViewModel
import com.israrxy.gestureessentials.ui.home.HomeViewModel
import com.israrxy.gestureessentials.ui.rules.RulesViewModel

/**
 * Manual dependency injection. Deliberately no Hilt/KSP: this keeps the CI
 * build fast and removes a whole class of annotation-processing failures that
 * are expensive to debug when builds only run on GitHub Actions.
 */
class AppGraph(
    context: Context,
    val appInfo: AppInfo,
) {
    private val appContext: Context = context.applicationContext

    private val rulesRepository: RulesRepository = RulesRepositoryImpl(RulesDataSource(appContext))

    private val observeRules = ObserveRulesUseCase(rulesRepository)
    private val getRule = GetRuleUseCase(rulesRepository)
    private val saveRule = SaveRuleUseCase(rulesRepository)
    private val deleteRule = DeleteRuleUseCase(rulesRepository)
    private val setRuleEnabled = SetRuleEnabledUseCase(rulesRepository)

    val homeViewModelFactory = viewModelFactory {
        initializer { HomeViewModel(appInfo = appInfo, observeRules = observeRules) }
    }

    val rulesViewModelFactory = viewModelFactory {
        initializer {
            RulesViewModel(
                observeRules = observeRules,
                setRuleEnabled = setRuleEnabled,
                deleteRule = deleteRule,
            )
        }
    }

    fun editorViewModelFactory(ruleId: String?) = viewModelFactory {
        initializer {
            EditorViewModel(
                ruleId = ruleId,
                getRule = getRule,
                saveRule = saveRule,
            )
        }
    }
}
