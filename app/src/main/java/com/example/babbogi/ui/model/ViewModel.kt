package com.example.babbogi.ui.model

import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.view.LifecycleCameraController
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.babbogi.network.BarcodeApi
import com.example.babbogi.network.NutritionApi
import com.example.babbogi.util.Product
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import kotlinx.coroutines.launch
import java.util.concurrent.ExecutorService

// 바코드 유효 인정 시간, 나노초 단위
private const val validRecognitionNanoTime = 200_000_000


class BabbogiViewModel: ViewModel() {
    // ML Kit 바코드 인식기
    private val barcodeRecognizer = BarcodeScanning.getClient(
        BarcodeScannerOptions.Builder()
            .setBarcodeFormats(Barcode.FORMAT_ALL_FORMATS)
            .enableAllPotentialBarcodes()
            .build()
    )

    // 상태 선언
    private var _validBarcode: MutableState<String?> = mutableStateOf(null)
    private var _productList: MutableState<List<Pair<Product, Int>>> = mutableStateOf(emptyList())
    private var _product: MutableState<Product?> = mutableStateOf(null)
    private var _recognizedBarcode = mutableStateOf(emptyMap<String, Long>())
    private var _isRecognizing = mutableStateOf(false)
    private var _isProductFetching = mutableStateOf(false)

    val validBarcode: String? get() = _validBarcode.value // 현재 인식 중인 바코드
    val product: Product? get() = _product.value // 현재 인식된 제품
    val productList: List<Pair<Product, Int>> get() = _productList.value // 인식된 제품 리스트
    val isProductFetching: Boolean get() = _isProductFetching.value // 제품 정보를 얻어오고 있는 상태인가?

    // 카메라를 이용해 바코드 정보를 가져옴 (비동기)
    @OptIn(ExperimentalGetImage::class)
    fun asyncGetBarcodeFromCamera(
        cameraController: LifecycleCameraController,
        executor: ExecutorService
    ) {
        if (_isRecognizing.value) return
        _isRecognizing.value = true
        cameraController.setImageAnalysisAnalyzer(executor) { imageProxy ->
            imageProxy.image?.let { image ->
                barcodeRecognizer.process(
                    InputImage.fromMediaImage(image, imageProxy.imageInfo.rotationDegrees)
                ).addOnSuccessListener { task ->
                    val time = System.nanoTime()
                    val preBarcodes = _recognizedBarcode.value
                    // 인식된 바코드와 인식된 시간 저장
                    _recognizedBarcode.value = emptyMap<String, Long>().toMutableMap().also { newMap ->
                        task.forEach lambda@ { barcode ->
                            val code = barcode.rawValue.toString()
                            if (code.isEmpty()) return@lambda
                            newMap[code] = preBarcodes[code] ?: time
                        }
                    }.toMap()
                    // 인식된 시간을 바탕으로 정확히 인식된 바코드를 추출
                    _validBarcode.value = _recognizedBarcode.value
                        .filter { (_, generatedTime) -> time - generatedTime > validRecognitionNanoTime }
                        .toList().maxByOrNull { it.second }?.first
                    imageProxy.close()
                    _isRecognizing.value = false
                }.addOnCanceledListener {
                    imageProxy.close()
                    _isRecognizing.value = false
                }.addOnFailureListener {
                    imageProxy.close()
                    _isRecognizing.value = false
                }
            }
        }
    }

    // 바코드 번호로 품명과 영양 정보를 가져옴 (비동기)
    fun asyncGetProductFromBarcode() {
        val barcode = _validBarcode.value
        if (_isProductFetching.value || barcode == null) return
        _isProductFetching.value = true
        viewModelScope.launch {
            val products = BarcodeApi.getProducts(barcode)
            if (products.isEmpty()) {
                _isProductFetching.value = false
                return@launch
            }
            val prodName = products.first().name
            _product.value = Product(
                prodName,
                barcode,
                try { NutritionApi.getNutrition(prodName).first() } catch (e: NoSuchElementException) { null },
            )
            _isProductFetching.value = false
        }
    }

    // 현재 인식된 제품 버리기
    fun truncateProduct() {
        _product.value = null
    }

    // 현재 인식된 제춤 리스트에 추가
    fun enrollProduct() {
        val product = _product.value
        if (product != null)
            _productList.value = _productList.value.plus(product to 1)
    }

    // 리스트에서 제품 정보 변경
    fun modifyProduct(
        index: Int,
        product: Product = _productList.value[index].first,
        amount: Int = _productList.value[index].second
    ) {
        val productList = _productList.value
        val newProductList = productList.mapIndexed { i, p ->
            if (i == index) product to amount else p
        }
        _productList.value = newProductList
    }

    // 리스트에서 제품 삭제
    fun deleteProduct(index: Int) {
        _productList.value = _productList.value.filterIndexed { i, _ -> i != index }
    }

    // 리스트 서버 전송
    fun sendList() {
        /* TODO */
        _productList.value = emptyList()
    }
}
