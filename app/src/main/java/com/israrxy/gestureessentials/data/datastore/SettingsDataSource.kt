package com.israrxy.gestureessentials.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.israrxy.gestureessentials.core.model.AppSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

/**
 * App-wide preferences. Shares the existing DataStore file so the process only
 * ever opens one DataStore instance.
 */
class SettingsDataSource(private val dataStore: DataStore<Preferences>) {

    constructor(context: Context) : this(context.rulesDataStore)

    fun observeSettings(): Flow<AppSettings> = dataStore.data
        .catch { emit(emptyPreferences()) }
        .map(::decode)

    /**
     * Read-modify-write inside a single DataStore edit, so rapid updates from a
     * slider cannot overwrite each other with stale values.
     */
    suspend fun update(transform: (AppSettings) -> AppSettings) {
        dataStore.edit { preferences ->
            val next = transform(decode(preferences))
            preferences[THEME_KEY] = next.themeMode.key
            preferences[DYNAMIC_COLOR_KEY] = next.dynamicColor
            preferences[SHAKE_SENSITIVITY_KEY] = next.shakeSensitivity.coerceIn(0, 100)
            preferences[HAPTICS_KEY] = next.hapticsEnabled
            preferences[DEFAULT_COOLDOWN_KEY] = next.defaultCooldownMs.coerceIn(0, MAX_COOLDOWN_MS)
        }
    }

    private fun decode(preferences: Preferences): AppSettings = AppSettings(
        themeMode = AppSettings.ThemeMode.fromKey(preferences[THEME_KEY]),
        dynamicColor = preferences[DYNAMIC_COLOR_KEY] ?: true,
        shakeSensitivity = (preferences[SHAKE_SENSITIVITY_KEY]
            ?: AppSettings.DEFAULT_SHAKE_SENSITIVITY).coerceIn(0, 100),
        hapticsEnabled = preferences[HAPTICS_KEY] ?: true,
        defaultCooldownMs = (preferences[DEFAULT_COOLDOWN_KEY] ?: 1_000L)
            .coerceIn(0, MAX_COOLDOWN_MS),
    )

    private companion object {
        val THEME_KEY = stringPreferencesKey("settings_theme_mode")
        val DYNAMIC_COLOR_KEY = booleanPreferencesKey("settings_dynamic_color")
        val SHAKE_SENSITIVITY_KEY = intPreferencesKey("settings_shake_sensitivity")
        val HAPTICS_KEY = booleanPreferencesKey("settings_haptics")
        val DEFAULT_COOLDOWN_KEY = longPreferencesKey("settings_default_cooldown_ms")
        const val MAX_COOLDOWN_MS = 60_000L
    }
}
