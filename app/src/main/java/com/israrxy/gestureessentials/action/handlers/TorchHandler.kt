package com.israrxy.gestureessentials.action.handlers

import android.content.Context
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
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
