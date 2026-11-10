package com.israrxy.gestureessentials.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore

/**
 * Single DataStore instance for the whole process. Declaring it as a top-level
 * delegate is mandatory: creating two DataStores for the same file throws.
 */
val Context.rulesDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "gesture_essentials_rules",
)

internal const val RULES_KEY = "rules_json"
