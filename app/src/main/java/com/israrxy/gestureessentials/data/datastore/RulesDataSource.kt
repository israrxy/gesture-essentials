package com.israrxy.gestureessentials.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.israrxy.gestureessentials.core.model.Rule
import com.israrxy.gestureessentials.data.mapper.RuleJsonCodec
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject

class RulesDataSource(private val dataStore: DataStore<Preferences>) {

    constructor(context: Context) : this(context.rulesDataStore)

    private val key = stringPreferencesKey(RULES_KEY)

    fun observeRules(): Flow<List<Rule>> = dataStore.data
        .catch { emit(emptyPreferences()) }
        .map { preferences -> decode(preferences[key]) }

    suspend fun readRules(): List<Rule> {
        val preferences = dataStore.data.first()
        return decode(preferences[key])
    }

    suspend fun writeRules(rules: List<Rule>) {
        val payload = JsonArray(rules.map(RuleJsonCodec::encode)).toString()
        dataStore.edit { preferences -> preferences[key] = payload }
    }

    private fun decode(raw: String?): List<Rule> {
        if (raw.isNullOrBlank()) return emptyList()
        val array = runCatching { Json.parseToJsonElement(raw) as? JsonArray }.getOrNull()
            ?: return emptyList()
        return array.mapNotNull { element ->
            (element as? JsonObject)?.let(RuleJsonCodec::decode)
        }
    }
}
