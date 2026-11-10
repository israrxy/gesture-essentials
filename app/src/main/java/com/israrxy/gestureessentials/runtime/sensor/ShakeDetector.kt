package com.israrxy.gestureessentials.runtime.sensor

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.SystemClock
import com.israrxy.gestureessentials.core.model.TriggerEvent
import kotlin.math.abs
import kotlin.math.sqrt

/**
 * Accelerometer shake detector. After every peak it emits the number of shakes
 * seen in the last second, so rules asking for two or three shakes can coexist
 * and each fires at its own count.
 */
class ShakeDetector(
    context: Context,
    private val emit: (TriggerEvent) -> Unit,
    sensitivity: Int = 55,
) : SensorEventListener {

    private val sensorManager = context.getSystemService(SensorManager::class.java)
    private val accelerometer = sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

    /** 0 = only violent shakes, 100 = very light shakes. */
    private val threshold = 4.2f - (sensitivity.coerceIn(0, 100) / 100f) * 3.0f

    private val peaks = ArrayDeque<Long>()
    private var gravity = 9.81f
    private var lastPeakAt = 0L

    fun start() {
        sensorManager?.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME)
    }

    fun stop() {
        sensorManager?.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent) {
        val x = event.values[0]
        val y = event.values[1]
        val z = event.values[2]
        val magnitude = sqrt(x * x + y * y + z * z)

        // Low-pass gravity estimate; the difference is the linear acceleration.
        gravity = gravity * 0.8f + magnitude * 0.2f
        if (abs(magnitude - gravity) < threshold) return

        val now = SystemClock.elapsedRealtime()
        if (now - lastPeakAt < MIN_PEAK_GAP_MS) return
        lastPeakAt = now

        peaks.addLast(now)
        while (peaks.isNotEmpty() && now - peaks.first() > WINDOW_MS) {
            peaks.removeFirst()
        }
        emit(TriggerEvent.Shake(peaks.size))
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit

    private companion object {
        const val WINDOW_MS = 1_000L
        const val MIN_PEAK_GAP_MS = 180L
    }
}
