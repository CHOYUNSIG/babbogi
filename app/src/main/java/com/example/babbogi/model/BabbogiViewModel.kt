package com.example.babbogi.model

import android.os.Build
import android.util.Log
import androidx.annotation.OptIn
import androidx.annotation.RequiresApi
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.view.LifecycleCameraController
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.babbogi.network.BarcodeApi
import com.example.babbogi.network.NutritionApi
import com.example.babbogi.network.ServerApi
import com.example.babbogi.util.HealthState
import com.example.babbogi.util.IntakeState
import com.example.babbogi.util.Nutrition
import com.example.babbogi.util.NutritionState
import com.example.babbogi.util.Product
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.concurrent.ExecutorService


class BabbogiViewModel: ViewModel() {
    // ML Kit 바코드 인식기
    private val barcodeRecognizer = BarcodeScanning.getClient(
        BarcodeScannerOptions.Builder()
            .setBarcodeFormats(Barcode.FORMAT_ALL_FORMATS)
            .enableAllPotentialBarcodes()
            .build()
    )

    // 상태 선언
    private val _productList = mutableStateOf<List<Pair<Product, Int>>>(emptyList())
    private val _product = mutableStateOf<Product?>(null)
    private val _dailyFoodList = mutableStateOf<Map<LocalDate, List<Pair<Product, Int>>>>(emptyMap())
    private val _nutritionState = mutableStateOf(DataPreference.getNutritionState() ?: NutritionState())
    private val _healthState = mutableStateOf(DataPreference.getHealthState())
    private val _isFetchingProduct = mutableStateOf(false)
    private val _isFetchingSuccess = mutableStateOf<Boolean?>(null)
    private val _isServerResponding = mutableStateOf(false)
    private val _isDailyFoodLoading = mutableStateOf(false)
    private val _isNutritionStateLoading = mutableStateOf(false)
    private val _isHealthStateLoading = mutableStateOf(false)
    private val _isNutritionRecommendationChanging = mutableStateOf(false)

    val product: Product? get() = _product.value // 현재 인식된 제품
    val productList: List<Pair<Product, Int>> get() = _productList.value // 인식된 제품 리스트
    val dailyFoodList: Map<LocalDate, List<Pair<Product, Int>>> get() = _dailyFoodList.value // 하루에 섭취한 음식 리스트
    val nutritionState: NutritionState get() = _nutritionState.value // 하루의 영양 상태
    val healthState: HealthState? get() = _healthState.value  // 사용자 건강 상태
    val isFetchingProduct: Boolean get() = _isFetchingProduct.value
    val isFetchingSuccess: Boolean? get() = _isFetchingSuccess.value
    val isServerResponding: Boolean get() = _isServerResponding.value
    val isDailyFoodLoading: Boolean get() = _isDailyFoodLoading.value
    val isNutritionStateLoading: Boolean get() = _isNutritionStateLoading.value
    val isHealthStateLoading: Boolean get() = _isHealthStateLoading.value

    fun truncateIntake() {
        val nutrition = _nutritionState.value
        val newNutrition = NutritionState(
            Nutrition.entries.associateWith { IntakeState(nutrition[it].recommended) }
        )
        _nutritionState.value = newNutrition
        DataPreference.saveNutritionState(newNutrition)
    }

