package com.echostate.ai.camera

import android.content.Context
import android.graphics.Bitmap
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.echostate.ai.llm.AgentLoopEngine

class CameraManager(
    private val context: Context,
    private val agentEngine: AgentLoopEngine
) {
    fun startCamera(lifecycleOwner: LifecycleOwner, surfaceProvider: androidx.camera.core.Preview.SurfaceProvider? = null) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        
        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build()
            
            surfaceProvider?.let {
                preview.setSurfaceProvider(it)
            }
            
            val imageAnalyzer = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
                .also {
                    it.setAnalyzer(ContextCompat.getMainExecutor(context)) { imageProxy ->
                        val bitmap = imageProxyToBitmap(imageProxy)
                        // Feed into the Sense-Decide-Act-Check loop
                        agentEngine.processCameraFrame(bitmap)
                        imageProxy.close()
                    }
                }

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    lifecycleOwner, CameraSelector.DEFAULT_BACK_CAMERA, preview, imageAnalyzer
                )
            } catch(exc: Exception) {
                // Handle error
            }
        }, ContextCompat.getMainExecutor(context))
    }

    private fun imageProxyToBitmap(imageProxy: ImageProxy): Bitmap {
        return try {
            imageProxy.toBitmap()
        } catch (_: Exception) {
            Bitmap.createBitmap(imageProxy.width.coerceAtLeast(1), imageProxy.height.coerceAtLeast(1), Bitmap.Config.ARGB_8888)
        }
    }
}
