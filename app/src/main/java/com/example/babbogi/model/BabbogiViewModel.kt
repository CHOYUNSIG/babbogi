package com.example.babbogi.model

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.babbogi.network.BarcodeApi
import com.example.babbogi.network.NutritionApi
import com.example.babbogi.network.ServerApi
import com.example.babbogi.util.Consumption
import com.example.babbogi.util.HealthState
import com.example.babbogi.util.Nutrition
import com.example.babbogi.util.NutritionRecommendation
import com.example.babbogi.util.Product
import com.example.babbogi.util.SearchResult
import com.example.babbogi.util.WeightHistory
import kotlinx.coroutines.launch
import java.time.LocalDate

class BabbogiViewModel: ViewModel() {
    private val _nutritionRecommendation = mutableStateOf(BabbogiModel.nutritionRecommendation ?: Nutrition.entries.associateWith { it.defaultRecommendation })
    private val _healthState = mutableStateOf(BabbogiModel.healthState)
    private val _isTutorialDone = mutableStateOf(BabbogiModel.isTutorialDone)
    private val _useServerRecommendation = mutableStateOf(BabbogiModel.useServerRecommendation)
    private val _notificationActivation = mutableStateOf(BabbogiModel.notificationActivation)
    @RequiresApi(Build.VERSION_CODES.O)
    private val _today = mutableStateOf(LocalDate.now())
    private val _productList = mutableStateOf(BabbogiModel.productList.map { Triple(it.first, it.second, true) })
    private val _periodReport = mutableStateOf<Pair<List<LocalDate>, String?>?>(null)
    private val _weightHistory = mutableStateOf<List<WeightHistory>?>(null)

    private val foodLists = mutableStateMapOf<LocalDate, List<Consumption>>()
    private val dailyReport = mutableStateMapOf<LocalDate, String>()

    var productList: List<Triple<Product, Float, Boolean>>
        get() = _productList.value
        private set(productList) {
            _productList.value = productList
            BabbogiModel.productList = productList.map { it.first to it.second }
        }

    var nutritionRecommendation: NutritionRecommendation
        get() = _nutritionRecommendation.value
        private set(nutritionRecommendation) {
            _nutritionRecommendation.value = nutritionRecommendation
            BabbogiModel.nutritionRecommendation = nutritionRecommendation
        }

    var healthState: HealthState?
        get() = _healthState.value
        private set(healthState) {
            _healthState.value = healthState
            BabbogiModel.healthState = healthState
        }

    var today: LocalDate
        @RequiresApi(Build.VERSION_CODES.O)
        get() = _today.value
        @RequiresApi(Build.VERSION_CODES.O)
        set(today) { _today.value = today }

    var isTutorialDone: Boolean
        get() = _isTutorialDone.value
        set(isTutorialDone) {
            _isTutorialDone.value = isTutorialDone
            BabbogiModel.isTutorialDone = isTutorialDone
        }

    var notificationActivation: Boolean
        get() = _notificationActivation.value
        set(notificationActivation) {
            _notificationActivation.value = notificationActivation
            BabbogiModel.notificationActivation = notificationActivation
        }

    var useServerRecommendation: Boolean
        get() = _useServerRecommendation.value
        set(useServerRecommendation) {
            _useServerRecommendation.value = useServerRecommendation
            BabbogiModel.useServerRecommendation = useServerRecommendation
        }

    var weightHistory: List<WeightHistory>?
        get() = _weightHistory.value
        set(weightHistory) { _weightHistory.value = weightHistory }

    var periodReport: Pair<List<LocalDate>, String?>?
        get() = _periodReport.value
        private set(periodReport) { _periodReport.value = periodReport }

    // 제품을 리스트에 추가
    fun addProduct(product: Product, intakeRatio: Float = 1f) {
        productList = productList.plus(Triple(product, intakeRatio, true))
    }

    // 리스트에서 제품 정보 변경
    fun modifyProduct(
        index: Int,
        product: Product = productList[index].first,
        intakeRatio: Float = productList[index].second,
        checked: Boolean = productList[index].third,
    ) {
        productList = productList.mapIndexed { i, triple -> if (i == index) Triple(product, intakeRatio, checked) else triple }
    }

