package com.israrxy.gestureessentials.data.mapper

import com.israrxy.gestureessentials.core.model.Constraints
import com.israrxy.gestureessentials.core.model.Rule
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

/** Hand-written codec: keeps JSON on disk stable and independent of Kotlin metadata. */
internal object RuleJsonCodec {

    fun encode(rule: Rule): JsonObject = buildJsonObject {
        put("id", rule.id)
        put("name", rule.name)
        put("enabled", rule.enabled)
        put("createdAt", rule.createdAt)
        put("cooldownMs", rule.cooldownMs)
        put("trigger", TriggerJsonCodec.encode(rule.trigger))
        put("actions", JsonArray(rule.actions.map(ActionJsonCodec::encode)))
        put(
            "constraints",
            buildJsonObject {
                rule.constraints.screenOn?.let { value -> put("screenOn", value) }
                put("chargingOnly", rule.constraints.chargingOnly)
            },
        )
    }

    fun decode(obj: JsonObject): Rule? {
        val id = obj.string("id") ?: return null
        val trigger = (obj["trigger"] as? JsonObject)?.let(TriggerJsonCodec::decode) ?: return null
        val actions = (obj["actions"] as? JsonArray)
            ?.mapNotNull { element -> (element as? JsonObject)?.let(ActionJsonCodec::decode) }
            .orEmpty()
        if (actions.isEmpty()) return null

        val constraintsObj = obj["constraints"] as? JsonObject
        val screenOn = if (constraintsObj?.containsKey("screenOn") == true) {
            constraintsObj.bool("screenOn", true)
        } else {
            null
        }

        return Rule(
            id = id,
            name = obj.string("name") ?: "Untitled rule",
            trigger = trigger,
            actions = actions,
            enabled = obj.bool("enabled", true),
            constraints = Constraints(
                screenOn = screenOn,
                chargingOnly = constraintsObj?.bool("chargingOnly", false) ?: false,
            ),
            cooldownMs = obj.long("cooldownMs", 1_000L),
            createdAt = obj.long("createdAt", 0L),
        )
    }
}
