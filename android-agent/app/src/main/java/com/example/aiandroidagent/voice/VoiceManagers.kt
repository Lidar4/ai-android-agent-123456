package com.example.aiandroidagent.voice

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import java.util.Locale

class SpeechRecognizerManager(
    private val context: Context,
    private val onText: (String) -> Unit,
    private val onStatus: (String) -> Unit
) {
    private var recognizer: SpeechRecognizer? = null
    var locale = Locale("bn", "BD")

    fun start() {
        if (!SpeechRecognizer.isRecognitionAvailable(context)) {
            onStatus("Speech recognition unavailable")
            return
        }
        recognizer?.destroy()
        recognizer = SpeechRecognizer.createSpeechRecognizer(context).apply {
            setRecognitionListener(object : RecognitionListener {
                override fun onReadyForSpeech(params: Bundle) { onStatus("Listening") }
                override fun onBeginningOfSpeech() = Unit
                override fun onRmsChanged(rmsdB: Float) = Unit
                override fun onBufferReceived(buffer: ByteArray) = Unit
                override fun onEndOfSpeech() = Unit
                override fun onError(error: Int) { onStatus("Voice error $error") }
                override fun onResults(results: Bundle) {
                    results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                        ?.firstOrNull()
                        ?.let(onText)
                    onStatus("Ready")
                }
                override fun onPartialResults(partialResults: Bundle) = Unit
                override fun onEvent(eventType: Int, params: Bundle) = Unit
            })
            startListening(
                Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
                    .putExtra(RecognizerIntent.EXTRA_LANGUAGE, locale.toLanguageTag())
                    .putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
            )
        }
    }

    fun stop() {
        recognizer?.stopListening()
        recognizer?.destroy()
        recognizer = null
    }
}

class TTSManager(context: Context) : TextToSpeech.OnInitListener {
    private val tts = TextToSpeech(context, this)

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) tts.language = Locale("bn", "BD")
    }

    fun speak(text: String) {
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "agent-response")
    }

    fun stop() { tts.stop() }
    fun release() { tts.shutdown() }
}
