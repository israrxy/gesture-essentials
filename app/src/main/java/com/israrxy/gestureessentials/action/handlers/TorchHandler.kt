package com.israrxy.gestureessentials.action.handlers

import android.content.Context
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.os.Handler
import android.os.Looper
import com.israrxy.gestureessentials.action.ActionHandler
import com.israrxy.gestureessentials.core.model.ActionSpec
import com.israrxy.gestureessentials.core.model.ActionType

/** Torch through camera2. setTorchMode needs no camera permission. */
class TorchHandler(context: Context) : ActionHandler {

    override val type = ActionType.TORCH

    private val cameraManager = context.getSystemService(CameraManager::class.java)
    private var isOn = false

    private val torchCameraId: String? by lazy {
        runCatching {
            cameraManager.cameraIdList.firstOrNull { id ->
                cameraManager.getCameraCharacteristics(id)
                    .get(CameraCharacteristics.FLASH_INFO_AVAILABLE) == true
            }
        }.getOrNull()
    }

    // Follow changes made by Quick Settings or other apps, not just our last action.
    // This handler is process-scoped by AppGraph, as is the callback registration.
    private val callback = object : CameraManager.TorchCallback() {
        override fun onTorchModeChanged(cameraId: String, enabled: Boolean) {
            if (cameraId == torchCameraId) isOn = enabled
        }

        override fun onTorchModeUnavailable(cameraId: String) {
            if (cameraId == torchCameraId) isOn = false
        }
    }

    init {
        runCatching { cameraManager.registerTorchCallback(callback, Handler(Looper.getMainLooper())) }
    }

    override suspend fun execute(action: ActionSpec) {
        val spec = action as? ActionSpec.Torch ?: return
        val cameraId = torchCameraId ?: return

        val target = when (spec.mode) {
            ActionSpec.TorchMode.ON -> true
            ActionSpec.TorchMode.OFF -> false
            ActionSpec.TorchMode.TOGGLE -> !isOn
        }

        runCatching {
            cameraManager.setTorchMode(cameraId, target)
            isOn = target
        }
    }
}
