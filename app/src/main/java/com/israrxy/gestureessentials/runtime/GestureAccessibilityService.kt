package com.israrxy.gestureessentials.runtime

import android.accessibilityservice.AccessibilityService
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.view.accessibility.AccessibilityEvent
import androidx.core.content.ContextCompat
import com.israrxy.gestureessentials.GestureEssentialsApp
import com.israrxy.gestureessentials.core.model.TriggerEvent
import com.israrxy.gestureessentials.runtime.screen.ScreenToggleDetector
import com.israrxy.gestureessentials.runtime.sensor.ShakeDetector

/**
 * The only background listener. It observes input and system events and pushes
 * them onto the trigger bus; all rule evaluation lives in the app process.
 */
class GestureAccessibilityService : AccessibilityService() {

    private val triggerBus
        get() = (application as GestureEssentialsApp).graph.triggerBus

    private var shakeDetector: ShakeDetector? = null
    private var screenDetector: ScreenToggleDetector? = null
    private var receiver: BroadcastReceiver? = null

    override fun onServiceConnected() {
        super.onServiceConnected()
        AccessibilityServiceRef.service = this
        if (receiver != null) return

        screenDetector = ScreenToggleDetector(triggerBus::emit)
        shakeDetector = ShakeDetector(this, emit = triggerBus::emit).also { it.start() }
        registerSystemReceiver()
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null || event.eventType != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) return
        val packageName = event.packageName?.toString() ?: return
        if (packageName == this.packageName) return
        triggerBus.emit(TriggerEvent.AppForeground(packageName))
    }

    override fun onInterrupt() = Unit

    override fun onUnbind(intent: Intent?): Boolean {
        release()
        return super.onUnbind(intent)
    }

    override fun onDestroy() {
        release()
        super.onDestroy()
    }

    private fun registerSystemReceiver() {
        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_SCREEN_ON)
            addAction(Intent.ACTION_SCREEN_OFF)
            addAction(Intent.ACTION_USER_PRESENT)
            addAction(Intent.ACTION_POWER_CONNECTED)
            addAction(Intent.ACTION_POWER_DISCONNECTED)
            addAction(Intent.ACTION_HEADSET_PLUG)
        }

        val callback = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                if (intent == null) return
                val detector = screenDetector ?: return
                when (intent.action) {
                    Intent.ACTION_SCREEN_ON -> detector.onScreenOn()
                    Intent.ACTION_SCREEN_OFF -> detector.onScreenOff()
                    Intent.ACTION_USER_PRESENT -> detector.onUnlocked()
                    Intent.ACTION_POWER_CONNECTED -> triggerBus.emit(TriggerEvent.Charger(true))
                    Intent.ACTION_POWER_DISCONNECTED -> triggerBus.emit(TriggerEvent.Charger(false))
                    Intent.ACTION_HEADSET_PLUG -> {
                        val plugged = intent.getIntExtra("state", 0) == 1
                        triggerBus.emit(TriggerEvent.Headset(plugged))
                    }
                }
            }
        }

        receiver = callback
        ContextCompat.registerReceiver(
            this,
            callback,
            filter,
            ContextCompat.RECEIVER_EXPORTED,
        )
    }

    private fun release() {
        AccessibilityServiceRef.service = null
        shakeDetector?.stop()
        shakeDetector = null
        screenDetector = null
        receiver?.let { runCatching { unregisterReceiver(it) } }
        receiver = null
    }
}
