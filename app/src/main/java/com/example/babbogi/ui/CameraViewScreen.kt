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
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.example.babbogi.R
import com.example.babbogi.Screen
import com.example.babbogi.ui.model.BabbogiViewModel
import com.example.babbogi.ui.view.CustomIconButton
import com.example.babbogi.ui.view.AlertDialog
import com.example.babbogi.util.Nutrition
import com.example.babbogi.util.Product
import com.example.babbogi.util.testProduct
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
        LaunchedEffect(key1 = null) {
            cameraController.bindToLifecycle(lifecycleOwner)
            cameraController.cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
            previewView.controller = cameraController
            viewModel.asyncGetBarcodeFromCamera(cameraController, executor)
        }

        // 카메라 화면
        Box(modifier = Modifier.fillMaxSize()) {
            AndroidView(factory = { previewView }, modifier = Modifier.fillMaxSize())
        }

        var showDialog by remember { mutableStateOf(false) }

        CameraView(
            barcode = viewModel.validBarcode,
            showDialog = showDialog,
            isProductFetching = viewModel.isProductFetching,
            product = viewModel.product,
            onAddClicked = {
                viewModel.enrollProduct()
                viewModel.truncateProduct()
                showDialog = false
            },
            onCancelClicked = {
                viewModel.truncateProduct()
                showDialog = false
            },
            onCaptureClicked = {
                viewModel.asyncGetProductFromBarcode()
                showDialog = true
            },
            onGotoListClicked = { navController.navigate(Screen.FoodList.name) },
        )
    }
}

@Preview
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
                painter = painterResource(id = R.drawable.baseline_camera_alt_24),
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
fun NutritionPopup(
    isProductFetching: Boolean,
    product: Product?,
    onAddClicked: () -> Unit,
    onCancelClicked: () -> Unit
) {
    if (!isProductFetching && product == null) AlertDialog(
        onDismissRequest = {},
        onConfirmation = onCancelClicked,
        dialogTitle = "찾을 수 없음",
        dialogText = "해당 상품은 찾을 수 없는 상품입니다.",
        iconResId = R.drawable.baseline_not_find_30
    )
    else ElevatedCard(
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            if (isProductFetching) Row (
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                CircularProgressIndicator(modifier = Modifier
                    .size(50.dp)
                    .padding(16.dp))
            }
            else if (product != null) {
                Text(
                    modifier = Modifier.padding(bottom = 8.dp),
                    fontWeight = FontWeight.Bold,
                    text = product.name
                )
                if (product.nutrition != null)
                    Text(
                        modifier = Modifier.padding(bottom = 8.dp),
                        text = product.nutrition.toString(
                            List(Nutrition.entries.size) {
                                stringResource(id = Nutrition.entries[it].res)
                            }
                        )
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
fun CameraView(
    barcode: String? = testProduct.barcode,
    showDialog: Boolean = true,
    isProductFetching: Boolean = false,
    product: Product? = testProduct,
    onAddClicked: () -> Unit = {},
    onCancelClicked: () -> Unit = {},
    onCaptureClicked: () -> Unit = {},
    onGotoListClicked: () -> Unit = {},
) {
    if (barcode != null) Box(
        contentAlignment = Alignment.TopCenter,
        modifier = Modifier
            .fillMaxWidth()
            .padding(30.dp)
    ) {
        BarcodeCard(barcode)
    }
    if (showDialog) Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxHeight()
            .padding(horizontal = 30.dp)
    ) {
        NutritionPopup(isProductFetching, product, onAddClicked, onCancelClicked)
    }
    Box(
        contentAlignment = Alignment.BottomCenter,
        modifier = Modifier
            .fillMaxSize()
            .padding(50.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            CustomIconButton(onCaptureClicked, R.drawable.baseline_camera_alt_24)
            CustomIconButton(onGotoListClicked, R.drawable.baseline_list_24)
        }
    }
}
