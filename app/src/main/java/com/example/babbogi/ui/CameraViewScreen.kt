package com.example.babbogi.ui

import android.Manifest
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
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.example.babbogi.R
import com.example.babbogi.model.BabbogiViewModel
import com.example.babbogi.ui.theme.BabbogiTheme
import com.example.babbogi.ui.view.ColumnWithDefault
import com.example.babbogi.ui.view.CustomAlertDialog
import com.example.babbogi.ui.view.ElevatedCardWithDefault
import com.example.babbogi.ui.view.ProductAbstraction
import com.example.babbogi.ui.view.TitleBar
import com.example.babbogi.util.Product
import com.example.babbogi.util.getRandomTestProduct
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionRequired
import com.google.accompanist.permissions.rememberPermissionState
import java.util.concurrent.Executors

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CameraViewScreen(viewModel: BabbogiViewModel, navController: NavController) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

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
        val previewView = remember { PreviewView(context) }
        val cameraController = remember { LifecycleCameraController(context) }
        val executor = remember { Executors.newSingleThreadExecutor() }

        LaunchedEffect(true) {
            cameraController.bindToLifecycle(lifecycleOwner)
            cameraController.cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
            previewView.controller = cameraController
        }

        LaunchedEffect(recognizedCount) {
            viewModel.startCameraRoutine(
                cameraController = cameraController,
                executor = executor,
                onBarcodeRecognized = {
                    showDialog = true
                    isFetching = true
                },
                onProductFetched = {
                    isFetching = false
                    product = it
                }
            )
        }

        CameraView(
            cameraView = previewView,
            showDialog = showDialog,
            isFetching = isFetching,
            product = product,
            onAddClicked = {
                product?.let { viewModel.addProduct(it) }
                product = null
                showDialog = false
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
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth(0.8f)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.baseline_mode_24),
                contentDescription = null,
                modifier = Modifier
                    .size(50.dp)
                    .padding(end = 8.dp)
            )
            Text(
                text = "This function needs camera permission. Please grant it.",
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun ProductPopup(
    isFetching: Boolean,
    product: Product?,
    onAddClicked: () -> Unit,
    onCancelClicked: () -> Unit
) {
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
    else if (product != null) Dialog(onDismissRequest = onCancelClicked) {
        ElevatedCardWithDefault {
            ColumnWithDefault {
                ProductAbstraction(product = product, nullMessage = "서버에 영양 정보가 없습니다.")
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.End),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(onClick = onAddClicked) { Text(text = "Add") }
                    Button(onClick = onCancelClicked) { Text(text = "Cancel") }
                }
            }
        }
    }
    else CustomAlertDialog(
        onDismissRequest = onCancelClicked,
        onConfirmation = onCancelClicked,
        dialogTitle = "제품 정보 없음",
        dialogText = "바코드에 해당되는 제품 정보가 없습니다.",
        iconResId = R.drawable.baseline_not_find_30
    )
}

@Composable
private fun CameraView(
    cameraView: PreviewView,
    showDialog: Boolean,
    isFetching: Boolean,
    product: Product?,
    onAddClicked: () -> Unit,
    onCancelClicked: () -> Unit,
) {
    // 카메라 화면
    Column {
        TitleBar("바코드 입력")
        ColumnWithDefault(modifier = Modifier.fillMaxHeight()) {
            Card(
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
            ) {
                AndroidView(
                    factory = { cameraView },
                    modifier = Modifier.fillMaxSize(),
                )
            }
            Text("카메라로 바코드를 인식하세요")
        }
    }

    if (showDialog) Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxHeight()
            .padding(horizontal = 30.dp)
    ) {
        ProductPopup(
            isFetching = isFetching,
            product = product,
            onAddClicked = onAddClicked,
            onCancelClicked = onCancelClicked,
        )
    }
}

@Preview
@Composable
fun PreviewCameraView() {
    BabbogiTheme {
        Scaffold {
            Box(modifier = Modifier.padding(it)) {
                val context = LocalContext.current

                CameraView(
                    cameraView = remember { PreviewView(context) },
                    showDialog = true,
                    isFetching = false,
                    product = getRandomTestProduct(true),
                    onAddClicked = {},
                    onCancelClicked = {},
                )
            }
        }
    }
}