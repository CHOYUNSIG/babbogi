package com.example.babbogi.ui

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
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.babbogi.R
import com.example.babbogi.ui.model.CameraViewModel
import kotlinx.coroutines.*
import java.util.concurrent.Executors


@OptIn(ExperimentalGetImage::class)
@Composable
fun CameraScreen(viewModel: CameraViewModel) {
    // 카메라 사용 설정
    val context = LocalContext.current
    val previewView = remember { PreviewView(context) }
    val cameraController = remember { LifecycleCameraController(context) }
    val executor = remember { Executors.newSingleThreadExecutor() }
    cameraController.bindToLifecycle(LocalLifecycleOwner.current)
    cameraController.cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
    previewView.controller = cameraController

    // 바코드 얻어오기 시작
    viewModel.asyncGetBarcodeFromCamera(cameraController, executor)

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
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxHeight()
                .padding(top = 30.dp, bottom = 30.dp)
        ) {
            BarcodeCard(viewModel)
            NutritionCard(viewModel)
            CaptureButton(viewModel)
        }
    }
}

@Composable
fun BarcodeCard(viewModel: CameraViewModel) {
    ElevatedCard(
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        modifier = Modifier.fillMaxWidth(0.8f)
    ) {
        Column {
            viewModel.validBarcode.forEach {
                Text(
                    text = it,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}

@Composable
fun NutritionCard(viewModel: CameraViewModel) {
    ElevatedCard(
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        modifier = Modifier.fillMaxWidth(0.8f)
    ) {
        if (viewModel.isProductFetching) {
            Row(
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(50.dp)
                        .padding(16.dp)
                )
            }
        }
        else if (viewModel.products.isNotEmpty()) {
            viewModel.products.forEach { (prodName, _, nutrition) ->
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(modifier = Modifier.padding(bottom = 8.dp), fontWeight = FontWeight.Bold, text = prodName)
                    if (nutrition != null) {
                        Text(modifier = Modifier.padding(bottom = 8.dp), text = nutrition.toString())
                        Text(text = "per ${100 * nutrition.servingUnit / nutrition.servingVolume}% of the total product.")
                    }
                    else {
                        Text(text = "There's no nutrition info.")
                    }
                }
            }
            Row(
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(16.dp)
            ) {
                Button(onClick = { viewModel.cleanProduct() }) {
                    Text(text = "Done")
                }
            }
        }
    }
}

@Composable
fun CaptureButton(viewModel: CameraViewModel) {
    IconButton(
        onClick = { viewModel.asyncGetProductFromBarcode() },
        modifier = Modifier
            .size(80.dp, 80.dp)
            .clip(RoundedCornerShape(40.dp))
            .background(Color.Black)
    ) {
        Icon(
            painter = painterResource(R.drawable.baseline_camera_alt_24),
            contentDescription = "Capture"
        )
    }
}
