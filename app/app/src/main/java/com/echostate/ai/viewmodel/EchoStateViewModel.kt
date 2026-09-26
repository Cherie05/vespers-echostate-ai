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

class EchoStateViewModel : ViewModel() {
    
    private val _spokenText = MutableStateFlow("Listening...")
    val spokenText: StateFlow<String> = _spokenText.asStateFlow()

    private val _brailleInputText = MutableStateFlow("")
    val brailleInputText: StateFlow<String> = _brailleInputText.asStateFlow()

    private var hapticManager: HapticFeedbackManager? = null
    private var audioEngine: LiveAudioEngine? = null
    private var cloudEngine: CloudMultimodalEngine? = null
    private var localGemma: LocalGemmaEngine? = null
    private var agentEngine: AgentLoopEngine? = null
    private var cameraManager: CameraManager? = null
    
    var brailleInputHandler: BrailleInputHandler? = null

    fun initializeEngines(context: Context, lifecycleOwner: LifecycleOwner) {
        hapticManager = HapticFeedbackManager(context)
        audioEngine = LiveAudioEngine(context)
        cloudEngine = CloudMultimodalEngine()
        
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
        cameraManager?.startCamera(lifecycleOwner)
        
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

    fun updateBrailleText(newChar: Char) {
        _brailleInputText.value += newChar
    }

    fun clearBrailleText() {
        _brailleInputText.value = ""
    }
    
    fun simulateCameraCentered() {
        hapticManager?.vibrateCentered()
        _spokenText.value = "Document centered. Reading: 'Ibuprofen 200mg'."
        cloudEngine?.synthesizeSpeech("Document centered. Ibuprofen 200mg") {
            // Audio ready
        }
    }
}
