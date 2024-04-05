package com.example.fridgea.ui

import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.view.LifecycleCameraController
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.fridgea.network.BarcodeApi
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.concurrent.ExecutorService

// 바코드 유효 인정 시간, 나노초 단위
private const val validRecognitionNanoTime = 200_000_000


class CameraViewModel: ViewModel() {
    // ML Kit 바코드 인식기
    private val barcodeRecognizer = BarcodeScanning.getClient(
        BarcodeScannerOptions.Builder()
            .setBarcodeFormats(Barcode.FORMAT_ALL_FORMATS)
            .enableAllPotentialBarcodes()
            .build()
    )

    // 상태 선언
    private var _validBarcode = mutableStateOf(emptyList<String>())
    private var _products = mutableStateOf(emptyList<String>())
    private var _recognizedBarcode = mutableStateOf(emptyMap<String, Long>())
    private var _isRecognizing = mutableStateOf(false)
    private var _isProductFetching = mutableStateOf(false)

    val validBarcode: List<String> get() = _validBarcode.value
    val products: List<String> get() =  _products.value
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

    // 바코드 번호로 품명을 가져옴 (비동기)
    fun asyncGetProductFromBarcode() {
        if (_isProductFetching.value)
            return
        _isProductFetching.value = true
        _products.value = emptyList()
        viewModelScope.launch {
            _products.value = MutableList(0) { "" }.also { list ->
                val job = emptyList<Job>().toMutableList()
                _validBarcode.value.forEach { barcode ->
                    job.add(launch {
                        // API를 통해 상품명 가져오기
                        val response = BarcodeApi.retrofitService.getProducts(barcode).C005
                        if (response.total_count > 0)
                            list.add(response.row!![0].PRDLST_NM)
                    })
                }
                job.forEach { it.join() }
            }.toList()
            _isProductFetching.value = false
        }
    }
}


class CameraViewModelFactory: ViewModelProvider.Factory {
    override fun<T: ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CameraViewModel::class.java))
            return CameraViewModel() as T
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}