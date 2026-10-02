package com.github.kmachida12345.simplemeditationlogger.data.device

import android.content.Context
import android.os.VibrationEffect
import android.os.VibratorManager
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VibrationHelper @Inject constructor(
    @ApplicationContext private val context: Context
) {
    fun vibrateOnCompletion() {
        val vibratorManager =
            context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                ?: return
        vibratorManager.defaultVibrator.vibrate(
            VibrationEffect.createWaveform(longArrayOf(0, 400, 200, 400), -1)
        )
    }
}
