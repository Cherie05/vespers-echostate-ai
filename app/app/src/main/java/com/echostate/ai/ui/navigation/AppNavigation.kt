package com.echostate.ai.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.echostate.ai.ui.screens.BrailleScreen
import com.echostate.ai.ui.screens.TwoWayScreen
import com.echostate.ai.ui.screens.VoiceModeScreen
import com.echostate.ai.viewmodel.EchoStateViewModel

import com.echostate.ai.ui.screens.CameraScreen
import com.echostate.ai.ui.screens.LiveVoiceScreen

@Composable
fun AppNavigation(navController: NavHostController, viewModel: EchoStateViewModel) {
    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            VoiceModeScreen(
                viewModel = viewModel,
                onNavigateToBraille = { navController.navigate("braille_mode") },
                onNavigateToTwoWay = { navController.navigate("two_way_mode") },
                onNavigateToCamera = { navController.navigate("camera_mode") },
                onNavigateToVoice = { navController.navigate("live_voice") }
            )
        }
        composable("camera_mode") {
            CameraScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }
        composable("live_voice") {
            LiveVoiceScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }
        composable("braille_mode") {
            BrailleScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                onSendToTwoWay = { navController.navigate("two_way_mode") }
            )
        }
        composable("two_way_mode") {
            TwoWayScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
