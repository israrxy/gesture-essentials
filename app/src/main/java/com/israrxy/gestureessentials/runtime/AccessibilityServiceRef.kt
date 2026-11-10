package com.israrxy.gestureessentials.runtime

import android.accessibilityservice.AccessibilityService

/**
 * The system owns the accessibility service instance, so action handlers reach
 * it through this small holder instead of being constructed with it (the graph
 * is built before the service exists).
 */
object AccessibilityServiceRef {
    @Volatile
    var service: AccessibilityService? = null
}
