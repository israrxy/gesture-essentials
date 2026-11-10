package com.israrxy.gestureessentials.action.handlers

import android.accessibilityservice.AccessibilityService
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.view.Display
import com.israrxy.gestureessentials.action.ActionHandler
import com.israrxy.gestureessentials.core.model.ActionSpec
import com.israrxy.gestureessentials.core.model.ActionType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/**
 * Captures the screen through AccessibilityService.takeScreenshot (API 30+)
 * and saves it to Pictures/GestureEssentials via MediaStore. No storage
 * permission is needed on API 29+.
 */
class ScreenshotHandler(
    private val context: Context,
    private val serviceProvider: () -> AccessibilityService?,
) : ActionHandler {

    override val type = ActionType.SCREENSHOT

    private val ioScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override suspend fun execute(action: ActionSpec) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) return
        val service = serviceProvider() ?: return

        service.takeScreenshot(
            Display.DEFAULT_DISPLAY,
            context.mainExecutor,
            object : AccessibilityService.TakeScreenshotCallback {
                override fun onSuccess(result: AccessibilityService.ScreenshotResult) {
                    val hardware = Bitmap.wrapHardwareBuffer(result.hardwareBuffer, result.colorSpace)
                    result.hardwareBuffer.close()
                    val software = hardware?.copy(Bitmap.Config.ARGB_8888, false) ?: return
                    ioScope.launch { save(software) }
                }

                override fun onFailure(errorCode: Int) = Unit
            },
        )
    }

    private fun save(bitmap: Bitmap) {
        runCatching {
            val resolver = context.contentResolver
            val values = ContentValues().apply {
                put(
                    MediaStore.Images.Media.DISPLAY_NAME,
                    "gesture_essentials_${System.currentTimeMillis()}.png",
                )
                put(MediaStore.Images.Media.MIME_TYPE, "image/png")
                put(
                    MediaStore.Images.Media.RELATIVE_PATH,
                    Environment.DIRECTORY_PICTURES + "/GestureEssentials",
                )
                put(MediaStore.Images.Media.IS_PENDING, 1)
            }

            val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values) ?: return
            resolver.openOutputStream(uri)?.use { stream ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            }

            values.clear()
            values.put(MediaStore.Images.Media.IS_PENDING, 0)
            resolver.update(uri, values, null, null)
        }
    }
}
