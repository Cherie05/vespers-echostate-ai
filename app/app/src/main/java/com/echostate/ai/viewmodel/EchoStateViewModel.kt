package com.echostate.ai.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.LifecycleOwner
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.echostate.ai.accessibility.HapticFeedbackManager
import com.echostate.ai.accessibility.BrailleInputHandler
import com.echostate.ai.audio.LiveAudioEngine
import com.echostate.ai.camera.CameraManager
import com.echostate.ai.cloud.CloudMultimodalEngine
import com.echostate.ai.llm.AgentLoopEngine
import com.echostate.ai.llm.LocalGemmaEngine

import android.speech.tts.TextToSpeech
import java.util.Locale

class EchoStateViewModel : ViewModel() {
    
    private val _spokenText = MutableStateFlow("Ready.")
    val spokenText: StateFlow<String> = _spokenText.asStateFlow()

    private val _brailleInputText = MutableStateFlow("")
    val brailleInputText: StateFlow<String> = _brailleInputText.asStateFlow()

    private var hapticManager: HapticFeedbackManager? = null
    private var audioEngine: LiveAudioEngine? = null
    private var cloudEngine: CloudMultimodalEngine? = null
    private var localGemma: LocalGemmaEngine? = null
    private var agentEngine: AgentLoopEngine? = null
    private var cameraManager: CameraManager? = null
    private var textToSpeech: TextToSpeech? = null
    
    var brailleInputHandler: BrailleInputHandler? = null

    fun initializeEngines(context: Context, lifecycleOwner: LifecycleOwner) {
        hapticManager = HapticFeedbackManager(context)
        audioEngine = LiveAudioEngine(context)
        cloudEngine = CloudMultimodalEngine()
        
        // Initialize Android Text-To-Speech for real audio feedback
        textToSpeech = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                textToSpeech?.language = Locale.US
            }
        }
        
        // Track 2: Connect to Gemini Live audio gateway on Railway/Local backend
        audioEngine?.connectToGeminiLive()
        
        // Track 5: Setup Gemma 4 local loop
        localGemma = LocalGemmaEngine(context)
        localGemma?.initialize()
        
        // Full Sense-Decide-Act-Check Agent Engine
        agentEngine = AgentLoopEngine(
            context = context,
            audioEngine = audioEngine!!,
            cloudEngine = cloudEngine!!,
            hapticManager = hapticManager!!,
            localGemma = localGemma!!
        )
        
        // Setup Camera for Multimodal input (10 FPS)
        cameraManager = CameraManager(context, agentEngine!!)
        
        // Setup Braille Screen Input
        brailleInputHandler = BrailleInputHandler(this)
    }

    private val _backendStatus = MutableStateFlow("Checking connection...")
    val backendStatus: StateFlow<String> = _backendStatus.asStateFlow()

    fun verifyBackendConnection() {
        _backendStatus.value = "Connecting to ${com.echostate.ai.BuildConfig.BACKEND_BASE_URL}..."
        cloudEngine?.verifyConnection { isConnected ->
            _backendStatus.value = if (isConnected) "Backend Connected" else "Backend Disconnected"
        }
    }

    fun speak(text: String) {
        _spokenText.value = text
        textToSpeech?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "echo_speech")
    }

    fun updateBrailleText(newChar: Char) {
        _brailleInputText.value += newChar
    }

    fun clearBrailleText() {
        _brailleInputText.value = ""
    }
    
    fun simulateCameraCentered() {
        hapticManager?.vibrateCentered()
        speak("Document centered. Reading: 'Ibuprofen 200mg'.")
    }

    fun startCamera(lifecycleOwner: LifecycleOwner, surfaceProvider: androidx.camera.core.Preview.SurfaceProvider) {
        cameraManager?.startCamera(lifecycleOwner, surfaceProvider)
    }

    fun analyzeCurrentCameraScene() {
        val bitmap = cameraManager?.captureCurrentFrame()
        if (bitmap == null) {
            speak("Camera is warming up. Please aim camera at your surroundings.")
            return
        }
        hapticManager?.vibrateCentered()
        speak("Analyzing scene with Gemini 3.8 Flash...")
        cloudEngine?.analyzeScene(bitmap) { description ->
            speak(description)
        }
    }

    fun startVoiceMode() {
        speak("Voice Assistant Active. Ask me anything about what's around you.")
    }

    fun askVoiceAssistant(query: String) {
        hapticManager?.vibrateCentered()
        speak("Thinking...")
        cloudEngine?.askQuestion(query) { answer ->
            speak(answer)
        }
    }

    fun captureImageForVoice(bitmap: android.graphics.Bitmap) {
        speak("Analyzing scene...")
        cloudEngine?.analyzeScene(bitmap) { description ->
            speak(description)
        }
    }

    override fun onCleared() {
        super.onCleared()
        textToSpeech?.stop()
        textToSpeech?.shutdown()
    }
}
