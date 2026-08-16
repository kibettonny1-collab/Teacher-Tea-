package com.example.util

import android.annotation.SuppressLint
import android.content.Context
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import com.google.zxing.BinaryBitmap
import com.google.zxing.MultiFormatReader
import com.google.zxing.PlanarYUVLuminanceSource
import com.google.zxing.common.HybridBinarizer
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicBoolean

class CameraQrScannerHelper(
    private val context: Context,
    private val lifecycleOwner: LifecycleOwner,
    private val previewView: PreviewView,
    private val onQrCodeDetected: (String) -> Unit
) {
    private val cameraExecutor = Executors.newSingleThreadExecutor()
    private var camera: Camera? = null
    private var cameraProvider: ProcessCameraProvider? = null
    private var isTorchOn = false
    private var isBackCamera = true
    private var isScanningEnabled = AtomicBoolean(true)
    private var lastScannedTimestamp = 0L

    // ML Kit Barcode Scanner configured for QR codes
    private val mlKitScanner = BarcodeScanning.getClient(
        BarcodeScannerOptions.Builder()
            .setBarcodeFormats(
                Barcode.FORMAT_QR_CODE,
                Barcode.FORMAT_DATA_MATRIX,
                Barcode.FORMAT_AZTEC,
                Barcode.FORMAT_CODE_128,
                Barcode.FORMAT_CODE_39
            )
            .build()
    )

    // ZXing fallback reader
    private val zxingReader = MultiFormatReader()

    fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        cameraProviderFuture.addListener({
            try {
                cameraProvider = cameraProviderFuture.get()
                bindCameraUseCases()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }, ContextCompat.getMainExecutor(context))
    }

    private fun bindCameraUseCases() {
        val provider = cameraProvider ?: return

        // Unbind previous use cases
        provider.unbindAll()

        val cameraSelector = if (isBackCamera) {
            CameraSelector.DEFAULT_BACK_CAMERA
        } else {
            CameraSelector.DEFAULT_FRONT_CAMERA
        }

        // Preview use case
        val preview = Preview.Builder()
            .build()
            .also {
                it.setSurfaceProvider(previewView.surfaceProvider)
            }

        // Image Analysis use case
        val imageAnalysis = ImageAnalysis.Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()
            .also {
                it.setAnalyzer(cameraExecutor) { imageProxy ->
                    processImageProxy(imageProxy)
                }
            }

        try {
            camera = provider.bindToLifecycle(
                lifecycleOwner,
                cameraSelector,
                preview,
                imageAnalysis
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @SuppressLint("UnsafeOptInUsageError")
    private fun processImageProxy(imageProxy: ImageProxy) {
        val now = System.currentTimeMillis()
        if (!isScanningEnabled.get() || (now - lastScannedTimestamp < 400L)) {
            imageProxy.close()
            return
        }

        val mediaImage = imageProxy.image
        if (mediaImage != null) {
            val inputImage = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
            mlKitScanner.process(inputImage)
                .addOnSuccessListener { barcodes ->
                    var found = false
                    for (barcode in barcodes) {
                        val rawValue = barcode.rawValue
                        if (!rawValue.isNullOrBlank()) {
                            found = true
                            lastScannedTimestamp = System.currentTimeMillis()
                            ContextCompat.getMainExecutor(context).execute {
                                onQrCodeDetected(rawValue)
                            }
                            break
                        }
                    }

                    if (!found) {
                        // Try ZXing Fallback
                        fallbackZxingScan(imageProxy)
                    } else {
                        imageProxy.close()
                    }
                }
                .addOnFailureListener {
                    fallbackZxingScan(imageProxy)
                }
        } else {
            fallbackZxingScan(imageProxy)
        }
    }

    private fun fallbackZxingScan(imageProxy: ImageProxy) {
        try {
            val buffer = imageProxy.planes[0].buffer
            val data = ByteArray(buffer.remaining())
            buffer.get(data)
            val width = imageProxy.width
            val height = imageProxy.height

            val source = PlanarYUVLuminanceSource(
                data, width, height, 0, 0, width, height, false
            )
            val bitmap = BinaryBitmap(HybridBinarizer(source))
            val result = zxingReader.decodeWithState(bitmap)
            if (result != null && result.text.isNotBlank()) {
                lastScannedTimestamp = System.currentTimeMillis()
                ContextCompat.getMainExecutor(context).execute {
                    onQrCodeDetected(result.text)
                }
            }
        } catch (_: Exception) {
            // No QR detected in this frame
        } finally {
            zxingReader.reset()
            imageProxy.close()
        }
    }

    fun toggleTorch(): Boolean {
        camera?.let { cam ->
            if (cam.cameraInfo.hasFlashUnit()) {
                isTorchOn = !isTorchOn
                cam.cameraControl.enableTorch(isTorchOn)
                return isTorchOn
            }
        }
        return false
    }

    fun switchCamera(): Boolean {
        isBackCamera = !isBackCamera
        bindCameraUseCases()
        return isBackCamera
    }

    fun setScanningEnabled(enabled: Boolean) {
        isScanningEnabled.set(enabled)
    }

    fun stopCamera() {
        try {
            cameraProvider?.unbindAll()
            mlKitScanner.close()
            cameraExecutor.shutdown()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
