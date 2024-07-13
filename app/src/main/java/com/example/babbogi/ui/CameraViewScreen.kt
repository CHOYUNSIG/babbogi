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
import androidx.compose.runtime.remember
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
import com.example.babbogi.Screen
import com.example.babbogi.model.BabbogiViewModel
import com.example.babbogi.ui.view.ColumnWithDefault
import com.example.babbogi.ui.view.CustomAlertDialog
import com.example.babbogi.ui.view.ElevatedCardWithDefault
import com.example.babbogi.ui.view.ProductAbstraction
import com.example.babbogi.ui.view.TitleBar
import com.example.babbogi.util.Product
import com.example.babbogi.util.getRandomTestProduct
import com.example.babbogi.util.testProductList
import com.example.babbogi.util.testProductNull
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
        // 카메라 사용 설정
        val previewView = remember { PreviewView(context) }
        val cameraController = remember { LifecycleCameraController(context) }
        val executor = remember { Executors.newSingleThreadExecutor() }
        LaunchedEffect(key1 = true) {
            cameraController.bindToLifecycle(lifecycleOwner)
            cameraController.cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
            previewView.controller = cameraController
            viewModel.asyncStartCameraRoutine(cameraController, executor)
        }

        CameraView(
            cameraView = previewView,
            isProductFetching = viewModel.isFetchingProduct,
            isFetchingSuccess = viewModel.isFetchingSuccess,
            product = viewModel.product,
            onAddClicked = {
                viewModel.enrollProduct()
                viewModel.truncateProduct()
                viewModel.confirmFetchingResult()
            },
            onCancelClicked = {
                viewModel.truncateProduct()
                viewModel.confirmFetchingResult()
            },
        )
    }
}

@Composable
fun CameraPermissionDenied() {
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
                contentDescription = "camera",
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
fun NutritionPopup(
    isProductFetching: Boolean,
    isFetchingSuccess: Boolean?,
    product: Product?,
    onAddClicked: () -> Unit,
    onCancelClicked: () -> Unit
) {
    if (isProductFetching || isFetchingSuccess == true) Dialog(onDismissRequest = onCancelClicked) {
        ElevatedCardWithDefault {
            if (isProductFetching) Row (
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                CircularProgressIndicator(modifier = Modifier
                    .size(50.dp)
                    .padding(16.dp))
            }
            else if (product != null) ColumnWithDefault {
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
        dialogTitle = "찾을 수 없음",
        dialogText = "해당 상품은 찾을 수 없는 상품입니다.",
        iconResId = R.drawable.baseline_not_find_30
    )
}

@Composable
fun CameraView(
    cameraView: PreviewView,
    isProductFetching: Boolean,
    isFetchingSuccess: Boolean?,
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
                modifier = Modifier.fillMaxSize().weight(1f)
            ) {
                AndroidView(
                    factory = { cameraView },
                    modifier = Modifier.fillMaxSize(),
                )
            }
            Text("카메라로 바코드를 인식하세요.")
        }
    }

    if (isProductFetching || isFetchingSuccess != null) Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxHeight()
            .padding(horizontal = 30.dp)
    ) {
        NutritionPopup(
            isProductFetching = isProductFetching,
            isFetchingSuccess = isFetchingSuccess,
            product = product,
            onAddClicked = onAddClicked,
            onCancelClicked = onCancelClicked,
        )
    }
}

@Preview
@Composable
fun PreviewCameraView() {
    Scaffold {
        Box(modifier = Modifier.padding(it)) {
            val context = LocalContext.current

            CameraView(
                cameraView = remember { PreviewView(context) },
                isProductFetching = false,
                isFetchingSuccess = true,
                product = getRandomTestProduct(true),
                onAddClicked = {},
                onCancelClicked = {},
            )
        }
    }
}