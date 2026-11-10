package com.israrxy.gestureessentials.action.handlers

import com.israrxy.gestureessentials.action.ActionHandler
import com.israrxy.gestureessentials.action.TtsSpeaker
import com.israrxy.gestureessentials.core.model.ActionSpec
import com.israrxy.gestureessentials.core.model.ActionType

class SpeakHandler(private val speaker: TtsSpeaker) : ActionHandler {

    override val type = ActionType.SPEAK

    override suspend fun execute(action: ActionSpec) {
        val spec = action as? ActionSpec.Speak ?: return
        if (spec.text.isNotBlank()) speaker.speak(spec.text)
    }
}