    // 리스트에서 제품 삭제
    fun deleteProduct() {
        productList = productList.filter { !it.third }
    }

    // 공공데이터 API로 상품 검색
    fun getProductByBarcode(barcode: String, onFetchingEnded: (Product?) -> Unit) {
        viewModelScope.launch {
            var product: Product? = null
            try {
                val productName = BarcodeApi.getProducts(barcode).firstOrNull() ?: return@launch
                val (nutrition, servingSize) = NutritionApi.getNutrition(productName).firstOrNull() ?: (null to 100f)
                product = Product(
                    name = productName,
                    nutrition = nutrition,
                    servingSize = servingSize,
                )
            }
            catch (e: Exception) {
                e.printStackTrace()
                Log.d("ViewModel", "Cannot get product by barcode.")
            }
            finally {
                onFetchingEnded(product)
            }
        }
    }

    // 섭취 리스트 서버 전송
    @RequiresApi(Build.VERSION_CODES.O)
    fun sendList(date: LocalDate? = null, onEnded: (success: Boolean) -> Unit) {
        viewModelScope.launch {
            var success = false
            try {
                val today = date ?: LocalDate.now()
                val id = BabbogiModel.id!!
                ServerApi.postProductList(id, productList.filter { it.third }.map { it.first to it.second }, today)
                foodLists[today] = ServerApi.getProductList(id, today)
                deleteProduct()
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
    fun getFoodLists(
        startDate: LocalDate,
        endDate: LocalDate = startDate,
        refresh: Boolean = false,
        onFetchingEnded: (foodLists: Map<LocalDate, List<Consumption>>?) -> Unit
    ) {
        viewModelScope.launch {
            var result: MutableMap<LocalDate, List<Consumption>>? = mutableMapOf()
            try {
                var date = startDate
                while (date <= endDate) {
                    if (!foodLists.containsKey(date) || refresh)
                        foodLists[date] = ServerApi.getProductList(BabbogiModel.id!!, date)
                    result!![date] = foodLists[date]!!
                    date = date.plusDays(1)
                }
            }
            catch (e: Exception) {
                result = null
                e.printStackTrace()
                Log.d("ViewModel", "Cannot get list from server.")
            }
            finally {
                onFetchingEnded(result?.toMap())
            }
        }
    }

    // 건강 정보를 변경하고 서버로 건강 정보를 전송한 뒤 조절된 권장량 로드
    @RequiresApi(Build.VERSION_CODES.O)
    fun changeHealthState(
        healthState: HealthState,
        onEnded: (success: Boolean) -> Unit
    ) {
        viewModelScope.launch {
            var success = false
            try {
                val newId = ServerApi.postHealthState(BabbogiModel.id, BabbogiModel.token!!, healthState, useServerRecommendation)
                val recommendation = ServerApi.getNutritionRecommendation(newId)
                val history = ServerApi.getWeightHistory(newId)
                BabbogiModel.id = newId
                this@BabbogiViewModel.healthState = healthState
                nutritionRecommendation = recommendation
                weightHistory = history
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
            var success = false
            try {
                ServerApi.putNutritionRecommendation(BabbogiModel.id!!, recommendation)
                nutritionRecommendation = recommendation
                success = true
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

    // 서버에 음식 이름 검색
    fun searchWord(
        word: String,
        onSearchDone: (List<SearchResult>?) -> Unit
    ) {
        viewModelScope.launch {
            var result: List<SearchResult>? = null
            try {
                result = ServerApi.getSearchResult(word).sortedBy { it.name }
            }
            catch (e: Exception) {
                e.printStackTrace()
                Log.d("ViewModel", "Cannot search food.")
            }
            finally {
                onSearchDone(result)
            }
        }
    }
    
    // 서버에서 이름에 해당하는 음식 정보 얻어오기
    fun getProductByID(
        id: String,
        onSearchDone: (Product?) -> Unit
    ) {
        viewModelScope.launch {
            var result: Product? = null
            try {
                result = ServerApi.getMatchedProduct(id)
            }
            catch (e: Exception) {
                e.printStackTrace()
                Log.d("ViewModel", "Cannot get food by searching.")
            }
            finally {
                onSearchDone(result)
            }
        }
    }
    
    // 서버에서 일일 레포트 받아오기
    @RequiresApi(Build.VERSION_CODES.O)
    fun getDailyReport(
        date: LocalDate,
        generate: Boolean = true,
        refresh: Boolean = false,
        onFetchingEnded: (report: String?) -> Unit,
    ) {
        viewModelScope.launch {
            var report: String? = null
            try {
                if (generate && (!dailyReport.containsKey(date) || refresh))
                    dailyReport[date] = ServerApi.getReport(BabbogiModel.id!!, date)
                report = dailyReport[date]
            }
            catch (e: Exception) {
                e.printStackTrace()
                Log.d("ViewModel", "Cannot get daily report.")
            }
            finally {
                onFetchingEnded(report)
            }
        }
    }

    // 서버에서 기간 레포트 받아오기
    fun getPeriodReport(
        startDate: LocalDate,
        endDate: LocalDate,
        refresh: Boolean = false,
        onFetchingEnded: (report: String?) -> Unit,
    ) {
        viewModelScope.launch {
            var report: String? = null
            try {
                periodReport?.let { (period, preReport) ->
                    report = if (refresh || preReport == null || period.first() != startDate || period.last() != endDate)
                        ServerApi.getReport(BabbogiModel.id!!, startDate, endDate)
                    else preReport
                } ?: run {
                    report = ServerApi.getReport(BabbogiModel.id!!, startDate, endDate)
                }
                periodReport = listOf(startDate, endDate) to report
            }
            catch(e: Exception) {
                e.printStackTrace()
                Log.d("ViewModel", "Cannot get period report.")
            }
            finally {
                onFetchingEnded(report)
            }
        }
    }

    // 서버에서 몸무게 추이 받아오기
    @RequiresApi(Build.VERSION_CODES.O)
    fun getWeightHistory(
        refresh: Boolean = false,
        onFetchingEnded: (weightHistory: List<WeightHistory>?) -> Unit
    ) {
        viewModelScope.launch {
            try {
                if (refresh || weightHistory == null)
                    weightHistory = ServerApi.getWeightHistory(BabbogiModel.id!!)
            }
            catch(e: Exception) {
                e.printStackTrace()
                Log.d("ViewModel", "Cannot get weight history.")
            }
            finally {
                onFetchingEnded(weightHistory)
            }
        }
    }
    
    // 서버에서 음식 삭제
    fun deleteConsumption(
        id: Long,
        onEnded: (success: Boolean) -> Unit,
    ) {
        viewModelScope.launch {
            var success = false
            try {
                ServerApi.deleteConsumption(id)
                success = true
            }
            catch (e: Exception) {
                e.printStackTrace()
                Log.d("ViewModel", "Cannot delete consumption.")
            }
            finally {
                onEnded(success)
            }
        }
    }

    // 서버에서 몸무게 정보 수정
    @RequiresApi(Build.VERSION_CODES.O)
    fun changeWeightHistory(
        id: Long,
        weight: Float,
        onEnded: (success: Boolean) -> Unit,
    ) {
        viewModelScope.launch {
            var success = false
            try {
                ServerApi.putWeight(id, weight, useServerRecommendation)
                weightHistory = ServerApi.getWeightHistory(BabbogiModel.id!!)
                success = true
            }
            catch (e: Exception) {
                e.printStackTrace()
                Log.d("ViewModel", "Cannot change weight history.")
            }
            finally {
                onEnded(success)
            }
        }
    }

    // 서버에서 몸무게 정보 삭제
    @RequiresApi(Build.VERSION_CODES.O)
    fun deleteWeightHistory(
        id: Long,
        onEnded: (success: Boolean) -> Unit,
    ) {
        viewModelScope.launch {
            var success = false
            try {
                ServerApi.deleteWeight(id, useServerRecommendation)
                weightHistory = ServerApi.getWeightHistory(BabbogiModel.id!!)
                success = true
            }
            catch (e: Exception) {
                e.printStackTrace()
                Log.d("ViewModel", "Cannot delete weight history.")
            }
            finally {
                onEnded(success)
            }
        }
    }
}
