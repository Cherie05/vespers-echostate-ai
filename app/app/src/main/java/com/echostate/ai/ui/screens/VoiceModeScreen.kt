package com.echostate.ai.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.echostate.ai.viewmodel.EchoStateViewModel

@Composable
fun VoiceModeScreen(
    viewModel: EchoStateViewModel,
    onNavigateToBraille: () -> Unit,
    onNavigateToTwoWay: () -> Unit
) {
    val currentSpeech = viewModel.spokenText.collectAsState().value

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            // Gesture navigation replaces tiny buttons for visually impaired users
            .pointerInput(Unit) {
                detectTapGestures(
                    onDoubleTap = { onNavigateToTwoWay() },
                    onLongPress = { onNavigateToBraille() },
                    onTap = { viewModel.simulateCameraCentered() }
                )
            }
            .semantics { 
                contentDescription = "Voice Mode Active. Double tap for Two-Way Display. Long press for Braille Screen Input. Tap to simulate camera scan." 
            }
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Voice Mode Active",
            color = Color.Yellow,
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(32.dp))
        Text(
            text = currentSpeech,
            color = Color.White,
            fontSize = 24.sp
        )
    }
}
