package com.echostate.ai.ui.screens

import android.view.MotionEvent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.echostate.ai.viewmodel.EchoStateViewModel

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun BrailleScreen(
    viewModel: EchoStateViewModel,
    onBack: () -> Unit,
    onSendToTwoWay: () -> Unit
) {
    val typedText = viewModel.brailleInputText.collectAsState().value

    // Captures multi-touch events directly.
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.DarkGray)
            .pointerInteropFilter { motionEvent ->
                viewModel.brailleInputHandler?.onTouchEvent(motionEvent)
                true
            }
    ) {
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Braille Input (6-Finger Touch)",
                color = Color.LightGray,
                fontSize = 18.sp
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = typedText.ifEmpty { "Type here..." },
                color = Color.Yellow,
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(32.dp))
            Text(
                text = "(Lift all fingers to commit cell)",
                color = Color.Gray,
                fontSize = 14.sp
            )
        }
    }
}
