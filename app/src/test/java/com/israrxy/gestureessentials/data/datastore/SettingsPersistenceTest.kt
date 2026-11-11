package com.israrxy.gestureessentials.data.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.israrxy.gestureessentials.core.model.ActionSpec
import com.israrxy.gestureessentials.core.model.AppSettings
import com.israrxy.gestureessentials.core.model.ShortcutPreset
import com.israrxy.gestureessentials.data.repository.RulesRepositoryImpl
import com.israrxy.gestureessentials.domain.usecase.SetPresetEnabledUseCase
import com.israrxy.gestureessentials.domain.usecase.SetRuleEnabledUseCase
import java.io.File
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

class SettingsPersistenceTest {
    @get:Rule val folder = TemporaryFolder()
    private lateinit var file: File
    private lateinit var job: Job
    private lateinit var store: DataStore<Preferences>

    @Before
    fun setUp() {
        file = File(folder.root, "preferences.preferences_pb")
        openStore()
    }

    @After
    fun tearDown() = runBlocking { job.cancelAndJoin() }

    @Test
    fun freshSettingsUseLowSensitivityAndDoNotSeedEnabledPresets() = runBlocking {
        assertEquals(25, SettingsDataSource(store).observeSettings().first().shakeSensitivity)
        assertTrue(RulesDataSource(store).readRules().isEmpty())
    }

    @Test
    fun legacyCustomRuleAndChosenSensitivitySurvivePreferenceUpdates() = runBlocking {
        seedLegacyPreferences()
        SettingsDataSource(store).update { it.copy(hapticsEnabled = false) }
        val saved = RulesDataSource(store).readRules().single()
        assertEquals("existing-screenshot", saved.id)
        assertEquals(listOf(ActionSpec.Screenshot), saved.actions)
        assertEquals(5_000L, saved.cooldownMs)
        assertEquals(55, SettingsDataSource(store).observeSettings().first().shakeSensitivity)
    }

    @Test
    fun enablingTheFlashlightPresetTakesOverTheExistingShakeRule() = runBlocking {
        seedLegacyPreferences()
        val repository = RulesRepositoryImpl(RulesDataSource(store))
        val setPreset = SetPresetEnabledUseCase(repository)

        val disabled = setPreset(ShortcutPreset.SHAKE_FLASHLIGHT, true, 2_000)

        assertEquals(listOf("Shake x2"), disabled)
        val rules = repository.observeRules().first()
        assertFalse(rules.single { it.id == "existing-screenshot" }.enabled)
        assertTrue(rules.single { it.id == ShortcutPreset.SHAKE_FLASHLIGHT.ruleId }.enabled)
    }

    @Test
    fun presetSwitchesAndAppearancePersistTogetherWithoutLosingCustomRules() = runBlocking {
        seedLegacyPreferences()
        val repository = RulesRepositoryImpl(RulesDataSource(store))
        val setPreset = SetPresetEnabledUseCase(repository)
        val setEnabled = SetRuleEnabledUseCase(repository)
        val settings = SettingsDataSource(store)

        assertEquals(listOf("Shake x2"), setPreset(ShortcutPreset.SHAKE_FLASHLIGHT, true, 2_000))
        setEnabled("existing-screenshot", false)
        coroutineScope {
            repeat(8) {
                launch { setPreset(ShortcutPreset.SHAKE_FLASHLIGHT, true, 2_000) }
                launch { settings.update { it.copy(themeMode = AppSettings.ThemeMode.DARK) } }
                launch { settings.update { it.copy(shakeSensitivity = 25) } }
            }
        }
        setPreset(ShortcutPreset.SHAKE_FLASHLIGHT, false, 500)
        setPreset(ShortcutPreset.SHAKE_FLASHLIGHT, true, 500)

        job.cancelAndJoin()
        openStore()
        val restored = RulesDataSource(store).readRules()
        assertEquals(2, restored.size)
        assertFalse(restored.first { it.id == "existing-screenshot" }.enabled)
        val preset = restored.single { it.id == ShortcutPreset.SHAKE_FLASHLIGHT.ruleId }
        assertTrue(preset.enabled)
        assertEquals(2_000L, preset.cooldownMs)
        assertEquals(AppSettings.ThemeMode.DARK, SettingsDataSource(store).observeSettings().first().themeMode)
        assertEquals(25, SettingsDataSource(store).observeSettings().first().shakeSensitivity)
    }

    private fun openStore() {
        job = SupervisorJob()
        store = PreferenceDataStoreFactory.create(
            scope = CoroutineScope(job + Dispatchers.IO),
            produceFile = { file },
        )
    }

    private suspend fun seedLegacyPreferences() {
        store.edit { preferences ->
            preferences[intPreferencesKey("settings_shake_sensitivity")] = 55
            preferences[stringPreferencesKey("rules_json")] = """
                [{"id":"existing-screenshot","name":"Shake x2","enabled":true,
                  "cooldownMs":5000,"createdAt":123,
                  "trigger":{"type":"shake","count":2,"sensitivity":55},
                  "actions":[{"type":"screenshot"}],"constraints":{"chargingOnly":false}}]
            """.trimIndent()
        }
    }
}
