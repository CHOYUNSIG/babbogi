package com.example.babbogi.model

import android.os.Build
import android.util.Log
import androidx.annotation.OptIn
import androidx.annotation.RequiresApi
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.view.LifecycleCameraController
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.babbogi.network.BarcodeApi
import com.example.babbogi.network.NutritionApi
import com.example.babbogi.network.ServerApi
import com.example.babbogi.util.HealthState
import com.example.babbogi.util.Nutrition
import com.example.babbogi.util.NutritionRecommendation
import com.example.babbogi.util.Product
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.concurrent.ExecutorService

private val barcodeRecognizer = BarcodeScanning.getClient(
    BarcodeScannerOptions.Builder()
        .setBarcodeFormats(Barcode.FORMAT_ALL_FORMATS)
        .enableAllPotentialBarcodes()
        .build()
)

class BabbogiViewModel: ViewModel() {
    private val _nutritionRecommendation = mutableStateOf(BabbogiModel.nutritionRecommendation ?: Nutrition.entries.associateWith { it.defaultRecommend })
    private val _healthState = mutableStateOf(BabbogiModel.healthState)
    private val _productList = mutableStateOf<List<Pair<Product, Int>>>(emptyList())
    private val _today = mutableStateOf(LocalDate.now())
    private val _periodReport = mutableStateOf<String?>(null)
    private val foodLists = mutableStateMapOf<LocalDate, List<Pair<Product, Int>>>()
    private val dailyReport = mutableStateMapOf<LocalDate, String>()

    var productList: List<Pair<Product, Int>>
        get() = _productList.value
        private set(productList) { _productList.value = productList }

    var nutritionRecommendation: NutritionRecommendation
        get() = _nutritionRecommendation.value
        private set(nutritionRecommendation) { _nutritionRecommendation.value = nutritionRecommendation }

    var healthState: HealthState?
        get() = _healthState.value
        private set(healthState) { _healthState.value = healthState }

    var today: LocalDate
        get() = _today.value
        set(today) { _today.value = today }

    var isTutorialDone: Boolean
        get() = BabbogiModel.isTutorialDone
        set(isTutorialDone) { BabbogiModel.isTutorialDone = isTutorialDone }

    var periodReport: String?
        get() = _periodReport.value
        private set(periodReport) { _periodReport.value = periodReport }

    // 카메라를 이용해 상품 정보를 가져오기 시작함
    @OptIn(ExperimentalGetImage::class)
    fun startCameraRoutine(
        cameraController: LifecycleCameraController,
        executor: ExecutorService,
        onBarcodeRecognized: (barcode: String) -> Unit,
        onProductFetched: (product: Product?) -> Unit,
    ) {
        var isFetching = false
        cameraController.setImageAnalysisAnalyzer(executor) analyzing@ { imageProxy ->
            val image = imageProxy.image
            if (isFetching || image == null) {
                imageProxy.close()
                return@analyzing
            }
            barcodeRecognizer.process(
                InputImage.fromMediaImage(image, imageProxy.imageInfo.rotationDegrees)
            ).addOnCompleteListener { task ->
                viewModelScope.launch {
                    isFetching = true
                    var success: Boolean? = null
                    var product: Product? = null
                    try {
                        val barcode = task.result.firstOrNull { raw ->
                            raw.rawValue != null && raw.rawValue!!.isNotEmpty()
                        }?.rawValue ?: return@launch
                        onBarcodeRecognized(barcode)
                        success = false
                        val prodName = BarcodeApi.getProducts(barcode).firstOrNull()?.name ?: return@launch
                        product = Product(
                            prodName,
                            NutritionApi.getNutrition(prodName).firstOrNull(),
                        )
                        success = true
                    }
                    catch (e: Exception) {
                        e.printStackTrace()
                        Log.d("ViewModel", "Cannot get product info")
                    }
                    finally {
                        if (success != null) onProductFetched(product)
                        isFetching = false
                        imageProxy.close()
                    }
                }
            }
        }
    }

