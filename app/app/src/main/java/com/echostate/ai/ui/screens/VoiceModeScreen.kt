package com.echostate.ai.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.echostate.ai.viewmodel.EchoStateViewModel

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.input.pointer.pointerInput

@Composable
fun VoiceModeScreen(
    viewModel: EchoStateViewModel,
    onNavigateToBraille: () -> Unit,
    onNavigateToTwoWay: () -> Unit,
    onNavigateToCamera: () -> Unit,
    onNavigateToVoice: () -> Unit
) {
    val currentSpeech = viewModel.spokenText.collectAsState().value
    val backendStatus = viewModel.backendStatus.collectAsState().value

    // Auto-verify connection when screen loads, and speak instructions
    LaunchedEffect(Unit) {
        viewModel.verifyBackendConnection()
        viewModel.speak("Welcome to Echo State A I. Swipe UP to open Camera Mode. Swipe DOWN to open Voice Mode.")
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragEnd = { /* Optionally handle drag end */ }
                ) { change, dragAmount ->
                    change.consume()
                    val (x, y) = dragAmount
                    if (kotlin.math.abs(y) > kotlin.math.abs(x)) {
                        if (y < -50) {
                            // Swiped Up -> Camera Mode
                            viewModel.speak("Opening Camera Mode.")
                            onNavigateToCamera()
                        } else if (y > 50) {
                            // Swiped Down -> Voice Mode
                            viewModel.speak("Opening Voice Mode.")
                            onNavigateToVoice()
                        }
                    }
                }
            }
            .pointerInput(Unit) {
                detectTapGestures(
                    onDoubleTap = {
                        viewModel.speak("Double tap detected. Swipe up for camera, down for voice.")
                    },
                    onLongPress = {
                        viewModel.speak("Backend status: $backendStatus")
                    }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                "Blind-Friendly Mode Active",
                color = Color.Green,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(32.dp))
            Text(
                "Swipe UP for Camera Mode",
                color = Color.White,
                fontSize = 20.sp,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                "Swipe DOWN for Voice Mode",
                color = Color.White,
                fontSize = 20.sp,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(32.dp))
            Text(
                text = "Last AI Output:\n$currentSpeech",
                color = Color.Yellow,
                fontSize = 18.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}
