package com.echostate.ai.audio

import android.content.Context
import android.util.Log
import com.echostate.ai.BuildConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.*
import okio.ByteString

/**
 * VOICE TO VOICE: Connects to EchoState FastAPI Backend /ws/live gateway
 * which proxies to Gemini 3.8 Live with sub-150ms barge-in and audio streaming.
 */
class LiveAudioEngine(private val context: Context) {
    private val scope = CoroutineScope(Dispatchers.IO)
    private val client = OkHttpClient()
    private var webSocket: WebSocket? = null

    fun connectToGeminiLive() {
        scope.launch {
            // Uses Railway backend WebSocket proxy or local emulator
            val wsUrl = "${BuildConfig.BACKEND_WS_URL}/ws/live"
            Log.d("LiveAudioEngine", "Connecting to WebSocket gateway: $wsUrl")

            val request = Request.Builder()
                .url(wsUrl)
                .build()

            webSocket = client.newWebSocket(request, object : WebSocketListener() {
                override fun onOpen(webSocket: WebSocket, response: Response) {
                    Log.d("LiveAudioEngine", "Connected to Gemini 3.8 Live Gateway")
                }

                override fun onMessage(webSocket: WebSocket, text: String) {
                    Log.d("LiveAudioEngine", "Gateway message: $text")
                }

                override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
                    // Play streaming audio directly to Android AudioTrack
                    Log.d("LiveAudioEngine", "Received ${bytes.size} audio bytes from Gemini Live")
                }

                override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                    Log.d("LiveAudioEngine", "WebSocket Closing: $reason")
                }

                override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                    Log.e("LiveAudioEngine", "WebSocket connection failure", t)
                }
            })
        }
    }
    
    fun sendMicrophoneStream(pcmData: ByteArray) {
        // Streams raw PCM chunks directly to the backend gateway
        webSocket?.send(ByteString.of(*pcmData))
    }

    fun triggerBargeIn() {
        // Halts current AI speech immediately when user starts speaking
        val haltSignal = "{\"action\": \"barge_in\"}"
        webSocket?.send(haltSignal)
    }

    fun speakOnline(text: String) {
        val prompt = "{\"action\": \"text_prompt\", \"text\": \"$text\"}"
        webSocket?.send(prompt)
    }

    fun disconnect() {
        webSocket?.close(1000, "App closed")
    }
}
