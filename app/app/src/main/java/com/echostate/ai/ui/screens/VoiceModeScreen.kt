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

@Composable
fun VoiceModeScreen(
    viewModel: EchoStateViewModel,
    onNavigateToBraille: () -> Unit,
    onNavigateToTwoWay: () -> Unit
) {
    val currentSpeech = viewModel.spokenText.collectAsState().value
    val backendStatus = viewModel.backendStatus.collectAsState().value

    // Auto-verify connection when screen loads
    LaunchedEffect(Unit) {
        viewModel.verifyBackendConnection()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF121212))
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Backend Status Indicator
        Text(
            text = backendStatus,
            color = if (backendStatus.contains("Connected")) Color.Green else Color.Red,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(top = 16.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Large Camera/Vision Mode Button
        Button(
            onClick = { viewModel.simulateCameraCentered() },
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(8.dp)
                .semantics { contentDescription = "Camera Mode. Tap to analyze your surroundings." },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1976D2)),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("📷 Camera Mode", fontSize = 32.sp, color = Color.White, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Analyze Surroundings", fontSize = 18.sp, color = Color.LightGray)
            }
        }

        // Large Voice Mode Button
        Button(
            onClick = { /* Connect to Voice Live Engine */ },
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(8.dp)
                .semantics { contentDescription = "Voice Mode. Tap to speak with the AI assistant." },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF388E3C)),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("🎤 Voice Mode", fontSize = 32.sp, color = Color.White, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Live Assistant", fontSize = 18.sp, color = Color.LightGray)
            }
        }

        // Output Display Area
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
                .background(Color.Black, shape = RoundedCornerShape(12.dp))
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = currentSpeech.ifEmpty { "AI Response will appear here..." },
                color = Color.Yellow,
                fontSize = 20.sp,
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
        
        // Navigation Options
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(onClick = onNavigateToTwoWay) { Text("Two-Way UI") }
            Button(onClick = onNavigateToBraille) { Text("Braille Input") }
        }
    }
}
