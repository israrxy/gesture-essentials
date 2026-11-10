package com.israrxy.gestureessentials

import android.content.Context
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.israrxy.gestureessentials.action.ActionExecutor
import com.israrxy.gestureessentials.action.ActionRegistry
import com.israrxy.gestureessentials.action.Haptics
import com.israrxy.gestureessentials.action.TtsSpeaker
import com.israrxy.gestureessentials.action.handlers.DelayHandler
import com.israrxy.gestureessentials.action.handlers.DndHandler
import com.israrxy.gestureessentials.action.handlers.LockScreenHandler
import com.israrxy.gestureessentials.action.handlers.MediaHandler
import com.israrxy.gestureessentials.action.handlers.OpenUrlHandler
import com.israrxy.gestureessentials.action.handlers.ScreenshotHandler
import com.israrxy.gestureessentials.action.handlers.SpeakHandler
import com.israrxy.gestureessentials.action.handlers.TorchHandler
import com.israrxy.gestureessentials.action.handlers.VibrateHandler
import com.israrxy.gestureessentials.core.model.AppInfo
import com.israrxy.gestureessentials.core.model.AppSettings
import com.israrxy.gestureessentials.data.datastore.RulesDataSource
import com.israrxy.gestureessentials.data.datastore.SettingsDataSource
import com.israrxy.gestureessentials.data.repository.RulesRepositoryImpl
import com.israrxy.gestureessentials.data.repository.SettingsRepositoryImpl
import com.israrxy.gestureessentials.data.system.ServiceStatusRepository
import com.israrxy.gestureessentials.data.system.SystemAccessRepository
import com.israrxy.gestureessentials.domain.repository.RulesRepository
import com.israrxy.gestureessentials.domain.repository.SettingsRepository
import com.israrxy.gestureessentials.domain.usecase.DeleteRuleUseCase
import com.israrxy.gestureessentials.domain.usecase.GetRuleUseCase
import com.israrxy.gestureessentials.domain.usecase.ObserveRulesUseCase
import com.israrxy.gestureessentials.domain.usecase.ObserveSettingsUseCase
import com.israrxy.gestureessentials.domain.usecase.SaveRuleUseCase
import com.israrxy.gestureessentials.domain.usecase.SetPresetEnabledUseCase
import com.israrxy.gestureessentials.domain.usecase.SetRuleEnabledUseCase
import com.israrxy.gestureessentials.domain.usecase.UpdateSettingsUseCase
import com.israrxy.gestureessentials.runtime.AccessibilityServiceRef
import com.israrxy.gestureessentials.runtime.bus.TriggerBus
import com.israrxy.gestureessentials.runtime.engine.ConstraintEvaluator
import com.israrxy.gestureessentials.runtime.engine.RuleEngine
import com.israrxy.gestureessentials.ui.editor.EditorViewModel
import com.israrxy.gestureessentials.ui.home.HomeViewModel
import com.israrxy.gestureessentials.ui.rules.RulesViewModel
import com.israrxy.gestureessentials.ui.settings.SettingsViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

/**
 * Manual dependency injection. Deliberately no Hilt/KSP: this keeps the CI
 * build fast and removes a whole class of annotation-processing failures that
 * are expensive to debug when builds only run on GitHub Actions.
 *
 * Owns the process-wide runtime: the trigger bus, the rule engine and the
 * action handlers. The accessibility service feeds the bus.
 */
class AppGraph(
    context: Context,
    val appInfo: AppInfo,
) {
    private val appContext: Context = context.applicationContext

    /** Outlives any screen; the engine must keep running in the background. */
    val appScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    val triggerBus = TriggerBus()

    private val rulesRepository: RulesRepository = RulesRepositoryImpl(RulesDataSource(appContext))
    private val settingsRepository: SettingsRepository =
        SettingsRepositoryImpl(SettingsDataSource(appContext))

    private val observeRules = ObserveRulesUseCase(rulesRepository)
    private val getRule = GetRuleUseCase(rulesRepository)
    private val saveRule = SaveRuleUseCase(rulesRepository)
    private val deleteRule = DeleteRuleUseCase(rulesRepository)
    private val setRuleEnabled = SetRuleEnabledUseCase(rulesRepository)
    private val setPresetEnabled = SetPresetEnabledUseCase(rulesRepository)

    private val observeSettings = ObserveSettingsUseCase(settingsRepository)
    private val updateSettings = UpdateSettingsUseCase(settingsRepository)

    /**
     * Cached for background components that cannot suspend: the accessibility
     * service reads shake sensitivity from here on every sensor event.
     */
    val settingsState: StateFlow<AppSettings> = observeSettings()
        .stateIn(appScope, SharingStarted.Eagerly, AppSettings())

    private val serviceStatus = ServiceStatusRepository(appContext)
    private val systemAccess = SystemAccessRepository(appContext)
    private val ttsSpeaker = TtsSpeaker(appContext)
    private val haptics = Haptics(appContext)

    private val actionExecutor = ActionExecutor(
        ActionRegistry(
            listOf(
                TorchHandler(appContext),
                MediaHandler(appContext),
                LockScreenHandler { AccessibilityServiceRef.service },
                ScreenshotHandler(appContext) { AccessibilityServiceRef.service },
                VibrateHandler(appContext),
                DndHandler(appContext),
                OpenUrlHandler(appContext),
                SpeakHandler(ttsSpeaker),
                DelayHandler(),
            ),
        ),
    )

    private val ruleEngine = RuleEngine(
        observeRules = observeRules,
        actionExecutor = actionExecutor,
        constraints = ConstraintEvaluator(appContext),
        scope = appScope,
        haptics = haptics,
        hapticsEnabled = { settingsState.value.hapticsEnabled },
    )

    init {
        ruleEngine.start(triggerBus)
    }

    val homeViewModelFactory = viewModelFactory {
        initializer {
            HomeViewModel(
                appInfo = appInfo,
                observeRules = observeRules,
                serviceStatus = serviceStatus,
            )
        }
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
                deleteRule = deleteRule,
                defaultCooldownMs = { settingsState.value.defaultCooldownMs },
            )
        }
    }

    val settingsViewModelFactory = viewModelFactory {
        initializer {
            SettingsViewModel(
                appInfo = appInfo,
                observeSettings = observeSettings,
                observeRules = observeRules,
                updateSettings = updateSettings,
                setPresetEnabled = setPresetEnabled,
                setRuleEnabled = setRuleEnabled,
                serviceStatus = serviceStatus,
                systemAccess = systemAccess,
            )
        }
    }
}
