package com.israrxy.gestureessentials.data.system

import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorManager
import android.os.Build
import android.os.PowerManager

/**
 * System permissions and exemptions that cannot be toggled from inside the
 * app. The settings screen only reports them and deep-links to the right page.
 */
class SystemAccessRepository(private val context: Context) {

    fun hasShakeSensor(): Boolean =
        context.getSystemService(SensorManager::class.java)
            ?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null

    fun hasFlashlight(): Boolean =
        context.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)

    fun supportsScreenshot(): Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.R

    fun hasDndAccess(): Boolean =
        context.getSystemService(NotificationManager::class.java)
            ?.isNotificationPolicyAccessGranted == true

    fun isIgnoringBatteryOptimizations(): Boolean =
        context.getSystemService(PowerManager::class.java)
            ?.isIgnoringBatteryOptimizations(context.packageName) == true
}
