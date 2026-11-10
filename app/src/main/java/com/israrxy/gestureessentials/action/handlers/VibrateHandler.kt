package com.israrxy.gestureessentials.action.handlers

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import com.israrxy.gestureessentials.action.ActionHandler
import com.israrxy.gestureessentials.core.model.ActionSpec
import com.israrxy.gestureessentials.core.model.ActionType

/** Short haptic confirmation. Needs only the VIBRATE permission. */
class VibrateHandler(context: Context) : ActionHandler {

    override val type = ActionType.VIBRATE

    private val vibrator: Vibrator? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        context.getSystemService(VibratorManager::class.java)?.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Vibrator::class.java)
    }

    override suspend fun execute(action: ActionSpec) {
        val spec = action as? ActionSpec.Vibrate ?: return

        val timings = when (spec.pattern) {
            ActionSpec.VibratePattern.SHORT -> longArrayOf(0, 80)
            ActionSpec.VibratePattern.DOUBLE -> longArrayOf(0, 60, 90, 60)
            ActionSpec.VibratePattern.LONG -> longArrayOf(0, 400)
        }

        runCatching {
            vibrator?.vibrate(VibrationEffect.createWaveform(timings, -1))
        }
    }
}
