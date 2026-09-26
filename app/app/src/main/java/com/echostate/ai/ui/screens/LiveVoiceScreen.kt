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

import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import androidx.compose.foundation.clickable
import androidx.compose.ui.platform.LocalContext
import java.util.Locale

@Composable
fun LiveVoiceScreen(
    viewModel: EchoStateViewModel,
    onBack: () -> Unit
) {
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
                viewModel.speak("I am listening...")
            }
            override fun onBeginningOfSpeech() {}
            override fun onRmsChanged(rmsdB: Float) {}
            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onEndOfSpeech() {
                isListening = false
            }
            override fun onError(error: Int) {
                isListening = false
                viewModel.speak("Could not hear you. Tap the mic to try again.")
            }
            override fun onResults(results: Bundle?) {
                isListening = false
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (!matches.isNullOrEmpty()) {
                    val spokenText = matches[0]
                    viewModel.askVoiceAssistant(spokenText)
                }
            }
            override fun onPartialResults(partialResults: Bundle?) {}
            override fun onEvent(eventType: Int, params: Bundle?) {}
        })

        onDispose {
            speechRecognizer.destroy()
        }
    }

    LaunchedEffect(Unit) {
        viewModel.startVoiceMode()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF121212))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            if (isListening) "Listening..." else "Gemini Live Assistant",
            color = if (isListening) Color(0xFFFF9800) else Color(0xFF388E3C),
            fontSize = 26.sp
        )

        // Interactive Mic Button
        Box(
            modifier = Modifier
                .size(160.dp)
                .background(
                    if (isListening) Color(0xFFFF9800).copy(alpha = 0.3f) else Color(0xFF388E3C).copy(alpha = 0.3f),
                    CircleShape
                )
                .padding(20.dp)
                .background(
                    if (isListening) Color(0xFFFF9800) else Color(0xFF388E3C),
                    CircleShape
                )
                .clickable {
                    if (isListening) {
                        speechRecognizer.stopListening()
                        isListening = false
                    } else {
                        speechRecognizer.startListening(intent)
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            Text(if (isListening) "👂" else "🎤", fontSize = 60.sp)
        }

        Text(
            text = "Tap microphone to speak to Gemini",
            color = Color.LightGray,
            fontSize = 16.sp
        )

        // Output Display Card
        Surface(
            modifier = Modifier.fillMaxWidth().heightIn(min = 120.dp),
            color = Color.Black,
            shape = MaterialTheme.shapes.medium
        ) {
            Box(modifier = Modifier.padding(16.dp), contentAlignment = Alignment.Center) {
                Text(
                    text = currentSpeech,
                    color = Color.Yellow,
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center
                )
            }
        }

        // Quick query suggestions
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = { viewModel.askVoiceAssistant("What is in front of me?") },
                colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray)
            ) {
                Text("What's near?", fontSize = 12.sp)
            }
            Button(
                onClick = { viewModel.askVoiceAssistant("Is there an obstacle or doorway ahead?") },
                colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray)
            ) {
                Text("Find Door", fontSize = 12.sp)
            }
        }

        Button(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F))
        ) {
            Text("Back to Home", color = Color.White, fontSize = 18.sp)
        }
    }
}
