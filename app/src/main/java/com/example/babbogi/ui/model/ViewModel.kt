package com.example.babbogi.ui.model

import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.view.LifecycleCameraController
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.babbogi.network.BarcodeApi
import com.example.babbogi.network.NutritionApi
import com.example.babbogi.util.NutritionInfo
import com.example.babbogi.util.ProductInfo
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import kotlinx.coroutines.Job
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
    private var _validBarcode = mutableStateOf(emptyList<String>())
    private var _products = mutableStateOf(emptyList<ProductInfo>())
    private var _recognizedBarcode = mutableStateOf(emptyMap<String, Long>())
    private var _isRecognizing = mutableStateOf(false)
    private var _isProductFetching = mutableStateOf(false)

    val validBarcode: List<String> get() = _validBarcode.value
    val products: List<ProductInfo> get() = _products.value
    val isProductFetching: Boolean get() = _isProductFetching.value

    // 카메라를 이용해 바코드 정보를 가져옴 (비동기)
    @OptIn(ExperimentalGetImage::class)
    fun asyncGetBarcodeFromCamera(
        cameraController: LifecycleCameraController,
        executor: ExecutorService
    ) {
        if (_isRecognizing.value)
            return
        _isRecognizing.value = true
        cameraController.setImageAnalysisAnalyzer(executor) { imageProxy ->
            imageProxy.image?.let { image ->
                barcodeRecognizer.process(
                    InputImage.fromMediaImage(image, imageProxy.imageInfo.rotationDegrees)
                ).addOnCompleteListener { task ->
                    val time = System.nanoTime()
                    // 인식된 바코드와 인식된 시간 저장
                    _recognizedBarcode.value = HashMap<String, Long>().also { newRecognizedCode ->
                        if (!task.isSuccessful)
                            return@also
                        task.result.forEach lambda@ { barcode ->
                            val code = barcode.rawValue.toString()
                            if (code.isEmpty())
                                return@lambda
                            newRecognizedCode[code] =
                                if (code in _recognizedBarcode.value.keys) _recognizedBarcode.value[code]!!
                                else time
                        }
                    }.toMap()
                    // 정확히 인식된 바코드를 추출
                    _validBarcode.value = _recognizedBarcode.value
                        .filter { (_, generatedTime) -> time - generatedTime > validRecognitionNanoTime }
                        .map { (code, _) -> code }
                    cameraController.clearImageAnalysisAnalyzer()
                    imageProxy.close()
                    _isRecognizing.value = false
                }
            }
        }
    }

    // 바코드 번호로 품명과 영양 정보를 가져옴 (비동기)
    fun asyncGetProductFromBarcode() {
        if (_isProductFetching.value)
            return
        _isProductFetching.value = true
        _products.value = emptyList()
        viewModelScope.launch {
            _products.value = emptyList<ProductInfo>().toMutableList().also { list ->
                val job = emptyList<Job>().toMutableList()
                _validBarcode.value.forEach { barcode ->
                    job.add(launch lambda@{
                        // API를 통해 상품명 가져오기
                        val barcodeResponse = BarcodeApi.getProducts(barcode)
                        if (barcodeResponse.total_count == 0)
                            return@lambda
                        val productName = barcodeResponse.row!!.first().PRDLST_NM
                        val nutritionResponse = NutritionApi.getNutrition(productName)
                        list.add(
                            ProductInfo(
                                productName,
                                barcode,
                                if (nutritionResponse.total_count != 0) {
                                    val nutrition = nutritionResponse.row!!.first()
                                    fun toFloat(string: String): Float = if (string.isNotBlank()) string.toFloat() else 0.0f
                                    NutritionInfo(
                                        toFloat(nutrition.SERVING_SIZE),
                                        toFloat(nutrition.SERVING_UNIT),
                                        toFloat(nutrition.NUTR_CONT1),
                                        toFloat(nutrition.NUTR_CONT2),
                                        toFloat(nutrition.NUTR_CONT3),
                                        toFloat(nutrition.NUTR_CONT4),
                                        toFloat(nutrition.NUTR_CONT5),
                                        toFloat(nutrition.NUTR_CONT6),
                                        toFloat(nutrition.NUTR_CONT7),
                                        toFloat(nutrition.NUTR_CONT8),
                                        toFloat(nutrition.NUTR_CONT9)
                                    )
                                }
                                else null
                            )
                        )
                    })
                }
                job.forEach { it.join() }
            }
            _isProductFetching.value = false
        }
    }

    fun cleanProduct() {
        _products.value = emptyList()
    }
}


class BabbogiViewModelFactory: ViewModelProvider.Factory {
    override fun<T: ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BabbogiViewModel::class.java))
            return BabbogiViewModel() as T
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}