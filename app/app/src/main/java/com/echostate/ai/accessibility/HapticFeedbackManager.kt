package com.echostate.ai.accessibility

import android.content.Context
import android.os.VibrationEffect
import android.os.VibratorManager

class HapticFeedbackManager(private val context: Context) {
    private val vibrator = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager

    fun vibrateTiltLeft() {
        // Pattern for "tilt left"
        val effect = VibrationEffect.createWaveform(longArrayOf(0, 100, 50, 100), -1)
        vibrator.defaultVibrator.vibrate(effect)
    }
    
    fun vibrateCentered() {
        // Crisp double-vibration for "centered"
        val effect = VibrationEffect.createWaveform(longArrayOf(0, 50, 50, 50), -1)
        vibrator.defaultVibrator.vibrate(effect)
    }

    fun vibrateObstacleWarning(distance: Float) {
        // Rapid ticks for close obstacles
        val timing = (distance * 100).toLong().coerceIn(20, 200)
        val effect = VibrationEffect.createWaveform(longArrayOf(0, timing, timing, timing), -1)
        vibrator.defaultVibrator.vibrate(effect)
    }
}
