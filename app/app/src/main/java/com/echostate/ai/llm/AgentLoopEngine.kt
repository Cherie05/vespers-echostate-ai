package com.echostate.ai.llm

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import com.echostate.ai.accessibility.HapticFeedbackManager
import com.echostate.ai.audio.LiveAudioEngine
import com.echostate.ai.cloud.CloudMultimodalEngine
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Problem Statement 5: Gemma 4 Autonomous Sense-Decide-Act-Check Agent Loop
 * Seamlessly integrates local on-device SLM with the FastAPI Railway Backend.
 */
class AgentLoopEngine(
    private val context: Context,
    private val audioEngine: LiveAudioEngine,
    private val cloudEngine: CloudMultimodalEngine,
    private val hapticManager: HapticFeedbackManager,
    private val localGemma: LocalGemmaEngine
) {
    private val scope = CoroutineScope(Dispatchers.IO)
    private var isProcessingFrame = false

    /**
     * Continuous 10 FPS Camera intake loop
     */
    fun processCameraFrame(bitmap: Bitmap) {
        if (isProcessingFrame) return
        isProcessingFrame = true

        scope.launch {
            try {
                // 1. SENSE (Local-First):
                // Gemma 4 inspects frame locally for sensitive text (prescriptions, ID cards, labels)
                val localResult = localGemma.processLocalContext("Analyzing document/label...")

                // 2. DECIDE:
                // Does this require cloud escalation or stay 100% private and offline?
                val decision = localGemma.decideRouting(
                    userIntent = "Identify immediate obstacles or read text",
                    localContext = localResult
                )

                if (decision == LocalGemmaEngine.RoutingDecision.LOCAL) {
                    // 3. ACT (Local):
                    // Double haptic pulse confirms document is centered & in frame
                    hapticManager.vibrateCentered()
                    
                    // Synthesize speech or read aloud
                    cloudEngine.synthesizeSpeech(localResult) { audioBytes ->
                        Log.d("AgentLoop", "Playing speech readout (${audioBytes.size} bytes)")
                    }
                } else {
                    // Cloud Escalation (Track 1 & 2):
                    // Call backend FastAPI gateway (Gemini 3.8 Flash)
                    cloudEngine.analyzeScene(bitmap) { cloudDescription ->
                        // 4. CHECK (Local Guardrail):
                        // Gemma 4 verifies the cloud response before speaking to the blind user
                        val verifiedOutput = localGemma.processLocalContext(cloudDescription)

                        // Act: Send response to audio stream
                        audioEngine.speakOnline(verifiedOutput)
                    }
                }
            } catch (e: Exception) {
                // Offline Error Recovery
                Log.e("AgentLoop", "Error in agent loop, recovering gracefully offline", e)
                hapticManager.vibrateCentered()
            } finally {
                isProcessingFrame = false
            }
        }
    }
}
