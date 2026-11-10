package com.israrxy.gestureessentials.action

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

/** Short confirmation tick when a rule actually runs. */
class Haptics(context: Context) {

    private val vibrator: Vibrator? =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            context.getSystemService(VibratorManager::class.java)?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Vibrator::class.java)
        }

    fun tap() {
        val device = vibrator ?: return
        if (!device.hasVibrator()) return
        runCatching {
            device.vibrate(
                VibrationEffect.createOneShot(TAP_MS, VibrationEffect.DEFAULT_AMPLITUDE),
            )
        }
    }

    private companion object {
        const val TAP_MS = 18L
    }
}
