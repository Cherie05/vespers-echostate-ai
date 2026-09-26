package com.echostate.ai.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.echostate.ai.ui.screens.BrailleScreen
import com.echostate.ai.ui.screens.TwoWayScreen
import com.echostate.ai.ui.screens.VoiceModeScreen
import com.echostate.ai.viewmodel.EchoStateViewModel

@Composable
fun AppNavigation(navController: NavHostController, viewModel: EchoStateViewModel) {
    NavHost(navController = navController, startDestination = "voice_mode") {
        composable("voice_mode") {
            VoiceModeScreen(
                viewModel = viewModel,
                onNavigateToBraille = { navController.navigate("braille_mode") },
                onNavigateToTwoWay = { navController.navigate("two_way_mode") }
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
