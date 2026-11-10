package com.israrxy.gestureessentials.action

import android.content.Context
import android.speech.tts.TextToSpeech
import java.util.Locale

/**
 * TextToSpeech initialises asynchronously, so the first phrase would otherwise
 * be dropped. Pending phrases are queued and spoken once the engine is ready.
 */
class TtsSpeaker(context: Context) {

    private val appContext = context.applicationContext
    private val pending = ArrayDeque<String>()

    private var tts: TextToSpeech? = null
    private var ready = false
    private var initialising = false

    fun speak(text: String) {
        synchronized(this) {
            if (ready) {
                tts?.speak(text, TextToSpeech.QUEUE_ADD, null, text)
            } else {
                pending.addLast(text)
                if (!initialising) initialise()
            }
        }
    }

    private fun initialise() {
        initialising = true
        tts = TextToSpeech(appContext) { status ->
            synchronized(this) {
                initialising = false
                if (status != TextToSpeech.SUCCESS) {
                    pending.clear()
                    return@synchronized
                }
                tts?.language = Locale.getDefault()
                ready = true
                while (pending.isNotEmpty()) {
                    val phrase = pending.removeFirst()
                    tts?.speak(phrase, TextToSpeech.QUEUE_ADD, null, phrase)
                }
            }
        }
    }
}
