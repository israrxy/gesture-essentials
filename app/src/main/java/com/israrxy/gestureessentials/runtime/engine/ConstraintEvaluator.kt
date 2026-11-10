package com.israrxy.gestureessentials.runtime.engine

import android.content.Context
import android.os.BatteryManager
import android.os.PowerManager
import com.israrxy.gestureessentials.core.model.Constraints

/** Evaluates the optional guards on a rule before its actions run. */
class ConstraintEvaluator(context: Context) {

    private val powerManager = context.getSystemService(PowerManager::class.java)
    private val batteryManager = context.getSystemService(BatteryManager::class.java)

    fun allows(constraints: Constraints): Boolean {
        constraints.screenOn?.let { expected ->
            if (powerManager.isInteractive != expected) return false
        }
        if (constraints.chargingOnly && batteryManager?.isCharging != true) return false
        return true
    }
}
