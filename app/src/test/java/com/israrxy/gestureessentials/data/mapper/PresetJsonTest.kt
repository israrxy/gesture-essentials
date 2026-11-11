package com.israrxy.gestureessentials.data.mapper

import com.israrxy.gestureessentials.core.model.Rule
import com.israrxy.gestureessentials.core.model.ShortcutPreset
import org.junit.Assert.assertEquals
import org.junit.Test

class PresetJsonTest {

    @Test
    fun everyPresetSurvivesADiskRoundTrip() {
        ShortcutPreset.entries.forEach { preset ->
            val original = preset.newRule(cooldownMs = 2_000, createdAt = 123)
            val restored = RuleJsonCodec.decode(RuleJsonCodec.encode(original))
            assertEquals("round trip changed ${preset.name}", original, restored)
        }
    }

    @Test
    fun aThreeShakePresetIsNotDowngradedToTwoShakes() {
        val restored = RuleJsonCodec.decode(
            RuleJsonCodec.encode(ShortcutPreset.SHAKE_SCREENSHOT.newRule(1_000, 0)),
        ) as Rule
        assertEquals(3, (restored.trigger as com.israrxy.gestureessentials.core.model.Trigger.Shake).count)
    }
}