    // 카메라를 이용해 상품 정보를 가져오기 시작함 (비동기)
    @OptIn(ExperimentalGetImage::class)
    fun asyncStartCameraRoutine(
        cameraController: LifecycleCameraController,
        executor: ExecutorService
    ) {
        cameraController.setImageAnalysisAnalyzer(executor) analyzing@ { imageProxy ->
            val image = imageProxy.image
            if (_isFetchingSuccess.value != null || _isFetchingProduct.value || image == null) {
                imageProxy.close()
                return@analyzing
            }
            barcodeRecognizer.process(
                InputImage.fromMediaImage(image, imageProxy.imageInfo.rotationDegrees)
            ).addOnCompleteListener { task ->
                viewModelScope.launch {
                    _isFetchingProduct.value = true
                    var success: Boolean? = null
                    try {
                        val barcode = task.result.firstOrNull { raw ->
                            raw.rawValue != null && raw.rawValue!!.isNotEmpty()
                        }?.rawValue ?: return@launch
                        Log.d("ViewModel", "Barcode: $barcode")
                        success = false
                        val prodName = BarcodeApi.getProducts(barcode).firstOrNull()?.name ?: return@launch
                        _product.value = Product(
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
                        _isFetchingProduct.value = false
                        _isFetchingSuccess.value = success
                        imageProxy.close()
                    }
                }
            }
        }
    }

    // 사용자가 바코드 인식 결과를 인지함
    fun confirmFetchingResult() {
        _isFetchingSuccess.value = null
    }

    // 현재 인식된 제품 버리기
    fun truncateProduct() {
        _product.value = null
    }

    // 리스트에 빈 음식 추가
    fun addProduct() {
        _productList.value = _productList.value.plus(
            Product("", null) to 1
        )
    }

    // 현재 인식된 제품 리스트에 추가
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

    // 섭취 리스트 서버 전송 (비동기)
    @RequiresApi(Build.VERSION_CODES.O)
    fun asyncSendListToServer() {
        _isServerResponding.value = true
        viewModelScope.launch {
            val productList = _productList.value
            try {
                val id = DataPreference.getID()!!
                ServerApi.postProductList(id, productList)
                val nutritionState = ServerApi.getNutritionState(id)
                DataPreference.saveNutritionState(nutritionState)
                _productList.value = emptyList()
                _nutritionState.value = nutritionState
            }
            catch (e: Exception) {
                e.printStackTrace()
                Log.d("ViewModel", "Cannot send list to server.")
            }
            finally {
                _isServerResponding.value = false
            }
        }
    }

    // 서버에서 해당 날짜의 섭취한 음식 정보 받아오기 (비동기)
    @RequiresApi(Build.VERSION_CODES.O)
    fun asyncGetFoodListFromServer(date: LocalDate) {
        if (_isDailyFoodLoading.value) return
        _isDailyFoodLoading.value = true
        viewModelScope.launch {
            try {
                val id = DataPreference.getID()!!
                val foodList = ServerApi.getProductList(id, date)
                val dailyFoodList = _dailyFoodList.value
                _dailyFoodList.value = dailyFoodList.plus(date to foodList)
            }
            catch (e: Exception) {
                e.printStackTrace()
                Log.d("ViewModel", "Cannot get list from server.")
            }
            finally {
                _isDailyFoodLoading.value = false
            }
        }
    }

    // 건강 정보를 변경하고 서버로 건강 정보를 전송한 뒤 조절된 권장량을 로드한다. (비동기)
    @RequiresApi(Build.VERSION_CODES.O)
    fun asyncChangeHealthStateWithServer(healthState: HealthState) {
        _isServerResponding.value = true
        viewModelScope.launch {
            try {
                val id = DataPreference.getID()
                val token = DataPreference.getToken()!!
                val newId = ServerApi.postHealthState(id, token, healthState)
                val nutritionState = ServerApi.getNutritionState(newId)
                DataPreference.saveID(newId)
                DataPreference.saveHealthState(healthState)
                DataPreference.saveNutritionState(nutritionState)
                _healthState.value = healthState
                _nutritionState.value = nutritionState
            }
            catch (e: Exception) {
                e.printStackTrace()
                Log.d("ViewModel", "Cannot send state to server.")
            }
            finally {
                _isServerResponding.value = false
            }
        }
    }

    // 서버에서 현재 건강 상태를 얻어온다. (비동기)
    fun asyncGetHealthStateFromServer() {
        _isHealthStateLoading.value = true
        viewModelScope.launch {
            try {
                val id = DataPreference.getID()!!
                val healthState = ServerApi.getHealthState(id)
                _healthState.value = healthState
                DataPreference.saveHealthState(healthState)
            }
            catch (e: Exception) {
                e.printStackTrace()
                Log.d("ViewModel", "Cannot get user state from server")
            }
            finally {
                _isHealthStateLoading.value = false
            }
        }
    }

    // 서버에서 현재 영양 상태를 얻어온다. (비동기)
    @RequiresApi(Build.VERSION_CODES.O)
    fun asyncGetNutritionStateFromServer() {
        _isNutritionStateLoading.value = true
        viewModelScope.launch {
            try {
                val id = DataPreference.getID()!!
                val nutritionState = ServerApi.getNutritionState(id)
                DataPreference.saveNutritionState(nutritionState)
                _nutritionState.value = nutritionState
            }
            catch (e: Exception) {
                e.printStackTrace()
                Log.d("ViewModel", "Cannot load nutrition state.")
            }
            finally {
                _isNutritionStateLoading.value = false
            }
        }
    }

    // 권장 섭취량을 수정하고 서버에 수정한 권장 섭취량을 전송한다.
    @RequiresApi(Build.VERSION_CODES.O)
    fun asyncChangeNutritionRecommendation(recommend: Map<Nutrition, Float>) {
        _isNutritionRecommendationChanging.value = true
        viewModelScope.launch {
            try {
                val id = DataPreference.getID()!!
                ServerApi.putNutritionRecommend(id, recommend)
                val nutritionState = ServerApi.getNutritionState(id)
                DataPreference.saveNutritionState(nutritionState)
                _nutritionState.value = nutritionState
            }
            catch (e: Exception) {
                e.printStackTrace()
                Log.d("ViewModel", "Cannot put nutrition recommend.")
            }
            finally {
                _isNutritionRecommendationChanging.value = false
            }
        }
    }
}
