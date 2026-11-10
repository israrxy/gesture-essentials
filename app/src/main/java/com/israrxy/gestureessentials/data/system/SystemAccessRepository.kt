package com.israrxy.gestureessentials.data.system

import android.app.NotificationManager
import android.content.Context
import android.os.PowerManager

/**
 * System permissions and exemptions that cannot be toggled from inside the
 * app. The settings screen only reports them and deep-links to the right page.
 */
class SystemAccessRepository(private val context: Context) {

    fun hasDndAccess(): Boolean =
        context.getSystemService(NotificationManager::class.java)
            ?.isNotificationPolicyAccessGranted == true

    fun isIgnoringBatteryOptimizations(): Boolean =
        context.getSystemService(PowerManager::class.java)
            ?.isIgnoringBatteryOptimizations(context.packageName) == true
}
