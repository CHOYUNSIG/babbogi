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
import com.example.babbogi.util.WeightHistory
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

private val retrofitClient = Retrofit.Builder()
    .addConverterFactory(ScalarsConverterFactory.create())
    .addConverterFactory(GsonConverterFactory.create(GsonBuilder().setLenient().create()))
    .baseUrl(BuildConfig.SERVER_URL + BuildConfig.SERVER_API_KEY)

private val longRetrofit = retrofitClient
    .client(
        OkHttpClient.Builder()
            .connectTimeout(5, java.util.concurrent.TimeUnit.SECONDS)
            .writeTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
            .readTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
            .build()
    )
    .build()

private val retrofit = retrofitClient
    .client(
        OkHttpClient.Builder()
            .connectTimeout(5, java.util.concurrent.TimeUnit.SECONDS)
            .writeTimeout(5, java.util.concurrent.TimeUnit.SECONDS)
            .readTimeout(5, java.util.concurrent.TimeUnit.SECONDS)
            .build()
    )
    .build()

interface ServerApiService {
    @GET("consumptions/user")
    suspend fun getConsumeList(
        @Query(value = "userId") id: Long,
        @Query(value = "date") date: String
    ): List<ServerConsumptionGetFormat>

    @GET("User")
    suspend fun getHealthState(
        @Query(value = "id") id: Long,
    ): List<ServerHealthGetFormat>

    @GET("UserNutrition")
    suspend fun getNutritionRecommendation(
        @Query(value = "id") id: Long,
    ): ServerNutritionRecommendationGetFormat

    @GET("search")
    suspend fun getSearchResult(
        @Query(value = "name") word: String,
    ): List<ServerSearchResultGetFormat>

    @GET("food")
    suspend fun getMatchedFood(
        @Query(value = "foodcode") foodID: String,
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
        @Query(value = "enddate") endDate: String,
    ): String

    @POST("consumptions")
    suspend fun postProductList(
        @Query(value = "userId") id: Long,
        @Body body: List<ServerConsumptionPostFormat>,
    ): List<ServerConsumptionGetFormat>

    @POST("consumptions/insert")
    suspend fun postProductListOnPast(
        @Query(value = "userId") id: Long,
        @Query(value = "date") date: String,
        @Body body: List<ServerConsumptionPostFormat>,
    ): List<ServerConsumptionGetFormat>

    @POST("User")
    suspend fun postHealthState(
        @Query(value = "token") token: String,
        @Query(value = "recommendation") useServerRecommendation: Boolean,
        @Body body: ServerHealthPostFormat,
    ): Long

    @PUT("User")
    suspend fun putNutritionRecommendation(
        @Body nutrition: ServerNutritionRecommendationPutFormat,
    ): ServerNutritionRecommendationGetFormat

    @PUT("updateweight")
    suspend fun putWeight(
        @Query(value = "id") seq: Long,
        @Query(value = "weight") weight: Double,
        @Query(value = "recommendation") useServerRecommendation: Boolean,
    )

    @DELETE("consumptions/delete")
    suspend fun deleteConsumption(
        @Query(value = "id") seq: Long,
    ): Boolean

    @DELETE("deleteweight")
    suspend fun deleteWeight(
        @Query(value = "id") seq: Long,
        @Query(value = "recommendation") useServerRecommendation: Boolean,
    )
}

object ServerApi {
    private val longRetrofitService : ServerApiService by lazy {
        longRetrofit.create(ServerApiService::class.java)
    }

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
    suspend fun getWeightHistory(id: Long): List<WeightHistory> {
        Log.d("ServerApi", "getWeightTransition($id)")
        return retrofitService.getHealthState(id).map { it.toWeightHistory() }.sortedBy { it.date }
    }

    suspend fun getNutritionRecommendation(id: Long): NutritionRecommendation {
        Log.d("ServerApi", "getNutritionRecommendation($id)")
        return retrofitService.getNutritionRecommendation(id).toNutritionMap()
    }

    suspend fun getSearchResult(word: String): List<SearchResult> {
        Log.d("ServerApi", "getSearchResult($word)")
        return retrofitService.getSearchResult(word).map { it.toSearchResult() }
    }

    suspend fun getMatchedProduct(foodID: String): Product {
        Log.d("ServerApi", "getMatchedProduct($foodID)")
        return retrofitService.getMatchedFood(foodID).toProduct()
    }

    suspend fun getReport(id: Long, startDate: LocalDate, endDate: LocalDate = startDate): String {
        Log.d("ServerApi", "getPeriodReport($id, $startDate, $endDate)")
        return if (startDate == endDate) longRetrofitService.getDailyReport(id, startDate.toString())
        else longRetrofitService.getPeriodReport(id, startDate.toString(), endDate.toString())
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun postProductList(id: Long, productList: List<Pair<Product, Float>>, date: LocalDate) {
        Log.d("ServerApi", "postProductList($id, $productList, $date)")
        val body = productList.map { (product, intakeRatio) -> ServerConsumptionPostFormat.fromProduct(product, intakeRatio) }
        Log.d("ServerApi", "body: ${Json.encodeToJsonElement(body)}")
        if (date == LocalDate.now()) retrofitService.postProductList(id, body)
        else retrofitService.postProductListOnPast(id, date.toString(), body)
    }

    suspend fun postHealthState(id: Long?, token: String, healthState: HealthState, useServerRecommendation: Boolean): Long {
        Log.d("ServerApi", "postHealthState($id, $token, $healthState")
        val body = ServerHealthPostFormat.fromHealthState(id, healthState)
        Log.d("ServerApi", "body: ${Json.encodeToJsonElement(body)}")
        return retrofitService.postHealthState(token, useServerRecommendation, body)
    }

    suspend fun putNutritionRecommendation(id: Long, nutrition: NutritionMap<Float>) {
        Log.d("ServerApi", "putNutritionRecommend($id, $nutrition)")
        val body = ServerNutritionRecommendationPutFormat.fromNutritionMap(id, nutrition)
        Log.d("ServerApi", "body: ${Json.encodeToJsonElement(body)}")
        retrofitService.putNutritionRecommendation(body)
    }

    suspend fun putWeight(seq: Long, weight: Float, useServerRecommendation: Boolean) {
        Log.d("ServerApi", "putWeight($seq, $weight)")
        retrofitService.putWeight(seq, weight.toDouble(), useServerRecommendation)
    }

    suspend fun deleteConsumption(seq: Long) {
        Log.d("ServerApi", "deleteConsumption($seq)")
        retrofitService.deleteConsumption(seq)
    }

    suspend fun deleteWeight(seq: Long, useServerRecommendation: Boolean) {
        Log.d("ServerApi", "deleteWeight($seq, $useServerRecommendation)")
        retrofitService.deleteWeight(seq, useServerRecommendation)
    }
}

