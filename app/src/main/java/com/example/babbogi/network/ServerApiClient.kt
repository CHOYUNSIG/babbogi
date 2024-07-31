package com.example.babbogi.network

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.babbogi.BuildConfig
import com.example.babbogi.network.response.ServerConsumptionGetFormat
import com.example.babbogi.network.response.ServerConsumptionPostFormat
import com.example.babbogi.network.response.ServerHealthGetFormat
import com.example.babbogi.network.response.ServerHealthPostFormat
import com.example.babbogi.network.response.ServerMatchedFoodGetFormat
import com.example.babbogi.network.response.ServerNutritionRecommendationGetFormat
import com.example.babbogi.network.response.ServerNutritionRecommendationPutFormat
import com.example.babbogi.network.response.ServerSearchResultGetFormat
import com.example.babbogi.util.Consumption
import com.example.babbogi.util.HealthState
import com.example.babbogi.util.NutritionMap
import com.example.babbogi.util.NutritionRecommendation
import com.example.babbogi.util.Product
import com.example.babbogi.util.SearchResult
import com.google.gson.GsonBuilder
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Query
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

private val retrofit = Retrofit.Builder()
    .client(
        OkHttpClient.Builder()
            .connectTimeout(10, java.util.concurrent.TimeUnit.SECONDS)
            .writeTimeout(10, java.util.concurrent.TimeUnit.SECONDS)
            .readTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
            .build()
    )
    .addConverterFactory(ScalarsConverterFactory.create())
    .addConverterFactory(GsonConverterFactory.create(GsonBuilder().setLenient().create()))
    .baseUrl(BuildConfig.SERVER_URL + BuildConfig.SERVER_API_KEY)
    .build()

interface ServerApiService {
    @GET("consumptions/user")
    suspend fun getConsumeList(
        @Query(value = "userId") id: Long,
        @Query(value = "date") date: String
    ): List<ServerConsumptionGetFormat>

    @GET("User")
    suspend fun getHealthState(
        @Query(value = "id") id: Long
    ): List<ServerHealthGetFormat>

    @GET("UserNutrition")
    suspend fun getNutritionRecommendation(
        @Query(value = "id") id: Long
    ): ServerNutritionRecommendationGetFormat

    @GET("search")
    suspend fun getSearchResult(
        @Query(value = "name") word: String
    ): List<ServerSearchResultGetFormat>

    @GET("food")
    suspend fun getMatchedFood(
        @Query(value = "foodcode") id: String
    ): ServerMatchedFoodGetFormat

    @GET("dailyreport")
    suspend fun getDailyReport(
        @Query(value = "id") id: Long,
        @Query(value = "date") date: String,
    ): String

    @GET("report")
    suspend fun getPeriodReport(
        @Query(value = "id") id: Long,
        @Query(value = "startdate") startDate: String,
        @Query(value = "enddate") endDate: String
    ): String

    @POST("consumptions")
    suspend fun postProductList(
        @Query(value = "userId") id: Long,
        @Body body: List<ServerConsumptionPostFormat>
    ): List<ServerConsumptionGetFormat>

    @POST("consumptions/insert")
    suspend fun postProductListOnPast(
        @Query(value = "userId") id: Long,
        @Query(value = "date") date: String,
        @Body body: List<ServerConsumptionPostFormat>
    ): List<ServerConsumptionGetFormat>

    @POST("User")
    suspend fun postHealthState(
        @Query(value = "token") token: String,
        @Body body: ServerHealthPostFormat
    ): Long

    @PUT("User")
    suspend fun putNutritionRecommendation(
        @Query(value = "id") id: Long,
        @Body nutrition: ServerNutritionRecommendationPutFormat
    ): ServerNutritionRecommendationGetFormat

    @DELETE("consumptions/delete")
    suspend fun deleteConsumption(
        @Query(value = "id") seq: Long,
    ): Boolean
}

object ServerApi {
    private val retrofitService : ServerApiService by lazy {
        retrofit.create(ServerApiService::class.java)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun getProductList(id: Long, date: LocalDate): List<Consumption> {
        Log.d("ServerApi", "getProductList($id, $date)")
        return retrofitService.getConsumeList(id, date.toString())
            .filter { it.foodName != null }
            .map { it.toConsumption() }
            .sortedBy { it.time ?: LocalDateTime.MAX }
    }

    suspend fun getHealthState(id: Long): HealthState {
        Log.d("ServerApi", "getHealthState($id)")
        return retrofitService.getHealthState(id).last().toHealthState()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun getWeightHistory(id: Long): Map<LocalDateTime, Float> {
        Log.d("ServerApi", "getWeightTransition($id)")
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        return retrofitService.getHealthState(id)
            .associate { LocalDateTime.parse(it.date!!, formatter) to it.weight!!.toFloat() }
    }

    suspend fun getNutritionRecommendation(id: Long): NutritionRecommendation {
        Log.d("ServerApi", "getNutritionRecommendation($id)")
        return retrofitService.getNutritionRecommendation(id).toNutritionMap()
    }

    suspend fun getSearchResult(word: String): List<SearchResult> {
        Log.d("ServerApi", "getSearchResult($word)")
        return retrofitService.getSearchResult(word).map { it.toSearchResult() }
    }

    suspend fun getMatchedProduct(id: String): Product {
        Log.d("ServerApi", "getMatchedProduct($id)")
        return retrofitService.getMatchedFood(id).toProduct()
    }

    suspend fun getReport(id: Long, startDate: LocalDate, endDate: LocalDate = startDate): String {
        Log.d("ServerApi", "getPeriodReport($id, $startDate, $endDate)")
        return if (startDate == endDate) retrofitService.getDailyReport(id, startDate.toString())
        else retrofitService.getPeriodReport(id, startDate.toString(), endDate.toString())
    }

    suspend fun postProductList(id: Long, productList: List<Pair<Product, Float>>, date: LocalDate? = null) {
        Log.d("ServerApi", "postProductList($id, $productList, $date)")
        val body = productList.map { (product, intakeRatio) -> ServerConsumptionPostFormat.fromProduct(product, intakeRatio) }
        Log.d("ServerApi", "body: ${Json.encodeToJsonElement(body)}")
        if (date == null) retrofitService.postProductList(id, body)
        else retrofitService.postProductListOnPast(id, date.toString(), body)
    }

    suspend fun postHealthState(id: Long?, token: String, healthState: HealthState): Long {
        Log.d("ServerApi", "postHealthState($id, $token, $healthState")
        val body = ServerHealthPostFormat.fromHealthState(id, healthState)
        Log.d("ServerApi", "body: ${Json.encodeToJsonElement(body)}")
        return retrofitService.postHealthState("\"" + token + "\"", body)
    }

    suspend fun putNutritionRecommendation(id: Long, nutrition: NutritionMap<Float>) {
        Log.d("ServerApi", "putNutritionRecommend($id, $nutrition)")
        val body = ServerNutritionRecommendationPutFormat.fromNutritionMap(nutrition)
        Log.d("ServerApi", "body: ${Json.encodeToJsonElement(body)}")
        retrofitService.putNutritionRecommendation(id, body)
    }

    suspend fun deleteConsumption(seq: Long) {
        Log.d("ServerApi", "deleteConsumption($seq)")
        retrofitService.deleteConsumption(seq)
    }
}

