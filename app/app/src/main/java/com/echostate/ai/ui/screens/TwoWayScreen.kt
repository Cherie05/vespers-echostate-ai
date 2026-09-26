package com.echostate.ai.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.echostate.ai.viewmodel.EchoStateViewModel

@Composable
fun TwoWayScreen(
    viewModel: EchoStateViewModel,
    onBack: () -> Unit
) {
    // This text comes from either Voice STT or Braille Input
    // E.g., "One iced Americano, please."
    val displayMessage = viewModel.brailleInputText.collectAsState().value.ifEmpty { 
        "Hello, I am deaf-blind. Please type your response on this screen."
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .clickable { onBack() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = displayMessage,
            color = Color.Yellow,
            fontSize = 72.sp, // Massive font for sighted readers
            fontWeight = FontWeight.Black,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(32.dp)
                .rotate(180f) // Faces outward towards the other person
        )
    }
}
