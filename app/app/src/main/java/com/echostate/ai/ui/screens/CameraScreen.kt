package com.echostate.ai.ui.screens

import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import com.echostate.ai.viewmodel.EchoStateViewModel
import androidx.compose.ui.unit.dp

import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import java.util.Locale
import androidx.compose.ui.Alignment

@Composable
fun CameraScreen(
    viewModel: EchoStateViewModel,
    onBack: () -> Unit
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current
    val currentSpeech = viewModel.spokenText.collectAsState().value
    var isListening by remember { mutableStateOf(false) }

    val speechRecognizer = remember {
        SpeechRecognizer.createSpeechRecognizer(context)
    }

    val intent = remember {
        Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        }
    }

    DisposableEffect(Unit) {
        speechRecognizer.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {
                isListening = true
                viewModel.speak("Ask about what is in front of you.")
            }
            override fun onBeginningOfSpeech() {}
            override fun onRmsChanged(rmsdB: Float) {}
            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onEndOfSpeech() {
                isListening = false
            }
            override fun onError(error: Int) {
                isListening = false
                viewModel.speak("Could not hear you.")
            }
            override fun onResults(results: Bundle?) {
                isListening = false
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (!matches.isNullOrEmpty()) {
                    val spokenText = matches[0]
                    
                    // We capture the frame AND ask the specific question about it!
                    val bitmap = viewModel.cameraManager?.captureCurrentFrame()
                    if (bitmap != null) {
                        viewModel.speak("Analyzing...")
                        viewModel.cloudEngine?.analyzeScene(bitmap, spokenText) { answer ->
                            viewModel.speak(answer)
                        }
                    } else {
                        viewModel.speak("Camera not ready.")
                    }
                }
            }
            override fun onPartialResults(partialResults: Bundle?) {}
            override fun onEvent(eventType: Int, params: Bundle?) {}
        })
        onDispose { speechRecognizer.destroy() }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = {
                        // Short Tap -> Standard scene description
                        viewModel.analyzeCurrentCameraScene()
                    },
                    onLongPress = {
                        // Long Press -> Ask a specific question about the scene
                        speechRecognizer.startListening(intent)
                    }
                )
            }
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragEnd = { /* handle */ }
                ) { change, dragAmount ->
                    change.consume()
                    val (x, y) = dragAmount
                    if (y > 50 && kotlin.math.abs(y) > kotlin.math.abs(x)) {
                        // Swipe Down -> Go Back
                        viewModel.speak("Closing Camera Mode.")
                        onBack()
                    }
                }
            }
    ) {
        AndroidView(
            factory = { ctx ->
                val previewView = PreviewView(ctx)
                viewModel.startCamera(lifecycleOwner, previewView.surfaceProvider)
                previewView
            },
            modifier = Modifier.fillMaxSize()
        )
        
        // Semi-transparent overlay to show instructions and text
        Surface(
            color = Color.Black.copy(alpha = 0.6f),
            modifier = Modifier.fillMaxWidth().align(Alignment.BottomCenter)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Camera Active. Tap anywhere to describe scene. Swipe down to go back.",
                    color = Color.White,
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = currentSpeech,
                    color = Color.Yellow,
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}