    // 제품을 리스트에 추가
    fun addProduct(product: Product = Product("", null)) {
        productList = productList.plus(product to 1)
    }

    // 리스트에서 제품 정보 변경
    fun modifyProduct(
        index: Int,
        product: Product = _productList.value[index].first,
        amount: Int = _productList.value[index].second,
    ) {
        productList = productList.mapIndexed { i, p -> if (i == index) product to amount else p }
    }

    // 리스트에서 제품 삭제
    fun deleteProduct(index: Int) {
        productList = productList.filterIndexed { i, _ -> i != index }
    }

    // 섭취 리스트 서버 전송
    @RequiresApi(Build.VERSION_CODES.O)
    fun sendList(onEnded: (success: Boolean) -> Unit) {
        viewModelScope.launch {
            var success = false
            try {
                val today = LocalDate.now()
                val id = BabbogiModel.id!!
                ServerApi.postProductList(id, productList)
                val products = ServerApi.getProductList(id, today)
                foodLists[today] = products
                productList = emptyList()
                success = true
            }
            catch (e: Exception) {
                e.printStackTrace()
                Log.d("ViewModel", "Cannot send list to server.")
            }
            finally {
                onEnded(success)
            }
        }
    }

    // 해당 날짜의 섭취한 음식 리스트 받아오기
    @RequiresApi(Build.VERSION_CODES.O)
    fun getFoodList(
        date: LocalDate,
        refresh: Boolean = false,
        onFetchingEnded: (foodList: List<Pair<Product, Int>>?) -> Unit
    ) {
        viewModelScope.launch {
            try {
                if (!foodLists.containsKey(date) || refresh)
                    foodLists[date] = ServerApi.getProductList(BabbogiModel.id!!, date)
            }
            catch (e: Exception) {
                e.printStackTrace()
                Log.d("ViewModel", "Cannot get list from server.")
            }
            finally {
                onFetchingEnded(foodLists[date])
            }
        }
    }

    // 건강 정보를 변경하고 서버로 건강 정보를 전송한 뒤 조절된 권장량 로드
    @RequiresApi(Build.VERSION_CODES.O)
    fun changeHealthState(healthState: HealthState, onEnded: (success: Boolean) -> Unit) {
        viewModelScope.launch {
            var success = false
            try {
                val newId = ServerApi.postHealthState(BabbogiModel.id, BabbogiModel.token!!, healthState)
                val recommendation = ServerApi.getNutritionRecommendation(newId)
                BabbogiModel.id = newId
                BabbogiModel.healthState = healthState
                BabbogiModel.nutritionRecommendation = recommendation
                nutritionRecommendation = recommendation
                success = true
            }
            catch (e: Exception) {
                e.printStackTrace()
                Log.d("ViewModel", "Cannot send state to server.")
            }
            finally {
                onEnded(success)
            }
        }
    }

    // 서버에서 현재 건강 상태 얻어오기
    fun getHealthStateFromServer(onEnded: (Boolean) -> Unit) {
        viewModelScope.launch {
            var success = false
            try {
                healthState = ServerApi.getHealthState(BabbogiModel.id!!)
                BabbogiModel.healthState = healthState
                success = true
            }
            catch (e: Exception) {
                e.printStackTrace()
                Log.d("ViewModel", "Cannot get user state from server")
            }
            finally {
                onEnded(success)
            }
        }
    }

    // 권장 섭취량을 수정하고 서버에 수정한 권장 섭취량을 전송
    @RequiresApi(Build.VERSION_CODES.O)
    fun changeNutritionRecommendation(
        recommendation: NutritionRecommendation,
        onEnded: (success: Boolean) -> Unit
    ) {
        viewModelScope.launch {
            val success = false
            try {
                ServerApi.putNutritionRecommendation(BabbogiModel.id!!, recommendation)
                BabbogiModel.nutritionRecommendation = recommendation
                nutritionRecommendation = recommendation
            }
            catch (e: Exception) {
                e.printStackTrace()
                Log.d("ViewModel", "Cannot put nutrition recommend.")
            }
            finally {
                onEnded(success)
            }
        }
    }
}
