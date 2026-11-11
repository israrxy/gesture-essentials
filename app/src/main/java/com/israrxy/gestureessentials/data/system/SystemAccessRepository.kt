package com.israrxy.gestureessentials.data.system

import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorManager
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
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

    /**
     * Uses the same camera check as [com.israrxy.gestureessentials.action.handlers.TorchHandler],
     * so a disabled row always means the torch action would really fail.
     */
    fun hasFlashlight(): Boolean = runCatching {
        val manager = context.getSystemService(CameraManager::class.java) ?: return@runCatching false
        manager.cameraIdList.any { id ->
            manager.getCameraCharacteristics(id).get(CameraCharacteristics.FLASH_INFO_AVAILABLE) == true
        }
    }.getOrElse {
        context.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)
    }

    fun supportsScreenshot(): Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.R

    fun hasDndAccess(): Boolean =
        context.getSystemService(NotificationManager::class.java)
            ?.isNotificationPolicyAccessGranted == true

    fun isIgnoringBatteryOptimizations(): Boolean =
        context.getSystemService(PowerManager::class.java)
            ?.isIgnoringBatteryOptimizations(context.packageName) == true
}
