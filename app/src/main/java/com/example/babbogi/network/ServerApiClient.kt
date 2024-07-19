package com.example.babbogi.network

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.babbogi.BuildConfig
import com.example.babbogi.network.response.ServerConsumeFormat
import com.example.babbogi.network.response.ServerFoodFormat
import com.example.babbogi.network.response.ServerNutritionFormat
import com.example.babbogi.network.response.ServerUserStateFormat
import com.example.babbogi.network.response.toServerNutritionFormat
import com.example.babbogi.network.response.toServerUserStateFormat
import com.example.babbogi.util.HealthState
import com.example.babbogi.util.NutritionMap
import com.example.babbogi.util.NutritionRecommendation
import com.example.babbogi.util.Product
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Query
import java.time.LocalDate
import java.time.LocalDateTime

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
    ): List<ServerConsumeFormat>

    @GET("User")
    suspend fun getUserData(
        @Query(value = "id") id: Long
    ): List<ServerUserStateFormat>

    @GET("UserNutrition")
    suspend fun getNutritionRecommendation(
        @Query(value = "id") id: Long
    ): ServerNutritionFormat

    @GET("search")
    suspend fun getSearchResult(
        @Query(value = "name") word: String
    ): List<String>

    @GET("food")
    suspend fun getFoodNutrition(
        @Query(value = "foodname") name: String
    ): ServerFoodFormat

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
        @Body body: List<ServerNutritionFormat>
    ): List<ServerConsumeFormat>

    @POST("User")
    suspend fun postUserData(
        @Query(value = "token") token: String,
        @Body body: ServerUserStateFormat
    ): String

    @PUT("User")
    suspend fun putNutritionRecommendation(
        @Query(value = "id") id: Long,
        @Body nutrition: ServerNutritionFormat
    ): ServerNutritionFormat
}


object ServerApi {
    private val retrofitService : ServerApiService by lazy {
        retrofit.create(ServerApiService::class.java)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun getProductList(id: Long, date: LocalDate): List<Pair<Product, Int>> {
        Log.d("ServerApi", "getProductList($id, $date)")
        return retrofitService.getConsumeList(id, date.toString())
            .filter{ it.foodName != null }
            .map { it.toProduct() to it.foodCount }
    }

    suspend fun getHealthState(id: Long): HealthState {
        Log.d("ServerApi", "getHealthState($id)")
        return retrofitService.getUserData(id).last().toHealthState()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun getWeightHistory(id: Long): List<Pair<LocalDateTime, Float>> {
        Log.d("ServerApi", "getWeightTransition($id)")
        return retrofitService.getUserData(id).map { LocalDateTime.parse(it.date) to it.weight.toFloat() }
    }

    suspend fun getNutritionRecommendation(id: Long): NutritionRecommendation {
        Log.d("ServerApi", "getNutritionRecommendation($id)")
        return retrofitService.getNutritionRecommendation(id).toMap()
    }

    suspend fun getSearchResult(word: String): List<String> {
        Log.d("ServerApi", "getSearchResult($word)")
        return retrofitService.getSearchResult(word)
    }

    suspend fun getMatchedProduct(name: String): Product {
        Log.d("ServerApi", "getMatchedProduct($name)")
        return retrofitService.getFoodNutrition(name).toProduct()
    }

    suspend fun getDailyReport(id: Long, date: LocalDate): String {
        Log.d("ServerApi", "getDailyReport($id, $date)")
        return retrofitService.getDailyReport(id, date.toString())
    }

    suspend fun getPeriodReport(id: Long, startDate: LocalDate, endDate: LocalDate): String {
        Log.d("ServerApi", "getPeriodReport($id, $startDate, $endDate)")
        return retrofitService.getPeriodReport(id, startDate.toString(), endDate.toString())
    }

    suspend fun postProductList(id: Long, productList: List<Pair<Product, Int>>) {
        Log.d("ServerApi", "postProductList($id, $productList)")
        retrofitService.postProductList(id, productList.map { (product, amount) -> product.toServerNutritionFormat(amount) })
    }

    suspend fun postHealthState(id: Long?, token: String, healthState: HealthState): Long {
        Log.d("ServerApi", "postHealthState($id, $token, $healthState")
        val response = retrofitService.postUserData("\"" + token + "\"", healthState.toServerUserStateFormat(id))
        return response.toLong()
    }

    suspend fun putNutritionRecommendation(id: Long, nutrition: NutritionMap<Float>) {
        Log.d("ServerApi", "putNutritionRecommend($id, $nutrition)")
        retrofitService.putNutritionRecommendation(id, nutrition.toServerNutritionFormat(id))
    }
}

