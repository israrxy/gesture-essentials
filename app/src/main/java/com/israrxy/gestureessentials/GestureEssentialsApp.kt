package com.israrxy.gestureessentials

import android.app.Application
import android.content.pm.PackageManager
import com.israrxy.gestureessentials.core.model.AppInfo

class GestureEssentialsApp : Application() {

    lateinit var graph: AppGraph
        private set

    override fun onCreate() {
        super.onCreate()
        graph = AppGraph(this, readAppInfo())
    }

    private fun readAppInfo(): AppInfo = try {
        val info = packageManager.getPackageInfo(packageName, 0)
        AppInfo(versionName = info.versionName ?: "0.0.0", versionCode = info.longVersionCode)
    } catch (e: PackageManager.NameNotFoundException) {
        AppInfo(versionName = "0.0.0", versionCode = 0L)
    }
}
