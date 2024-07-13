package com.example.babbogi.network

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.babbogi.BuildConfig
import com.example.babbogi.network.response.ServerConsumeFormat
import com.example.babbogi.network.response.ServerNutritionFormat
import com.example.babbogi.network.response.ServerUserStateFormat
import com.example.babbogi.network.response.toHealthState
import com.example.babbogi.network.response.toMap
import com.example.babbogi.network.response.toProduct
import com.example.babbogi.network.response.toRemainingMap
import com.example.babbogi.network.response.toServerNutritionFormat
import com.example.babbogi.network.response.toServerUserStateFormat
import com.example.babbogi.util.HealthState
import com.example.babbogi.util.IntakeState
import com.example.babbogi.util.Nutrition
import com.example.babbogi.util.NutritionMap
import com.example.babbogi.util.NutritionState
import com.example.babbogi.util.Product
import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Query
import java.time.LocalDate

private val retrofit = Retrofit.Builder()
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
    suspend fun getNutritionRecommend(
        @Query(value = "id") id: Long
    ): ServerNutritionFormat

    @GET("send-notification")
    suspend fun getNotification(
        @Query(value = "token") token: String
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
    suspend fun putNutritionRecommend(
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
        val response = retrofitService.getConsumeList(id, date.toString()).drop(1)
        return response.filter{ it.foodName != null }.map { it.toProduct() to it.foodCount }
    }

    suspend fun getHealthState(id: Long): HealthState {
        Log.d("ServerApi", "getHealthState($id)")
        val response = retrofitService.getUserData(id).last()
        return response.toHealthState()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun getNutritionState(id: Long): NutritionState {
        Log.d("ServerApi", "getNutritionState($id)")
        val recommend = retrofitService.getNutritionRecommend(id).toMap()
        val response = retrofitService.getConsumeList(id, LocalDate.now().toString()).drop(1)
        if (response.isEmpty()) return NutritionState(
            Nutrition.entries.associateWith { IntakeState(recommend[it]!!) }
        )
        val remain = response.maxBy { it.id }.toRemainingMap()
        return NutritionState(
            Nutrition.entries.associateWith { IntakeState(recommend[it]!!, recommend[it]!! - remain[it]!!) }
        )
    }

    suspend fun postProductList(id: Long, productList: List<Pair<Product, Int>>) {
        Log.d("ServerApi", "postProductList($id, $productList)")
        retrofitService.postProductList(id, productList.map { (product, amount) ->
            product.toServerNutritionFormat(amount)
        })
    }

    suspend fun postHealthState(id: Long?, token: String, healthState: HealthState): Long {
        Log.d("ServerApi", "postHealthState($id, $token, $healthState")
        val response = retrofitService.postUserData("\"" + token + "\"", healthState.toServerUserStateFormat(id))
        return response.toLong()
    }

    suspend fun putNutritionRecommend(id: Long, nutrition: NutritionMap<Float>) {
        Log.d("ServerApi", "putNutritionRecommend($id, $nutrition)")
        retrofitService.putNutritionRecommend(id, nutrition.toServerNutritionFormat(id))
    }
}

