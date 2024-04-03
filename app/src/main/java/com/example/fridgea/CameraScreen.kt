package com.example.fridgea

import androidx.annotation.OptIn
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import kotlinx.coroutines.*
import java.util.concurrent.Executors


@OptIn(ExperimentalGetImage::class)
@Composable
fun CameraScreen() {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val previewView = remember { PreviewView(context) }
    val cameraController = remember { LifecycleCameraController(context) }
    cameraController.bindToLifecycle(lifecycleOwner)
    cameraController.cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
    previewView.controller = cameraController

    val executor = remember { Executors.newSingleThreadExecutor() }
    val barcodeRecognizer = remember {
        BarcodeScanning.getClient(
            BarcodeScannerOptions.Builder()
                .setBarcodeFormats(Barcode.FORMAT_ALL_FORMATS)
                .enableAllPotentialBarcodes()
                .build()
        )
    }

    var recognizedCode by rememberSaveable { mutableStateOf(emptyMap<String, Long>()) }
    var isLoading by remember { mutableStateOf(false) }

    // 카메라로부터 바코드 정보를 가져옴
    if (!isLoading) {
        runBlocking {
            launch {
                isLoading = true
                cameraController.setImageAnalysisAnalyzer(executor) { imageProxy ->
                    imageProxy.image?.let { image ->
                        val img = InputImage.fromMediaImage(image, imageProxy.imageInfo.rotationDegrees)
                        val generatedTime = System.nanoTime()

                        barcodeRecognizer.process(img).addOnCompleteListener { task ->
                            recognizedCode =
                                if (!task.isSuccessful)
                                    emptyMap()
                                else
                                    HashMap<String, Long>().also { newRecognizedCode ->
                                        task.result.forEach lambda@ { barcode ->
                                            val code = barcode.rawValue.toString()
                                            if (code.isEmpty())
                                                return@lambda
                                            newRecognizedCode[code] =
                                                if (code in recognizedCode.keys) recognizedCode[code]!!
                                                else generatedTime
                                        }
                                    }.toMap()
                            cameraController.clearImageAnalysisAnalyzer()
                            imageProxy.close()
                            isLoading = false
                        }
                    }
                }
            }
        }
    }
    
    // 화면 구성
    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(factory = { previewView }, modifier = Modifier.fillMaxSize())
    }

    Row(
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 12.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxHeight()
                .padding(top = 30.dp, bottom = 30.dp)
        ) {
            ElevatedCard(
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                modifier = Modifier
                    .fillMaxWidth(0.8f)
            ) {
                val time = System.nanoTime()
                Text(
                    text = recognizedCode
                        .filter { (_, generatedTime) -> time - generatedTime > 200_000_000 }
                        .map { (code, _) -> code }
                        .joinToString("\n"),
                    modifier = Modifier
                        .padding(16.dp),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            IconButton(
                onClick = { /*TODO*/ },
                modifier = Modifier
                    .size(100.dp, 100.dp)
                    .clip(RoundedCornerShape(50.dp))
                    .background(Color.Black)
            ) {
                Icon(
                    painter = painterResource(R.drawable.baseline_camera_alt_24),
                    contentDescription = "Capture"
                )
            }
        }
    }
}
