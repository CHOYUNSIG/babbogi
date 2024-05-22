package com.example.babbogi.ui

import androidx.annotation.OptIn
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.example.babbogi.BabbogiScreen
import com.example.babbogi.R
import com.example.babbogi.ui.model.BabbogiViewModel
import com.example.babbogi.ui.view.CustomIconButton
import com.example.babbogi.util.ProductInfo
import com.example.babbogi.util.testProduct
import java.util.concurrent.Executors


@OptIn(ExperimentalGetImage::class)
@Composable
fun CameraScreen(viewModel: BabbogiViewModel, navController: NavController) {
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

    Camera(
        barcode = viewModel.validBarcode,
        isProductFetching = viewModel.isProductFetching,
        productInfo = viewModel.product,
        onAddClicked = { viewModel.enrollProduct(); viewModel.truncateProduct() },
        onCancelClicked = { viewModel.truncateProduct() },
        onCaptureClicked = { viewModel.asyncGetProductFromBarcode() },
        onGotoListClicked = { navController.navigate(BabbogiScreen.FoodList.name) },
    )
}

@Composable
fun BarcodeCard(barcode: String) {
    ElevatedCard(
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        modifier = Modifier.fillMaxWidth(0.8f)
    ) {
        Column {
            Text(
                text = barcode,
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

@Composable
fun NutritionPopup(isProductFetching: Boolean, productInfo: ProductInfo?, onAddClicked: () -> Unit, onCancelClicked: () -> Unit) {
    ElevatedCard(
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            if (isProductFetching)
                Row (
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    CircularProgressIndicator(modifier = Modifier.size(50.dp).padding(16.dp))
                }
            else if (productInfo != null) {
                Text(
                    modifier = Modifier.padding(bottom = 8.dp),
                    fontWeight = FontWeight.Bold,
                    text = productInfo.name
                )
                if (productInfo.nutrition != null)
                    Text(
                        modifier = Modifier.padding(bottom = 8.dp),
                        text = productInfo.nutrition.toString()
                    )
                else
                    Text(text = "There's no nutrition info.")
                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(onClick = onAddClicked, modifier = Modifier.padding(start = 8.dp)) {
                        Text(text = "Add")
                    }
                    Button(onClick = onCancelClicked, modifier = Modifier.padding(start = 8.dp)) {
                        Text(text = "Cancel")
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun Camera(
    barcode: String? = testProduct.barcode,
    isProductFetching: Boolean = false,
    productInfo: ProductInfo? = testProduct,
    onAddClicked: () -> Unit = {},
    onCancelClicked: () -> Unit = {},
    onCaptureClicked: () -> Unit = {},
    onGotoListClicked: () -> Unit = {},
) {
    if (barcode != null)
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(30.dp)
        ) {
            BarcodeCard(barcode)
        }
    if (isProductFetching || productInfo != null)
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxHeight()
                .padding(horizontal = 30.dp)
        ) {
            NutritionPopup(isProductFetching, productInfo, onAddClicked, onCancelClicked)
        }
    Box(
        contentAlignment = Alignment.BottomEnd,
        modifier = Modifier
            .fillMaxSize()
            .padding(50.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            CustomIconButton(onCaptureClicked, R.drawable.baseline_camera_alt_24)
            CustomIconButton(onGotoListClicked, R.drawable.baseline_send_24)
        }
    }
}
