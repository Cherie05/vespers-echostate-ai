package com.echostate.ai.cloud

import android.graphics.Bitmap
import android.util.Base64
import android.util.Log
import com.echostate.ai.BuildConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.ByteArrayOutputStream

/**
 * Connects to EchoState FastAPI Backend for:
 * - Gemini 3.8 Flash (Vision & Video scene description)
 * - Gemini 3.8 Flash TTS (Text to Speech)
 * - Antigravity Agent Orchestration
 */
class CloudMultimodalEngine {
    private val client = OkHttpClient()
    private val scope = CoroutineScope(Dispatchers.IO)
    private val backendBaseUrl = BuildConfig.BACKEND_BASE_URL

    fun verifyConnection(onResult: (Boolean) -> Unit) {
        scope.launch {
            try {
                val request = Request.Builder().url("$backendBaseUrl/health").build()
                client.newCall(request).execute().use { response ->
                    onResult(response.isSuccessful)
                }
            } catch (e: Exception) {
                onResult(false)
            }
        }
    }

    /**
     * IMAGE / VIDEO TO TEXT: Sends frame to /api/v1/vision/analyze-base64
     */
    fun analyzeScene(bitmap: Bitmap, onResult: (String) -> Unit) {
        scope.launch {
            try {
                val outputStream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 85, outputStream)
                val base64Image = Base64.encodeToString(outputStream.toByteArray(), Base64.NO_WRAP)

                val url = "$backendBaseUrl/api/v1/vision/analyze-base64"
                val jsonPayload = JSONObject().apply {
                    put("image_base64", base64Image)
                    put("mime_type", "image/jpeg")
                    put("prompt", "Analyze what is in front of the blind user. Highlight immediate obstacles or doors.")
                }

                val request = Request.Builder()
                    .url(url)
                    .post(jsonPayload.toString().toRequestBody("application/json".toMediaType()))
                    .build()

                client.newCall(request).execute().use { response ->
                    val responseStr = response.body?.string() ?: ""
                    if (response.isSuccessful) {
                        val json = JSONObject(responseStr)
                        val description = json.optString("description", "Path is clear.")
                        onResult(description)
                    } else {
                        Log.e("CloudMultimodal", "Vision backend error: ${response.code}")
                        onResult("Unable to analyze scene via gateway.")
                    }
                }
            } catch (e: Exception) {
                Log.e("CloudMultimodal", "Network error in vision call", e)
                onResult("Network unavailable. Relying on local Gemma 4.")
            }
        }
    fun askQuestion(prompt: String, onResult: (String) -> Unit) {
        scope.launch {
            try {
                val url = "$backendBaseUrl/api/v1/vision/chat"
                val jsonPayload = JSONObject().apply {
                    put("prompt", prompt)
                }

                val request = Request.Builder()
                    .url(url)
                    .post(jsonPayload.toString().toRequestBody("application/json".toMediaType()))
                    .build()

                client.newCall(request).execute().use { response ->
                    val responseStr = response.body?.string() ?: ""
                    if (response.isSuccessful) {
                        val json = JSONObject(responseStr)
                        val reply = json.optString("description", "I heard you.")
                        onResult(reply)
                    } else {
                        Log.e("CloudMultimodal", "Chat backend error: ${response.code}")
                        onResult("Backend returned error ${response.code}")
                    }
                }
            } catch (e: Exception) {
                Log.e("CloudMultimodal", "Network error in chat call", e)
                onResult("Network unavailable.")
            }
        }
    }

    /**
     * TEXT TO VOICE: Calls backend /api/v1/audio/tts
     */
    fun synthesizeSpeech(text: String, onAudioReady: (ByteArray) -> Unit) {
        scope.launch {
            try {
                val url = "$backendBaseUrl/api/v1/audio/tts"
                val payload = JSONObject().apply {
                    put("text", text)
                    put("voice_name", "en-US-Journey-D")
                }

                val request = Request.Builder()
                    .url(url)
                    .post(payload.toString().toRequestBody("application/json".toMediaType()))
                    .build()

                client.newCall(request).execute().use { response ->
                    if (response.isSuccessful) {
                        val json = JSONObject(response.body?.string() ?: "{}")
                        val base64Audio = json.optString("audio_base64", "")
                        if (base64Audio.isNotEmpty()) {
                            val audioBytes = Base64.decode(base64Audio, Base64.DEFAULT)
                            onAudioReady(audioBytes)
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("CloudMultimodal", "TTS backend error", e)
            }
        }
    }
}
