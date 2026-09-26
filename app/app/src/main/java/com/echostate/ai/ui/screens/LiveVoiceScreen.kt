package com.echostate.ai.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.echostate.ai.viewmodel.EchoStateViewModel

@Composable
fun LiveVoiceScreen(
    viewModel: EchoStateViewModel,
    onBack: () -> Unit
) {
    val currentSpeech = viewModel.spokenText.collectAsState().value

    LaunchedEffect(Unit) {
        viewModel.startVoiceMode()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1E1E1E))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            "Gemini Live Active",
            color = Color(0xFF388E3C),
            fontSize = 28.sp
        )

        // Pulsing mic placeholder
        Box(
            modifier = Modifier
                .size(150.dp)
                .background(Color(0xFF388E3C).copy(alpha = 0.2f), CircleShape)
                .padding(20.dp)
                .background(Color(0xFF388E3C), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text("🎤", fontSize = 60.sp)
        }

        Text(
            text = currentSpeech,
            color = Color.White,
            fontSize = 20.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
        ) {
            Text("End Call", color = Color.White, fontSize = 20.sp)
        }
    }
}
