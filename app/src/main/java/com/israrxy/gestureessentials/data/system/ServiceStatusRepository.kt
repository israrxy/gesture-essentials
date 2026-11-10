package com.israrxy.gestureessentials.data.system

import android.content.ComponentName
import android.content.Context
import android.provider.Settings
import com.israrxy.gestureessentials.runtime.GestureAccessibilityService

/**
 * Authoritative service status, read from the system rather than from app
 * memory: enabled in settings but not yet bound still counts as enabled.
 */
class ServiceStatusRepository(private val context: Context) {

    fun isEnabled(): Boolean {
        val expected = ComponentName(context, GestureAccessibilityService::class.java)
        val enabled = Settings.Secure.getString(
            context.contentResolver,
            Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES,
        ) ?: return false

        return enabled.split(':').any { entry ->
            ComponentName.unflattenFromString(entry) == expected
        }
    }
}
