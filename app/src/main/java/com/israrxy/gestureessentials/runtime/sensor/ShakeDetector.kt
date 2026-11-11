package com.israrxy.gestureessentials.runtime.sensor

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import com.israrxy.gestureessentials.core.model.AppSettings
import com.israrxy.gestureessentials.core.model.TriggerEvent
import kotlin.math.abs
import kotlin.math.sqrt

/**
 * Accelerometer peaks are grouped into one completed sequence. Only its final
 * count reaches the engine, so a triple shake never also fires a double shake.
 */
class ShakeDetector(
    context: Context,
    private val emit: (TriggerEvent) -> Unit,
    /** Read on every sensor event so a Settings change applies immediately. */
    private val sensitivityProvider: () -> Int = { AppSettings.DEFAULT_SHAKE_SENSITIVITY },
) : SensorEventListener {

    private val sensorManager = context.getSystemService(SensorManager::class.java)
    private val accelerometer = sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

    private val handler = Handler(Looper.getMainLooper())
    private val sequence = ShakeSequence { count -> emit(TriggerEvent.Shake(count)) }
    private val finishSequence = object : Runnable {
        override fun run() {
            if (!sequence.finishIfQuiet(SystemClock.elapsedRealtime())) {
                // A handler may wake a hair early; never drop a completed burst.
                handler.postDelayed(this, 40L)
            }
        }
    }
    private var gravity: Float? = null
    private var lastSensitivity = sensitivityProvider()

    /** One physical jolt counts once; the sensor must settle before re-arming. */
    private var armed = true

    fun start() {
        if (accelerometer != null) {
            sensorManager?.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME)
        }
    }

    fun stop() {
        sensorManager?.unregisterListener(this)
        handler.removeCallbacks(finishSequence)
        sequence.reset()
        gravity = null
        armed = true
    }

    override fun onSensorChanged(event: SensorEvent) {
        val x = event.values[0]
        val y = event.values[1]
        val z = event.values[2]
        val magnitude = sqrt(x * x + y * y + z * z)

        val previousGravity = gravity
        if (previousGravity == null) {
            gravity = magnitude
            return
        }
        val estimate = previousGravity * 0.8f + magnitude * 0.2f
        gravity = estimate

        val sensitivity = sensitivityProvider().coerceIn(0, 100)
        if (sensitivity != lastSensitivity) {
            sequence.reset()
            handler.removeCallbacks(finishSequence)
            lastSensitivity = sensitivity
            armed = true
        }
        // Lower sensitivity requires firmer movement, but stays usable at the
        // 25% default: ordinary deliberate shakes clear about 3 m/s².
        val threshold = 3.6f - (sensitivity / 100f) * 2.4f
        val deviation = abs(magnitude - estimate)
        if (deviation < threshold * REARM_FRACTION) armed = true
        if (!armed || deviation < threshold) return
        armed = false

        val now = SystemClock.elapsedRealtime()
        if (sequence.onPeak(now)) {
            handler.removeCallbacks(finishSequence)
            handler.postDelayed(finishSequence, sequence.quietWindow())
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit

    private companion object {
        /** Below this fraction of the trigger level the sensor counts as settled. */
        const val REARM_FRACTION = 0.5f
    }
}
