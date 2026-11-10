package com.israrxy.gestureessentials.data.mapper

import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

internal fun JsonObject.string(key: String): String? {
    val primitive = this[key] as? JsonPrimitive ?: return null
    return if (primitive.isString) primitive.content else null
}

internal fun JsonObject.int(key: String, fallback: Int): Int =
    (this[key] as? JsonPrimitive)?.content?.toIntOrNull() ?: fallback

internal fun JsonObject.long(key: String, fallback: Long): Long =
    (this[key] as? JsonPrimitive)?.content?.toLongOrNull() ?: fallback

internal fun JsonObject.bool(key: String, fallback: Boolean): Boolean =
    (this[key] as? JsonPrimitive)?.content?.toBooleanStrictOrNull() ?: fallback
