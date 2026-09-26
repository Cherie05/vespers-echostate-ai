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

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.input.pointer.pointerInput

@Composable
fun CameraScreen(
    viewModel: EchoStateViewModel,
    onBack: () -> Unit
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current
    val currentSpeech = viewModel.spokenText.collectAsState().value

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = {
                        viewModel.analyzeCurrentCameraScene()
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
            modifier = Modifier.fillMaxWidth().align(androidx.compose.ui.Alignment.BottomCenter)
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
