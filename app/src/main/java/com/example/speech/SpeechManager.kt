package com.example.speech

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

class SpeechManager(private val context: Context) : TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = null
    private var isTtsReady = false

    private var speechRecognizer: SpeechRecognizer? = null

    private val _isListening = MutableStateFlow(false)
    val isListening: StateFlow<Boolean> = _isListening.asStateFlow()

    private val _spokenText = MutableStateFlow("")
    val spokenText: StateFlow<String> = _spokenText.asStateFlow()

    private val _rmsLevel = MutableStateFlow(0f)
    val rmsLevel: StateFlow<Float> = _rmsLevel.asStateFlow()

    private val _speechError = MutableStateFlow<String?>(null)
    val speechError: StateFlow<String?> = _speechError.asStateFlow()

    init {
        try {
            tts = TextToSpeech(context, this)
        } catch (e: Exception) {
            Log.e("SpeechManager", "Failed to init TTS", e)
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts?.setLanguage(Locale.US)
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.w("SpeechManager", "TTS US English not supported, trying default")
                tts?.language = Locale.getDefault()
            }
            tts?.setSpeechRate(0.92f)
            tts?.setPitch(1.0f)
            isTtsReady = true
        } else {
            Log.e("SpeechManager", "TTS initialization failed with status $status")
        }
    }

    fun speak(text: String, speedRate: Float = 0.92f) {
        if (!isTtsReady || tts == null) {
            // Re-attempt init if null
            tts = TextToSpeech(context) { status ->
                if (status == TextToSpeech.SUCCESS) {
                    tts?.language = Locale.US
                    tts?.setSpeechRate(speedRate)
                    tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "UtteranceId_${System.currentTimeMillis()}")
                }
            }
            return
        }
        val cleanText = text.replace(Regex("[()_*\\[\\]]"), " ")
        tts?.setSpeechRate(speedRate)
        tts?.speak(cleanText, TextToSpeech.QUEUE_FLUSH, null, "UtteranceId_${System.currentTimeMillis()}")
    }

    fun speakSlow(text: String) {
        speak(text, speedRate = 0.70f)
    }

    fun stopSpeaking() {
        tts?.stop()
    }

    fun startListening(onResult: (String) -> Unit) {
        if (!SpeechRecognizer.isRecognitionAvailable(context)) {
            _speechError.value = "Speech recognition is not available on this device"
            return
        }

        stopListening()

        try {
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context).apply {
                setRecognitionListener(object : RecognitionListener {
                    override fun onReadyForSpeech(params: Bundle?) {
                        _isListening.value = true
                        _speechError.value = null
                        _rmsLevel.value = 0f
                    }

                    override fun onBeginningOfSpeech() {}

                    override fun onRmsChanged(rmsdB: Float) {
                        // Normalize typical dB value (-2 to 10) to 0.0..1.0 for UI visualizer
                        val normalized = ((rmsdB + 2f) / 12f).coerceIn(0f, 1f)
                        _rmsLevel.value = normalized
                    }

                    override fun onBufferReceived(buffer: ByteArray?) {}

                    override fun onEndOfSpeech() {
                        _isListening.value = false
                        _rmsLevel.value = 0f
                    }

                    override fun onError(error: Int) {
                        _isListening.value = false
                        _rmsLevel.value = 0f
                        val errorMsg = when (error) {
                            SpeechRecognizer.ERROR_AUDIO -> "Audio recording error. Please check microphone."
                            SpeechRecognizer.ERROR_CLIENT -> "Speech recognition client error."
                            SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Microphone permission is required."
                            SpeechRecognizer.ERROR_NETWORK -> "Network required for speech-to-text service."
                            SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "Network timeout. Try speaking again."
                            SpeechRecognizer.ERROR_NO_MATCH -> "No speech recognized. Please speak closer to the mic."
                            SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "Recognition service busy. Retrying..."
                            SpeechRecognizer.ERROR_SERVER -> "Voice recognition server error."
                            SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "No speech detected before timeout."
                            else -> "Could not capture voice ($error)"
                        }
                        _speechError.value = errorMsg
                    }

                    override fun onResults(results: Bundle?) {
                        _isListening.value = false
                        _rmsLevel.value = 0f
                        val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                        val text = matches?.firstOrNull() ?: ""
                        if (text.isNotBlank()) {
                            _spokenText.value = text
                            onResult(text)
                        } else {
                            _speechError.value = "No clear words detected. Try again!"
                        }
                    }

                    override fun onPartialResults(partialResults: Bundle?) {
                        val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                        matches?.firstOrNull()?.let {
                            _spokenText.value = it
                        }
                    }

                    override fun onEvent(eventType: Int, params: Bundle?) {}
                })
            }

            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US")
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "en-US")
                putExtra(RecognizerIntent.EXTRA_ONLY_RETURN_LANGUAGE_PREFERENCE, "en-US")
                putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
                putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3)
            }

            speechRecognizer?.startListening(intent)
        } catch (e: Exception) {
            _isListening.value = false
            _rmsLevel.value = 0f
            _speechError.value = e.message ?: "Failed to start listening"
        }
    }

    fun stopListening() {
        try {
            speechRecognizer?.stopListening()
            speechRecognizer?.destroy()
        } catch (e: Exception) {
            // Ignore
        } finally {
            speechRecognizer = null
            _isListening.value = false
        }
    }

    fun clearError() {
        _speechError.value = null
    }

    fun destroy() {
        try {
            tts?.stop()
            tts?.shutdown()
        } catch (e: Exception) {
            // Ignore
        }
        stopListening()
    }
}
