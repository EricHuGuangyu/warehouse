package com.example.warehouse.ui.screen

import android.view.ViewGroup
import androidx.annotation.OptIn
import androidx.camera.core.CameraControl
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavHostController
import com.example.warehouse.R
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import java.util.concurrent.Executors

@Composable
fun BarcodeScannerScreen(navController: NavHostController) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }
    var cameraControl: CameraControl? by remember { mutableStateOf(null) }
    val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

    AndroidView(
        factory = { context ->
            val previewView = PreviewView(context).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            }

            val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
            cameraProviderFuture.addListener({
                val cameraProvider = cameraProviderFuture.get()
                val preview = Preview.Builder().build().also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }
                val camera = cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    cameraSelector,
                    preview
                )
                cameraControl = camera.cameraControl

                val barcodeScanner = BarcodeScanning.getClient()
                val imageAnalysis = ImageAnalysis.Builder()
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()
                    .also { analysis ->
                        analysis.setAnalyzer(cameraExecutor) { imageProxy ->
                            processImageProxy(barcodeScanner, imageProxy, navController, cameraProvider)
                        }
                    }

                try {
                    cameraProvider.unbindAll()
                    cameraProvider.bindToLifecycle(
                        lifecycleOwner,
                        cameraSelector,
                        preview,
                        imageAnalysis
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }, ContextCompat.getMainExecutor(context))

            previewView
        },
        modifier = Modifier.fillMaxSize()
    )
    // Draw Semi-transparent
    cameraControl?.let { ScannerOverlay(it) }
}

@Composable
fun ScannerOverlay(cameraControl:CameraControl) {
    // Dynamically control the animation of the flashing line
    val infiniteTransition = rememberInfiniteTransition(label = "")
    val lineOffsetY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ), label = ""
    )

    var isFlashOn by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        // Semi-transparent background
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawRect(
                color = Color.Black.copy(alpha = 0.5f),
                size = size
            )
        }

        // Instruction text
        Text(
            text = "Align the barcode to scan",
            color = Color.White,
            fontSize = 16.sp,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 100.dp) // Adjust the position of the text
        )

        // Flashlight icon
        IconButton(
            onClick = {
                isFlashOn = !isFlashOn
                cameraControl.enableTorch(isFlashOn) // Control flashlight
            },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 16.dp, end = 16.dp)
        ) {
            Icon(
                painter = painterResource(id = if (isFlashOn) R.drawable.ic_flash_on_black_24dp
                else R.drawable.ic_flash_off_black_24dp),
                contentDescription = if (isFlashOn) "Turn off flashlight" else "Turn on flashlight",
                tint = Color.White,
                modifier = Modifier.size(32.dp)
            )
        }

        // Scan frame and content
        Canvas(modifier = Modifier.fillMaxSize()) {
            val frameWidth = size.width * 0.8f
            val frameHeight = size.height * 0.3f

            val centerX = size.width / 2
            val centerY = size.height / 2 - 100.dp.toPx() // Move the scan frame up

            // Clear the middle area to make the scan frame transparent
            drawRect(
                color = Color.Transparent,
                topLeft = androidx.compose.ui.geometry.Offset(
                    x = centerX - frameWidth / 2,
                    y = centerY - frameHeight / 2
                ),
                size = androidx.compose.ui.geometry.Size(
                    width = frameWidth,
                    height = frameHeight
                ),
                blendMode = androidx.compose.ui.graphics.BlendMode.Clear
            )

            // Draw the corners of the scan frame
            val cornerLength = 30.dp.toPx()
            val cornerStrokeWidth = 4.dp.toPx()
            val rectTopLeft = androidx.compose.ui.geometry.Offset(
                x = centerX - frameWidth / 2,
                y = centerY - frameHeight / 2
            )
            val rectBottomRight = androidx.compose.ui.geometry.Offset(
                x = centerX + frameWidth / 2,
                y = centerY + frameHeight / 2
            )

            // Top-left corner
            drawLine(
                color = Color.White,
                start = rectTopLeft,
                end = rectTopLeft.copy(x = rectTopLeft.x + cornerLength),
                strokeWidth = cornerStrokeWidth
            )
            drawLine(
                color = Color.White,
                start = rectTopLeft,
                end = rectTopLeft.copy(y = rectTopLeft.y + cornerLength),
                strokeWidth = cornerStrokeWidth
            )

            // Top-right corner
            drawLine(
                color = Color.White,
                start = rectTopLeft.copy(x = rectBottomRight.x),
                end = rectTopLeft.copy(x = rectBottomRight.x - cornerLength),
                strokeWidth = cornerStrokeWidth
            )
            drawLine(
                color = Color.White,
                start = rectTopLeft.copy(x = rectBottomRight.x),
                end = rectTopLeft.copy(x = rectBottomRight.x, y = rectTopLeft.y + cornerLength),
                strokeWidth = cornerStrokeWidth
            )

            // Bottom-left corner
            drawLine(
                color = Color.White,
                start = rectBottomRight.copy(x = rectTopLeft.x),
                end = rectBottomRight.copy(x = rectTopLeft.x + cornerLength),
                strokeWidth = cornerStrokeWidth
            )
            drawLine(
                color = Color.White,
                start = rectBottomRight.copy(x = rectTopLeft.x),
                end = rectBottomRight.copy(x = rectTopLeft.x, y = rectBottomRight.y - cornerLength),
                strokeWidth = cornerStrokeWidth
            )

            // Bottom-right corner
            drawLine(
                color = Color.White,
                start = rectBottomRight,
                end = rectBottomRight.copy(x = rectBottomRight.x - cornerLength),
                strokeWidth = cornerStrokeWidth
            )
            drawLine(
                color = Color.White,
                start = rectBottomRight,
                end = rectBottomRight.copy(y = rectBottomRight.y - cornerLength),
                strokeWidth = cornerStrokeWidth
            )

            // Dynamically draw the flashing line in the middle of the scan frame
            val lineY = centerY - frameHeight / 2 + (frameHeight * lineOffsetY)
            drawLine(
                color = Color.Red,
                start = androidx.compose.ui.geometry.Offset(
                    x = centerX - frameWidth / 2,
                    y = lineY
                ),
                end = androidx.compose.ui.geometry.Offset(
                    x = centerX + frameWidth / 2,
                    y = lineY
                ),
                strokeWidth = 2.dp.toPx()
            )
        }
    }
}

@OptIn(ExperimentalGetImage::class)
private fun processImageProxy(
    barcodeScanner: BarcodeScanner,
    imageProxy: ImageProxy,
    navController: NavHostController,
    cameraProvider: ProcessCameraProvider
) {
    val mediaImage = imageProxy.image ?: return
    val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)

    barcodeScanner.process(image)
        .addOnSuccessListener { barcodes ->
            val hasNavigated = mutableStateOf(false)
            for (barcode in barcodes) {
                barcode.rawValue?.let { barCodeValue ->
                    if (!hasNavigated.value) {
                        hasNavigated.value = true
                        println(barCodeValue)
                        navController.navigate("productDetails/$barCodeValue")
                        cameraProvider.unbindAll()
                    }
                }
            }
        }
        .addOnFailureListener {
            it.printStackTrace()
        }
        .addOnCompleteListener {
            imageProxy.close()
        }
}
