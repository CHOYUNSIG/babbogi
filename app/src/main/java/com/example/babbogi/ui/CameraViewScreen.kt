package com.example.babbogi.ui

import android.Manifest
import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.camera.core.CameraSelector
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.example.babbogi.R
import com.example.babbogi.Screen
import com.example.babbogi.model.BabbogiViewModel
import com.example.babbogi.ui.view.ColumnWithDefault
import com.example.babbogi.ui.view.CustomPopup
import com.example.babbogi.ui.view.DescriptionText
import com.example.babbogi.ui.view.ElevatedCardWithDefault
import com.example.babbogi.ui.view.ProductAbstraction
import com.example.babbogi.ui.view.ScreenPreviewer
import com.example.babbogi.util.Nutrition
import com.example.babbogi.util.Product
import com.example.babbogi.util.getRandomTestProduct
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionRequired
import com.google.accompanist.permissions.rememberPermissionState
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import java.util.concurrent.Executors

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CameraViewScreen(
    viewModel: BabbogiViewModel,
    navController: NavController,
    showSnackBar: (message: String) -> Unit,
    showAlertPopup: (title: String, message: String, icon: Int) -> Unit,
) {
    // 카메라 권한 설정
    val cameraPermission = rememberPermissionState(Manifest.permission.CAMERA)

    PermissionRequired(
        permissionState = cameraPermission,
        permissionNotGrantedContent = { LaunchedEffect(key1 = null) { cameraPermission.launchPermissionRequest() } },
        permissionNotAvailableContent = { CameraPermissionDenied() }
    ) {
        var product by remember { mutableStateOf<Product?>(null) }
        var showDialog by remember { mutableStateOf(false) }
        var isFetching by remember { mutableStateOf(false) }
        var recognizedCount by remember { mutableStateOf(0) }

        // 카메라 사용 설정
        val barcodeRecognizer = remember {
            BarcodeScanning.getClient(
                BarcodeScannerOptions.Builder()
                    .setBarcodeFormats(Barcode.FORMAT_ALL_FORMATS)
                    .enableAllPotentialBarcodes()
                    .build()
            )
        }
        val context = LocalContext.current
        val lifecycleOwner = LocalLifecycleOwner.current
        val previewView = remember { PreviewView(context) }
        val cameraController = remember { LifecycleCameraController(context) }
        val executor = remember { Executors.newSingleThreadExecutor() }

        LaunchedEffect(true) {
            cameraController.bindToLifecycle(lifecycleOwner)
            cameraController.cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
            previewView.controller = cameraController
        }

        LaunchedEffect(recognizedCount) {
            cameraController.setImageAnalysisAnalyzer(executor) analyzing@ { imageProxy ->
                val image = imageProxy.image
                if (image == null) {
                    imageProxy.close()
                    return@analyzing
                }
                barcodeRecognizer.process(
                    InputImage.fromMediaImage(image, imageProxy.imageInfo.rotationDegrees)
                ).addOnCompleteListener onCompleted@ { task ->
                    val barcode = task.result.map { raw -> raw.rawValue }.firstOrNull { it?.isNotEmpty() ?: false }
                    if (barcode == null) {
                        imageProxy.close()
                        return@onCompleted
                    }
                    isFetching = true
                    showDialog = true
                    viewModel.getProductByBarcode(barcode) {
                        cameraController.clearImageAnalysisAnalyzer()
                        product = it
                        isFetching = false
                        imageProxy.close()
                    }
                }
            }
        }

        CameraView(
            cameraView = {
                AndroidView(
                    factory = { previewView },
                    modifier = Modifier.fillMaxSize(),
                )
            },
            showDialog = showDialog,
            isFetching = isFetching,
            product = product,
            onAddClicked = {
                product?.let {
                    viewModel.addProduct(
                        it.copy(nutrition = it.nutrition ?: Nutrition.entries.associateWith { 0f })
                    )
                }
                product = null
                showDialog = false
                showSnackBar("음식이 추가되었습니다.")
                recognizedCount++
            },
            onCancelClicked = {
                product = null
                showDialog = false
                recognizedCount++
            },
        )
    }
}

@Composable
private fun CameraPermissionDenied() {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth(0.8f)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.baseline_photo_camera_24),
                contentDescription = null,
                modifier = Modifier
                    .size(50.dp)
                    .padding(end = 8.dp)
            )
            DescriptionText(
                text = "이 기능은 카메라 권한이 필요합니다.\n" +
                        "사용하려면 카메라 권한을 부여하세요."
            )
        }
    }
}

@Composable
private fun CameraView(
    cameraView: @Composable () -> Unit,
    showDialog: Boolean,
    isFetching: Boolean,
    product: Product?,
    onAddClicked: () -> Unit,
    onCancelClicked: () -> Unit,
) {
    ColumnWithDefault(modifier = Modifier.fillMaxHeight()) {
        Card(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxSize()
                .weight(1f),
            content = { cameraView() }
        )
        DescriptionText("카메라로 바코드를 인식하세요")
    }

    if (showDialog) {
        if (isFetching) Dialog(onDismissRequest = onCancelClicked) {
            ElevatedCardWithDefault {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .size(50.dp)
                            .padding(16.dp)
                    )
                }
            }
        }
        else if (product != null) CustomPopup(
            callbacks = listOf(onAddClicked, onCancelClicked),
            labels = listOf("추가", "취소"),
            onDismiss = onCancelClicked,
            title = "다음 상품을 추가하시겠습니까?"
        ) {
            ProductAbstraction(product = product, nullMessage = "서버에 영양 정보가 없습니다.")
        }
        else CustomPopup(
            callbacks = listOf(onCancelClicked),
            labels = listOf("확인"),
            onDismiss = onCancelClicked,
            title = "제품 정보 없음",
            icon = R.drawable.baseline_not_find_30
        ) {
            Text(text = "해당 상품은 찾을 수 없는 상품입니다.")
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(uiMode = UI_MODE_NIGHT_NO)
@Preview(uiMode = UI_MODE_NIGHT_YES)
@Composable
fun PreviewCameraView() {
    ScreenPreviewer(screen = Screen.Camera) {
        CameraView(
            cameraView = {},
            showDialog = true,
            isFetching = false,
            product = getRandomTestProduct(true),
            onAddClicked = {},
            onCancelClicked = {},
        )
    }
}