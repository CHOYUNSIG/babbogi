package com.example.babbogi.network

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.babbogi.BuildConfig
import com.example.babbogi.network.response.ServerConsumeFormat
import com.example.babbogi.network.response.ServerNutritionFormat
import com.example.babbogi.network.response.ServerUserStateFormat
import com.example.babbogi.network.response.toMap
import com.example.babbogi.network.response.toRemainingMap
import com.example.babbogi.network.response.toServerNutritionFormat
import com.example.babbogi.util.AdultDisease
import com.example.babbogi.util.Gender
import com.example.babbogi.util.HealthState
import com.example.babbogi.util.IntakeState
import com.example.babbogi.util.Nutrition
import com.example.babbogi.util.NutritionState
import com.example.babbogi.util.Product
import com.example.babbogi.util.ProductNutritionInfo
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Query
import java.time.LocalDate

private val retrofit = Retrofit.Builder()
    .addConverterFactory(GsonConverterFactory.create())
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
        val response = retrofitService.getConsumeList(id, date.toString()).drop(1)
        Log.d("ServerApi", "getProductList($id, $date)")
        return response.filter{
            it.foodName != null
        }.map {
            Product(
                name = it.foodName!!,
                barcode = "",
                ProductNutritionInfo(
                    mapOf(
                        Nutrition.Fat to it.fat!!.toFloat(),
                        Nutrition.Salt to it.natrium!!.toFloat(),
                        Nutrition.Sugar to it.sugar!!.toFloat(),
                        Nutrition.Calorie to it.kcal!!.toFloat(),
                        Nutrition.Protein to it.protein!!.toFloat(),
                        Nutrition.TransFat to it.transfat!!.toFloat(),
                        Nutrition.Cholesterol to it.cholesterol!!.toFloat(),
                        Nutrition.Carbohydrate to it.carbohydrate!!.toFloat(),
                        Nutrition.SaturatedFat to it.saturatedfat!!.toFloat(),
                    )
                )
            ) to it.foodCount
        }
    }

    suspend fun getHealthState(id: Long): HealthState {
        val response = retrofitService.getUserData(id).last()
        Log.d("ServerApi", "getHealthState($id)")
        return HealthState(
            height = response.height.toFloat(),
            weight = response.weight.toFloat(),
            gender = when (response.gender) {
                "M" -> Gender.Male
                "F" -> Gender.Female
                else -> Gender.entries.random()
            },
            age = response.age,
            adultDisease = when (response.disease) {
                "diabetes" -> AdultDisease.Diabetes
                "highbloodpressure" -> AdultDisease.HighBloodPressure
                else -> null
            }
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun getNutritionState(id: Long): NutritionState {
        val recommend = retrofitService.getNutritionRecommend(id).toMap()
        val response = retrofitService.getConsumeList(id, LocalDate.now().toString()).drop(1)
        Log.d("ServerApi", "getNutritionState($id)")
        if (response.isEmpty())
            return NutritionState(
                Nutrition.entries.associateWith { IntakeState(recommend[it]!!) }
            )
        val remain = response.maxBy { it.id }.toRemainingMap()
        Log.d("ServerApi", "remain: $remain")
        Log.d("ServerApi", "recommend: $recommend")
        return NutritionState(
            Nutrition.entries.associateWith { IntakeState(recommend[it]!!, recommend[it]!! - remain[it]!!) }
        )
    }

    suspend fun postProductList(id: Long, productList: List<Pair<Product, Int>>) {
        retrofitService.postProductList(id, productList.map { (product, amount) ->
            product.toServerNutritionFormat(amount)
        })
        Log.d("ServerApi", "postProductList($id, productList)")
    }

    suspend fun postHealthState(id: Long?, token: String, healthState: HealthState): Long {
        val response = retrofitService.postUserData(token, ServerUserStateFormat(
            id = id,
            name = "babbogi_app",
            height = healthState.height.toDouble(),
            weight = healthState.weight.toDouble(),
            age = healthState.age,
            gender = when (healthState.gender) {
                Gender.Male -> "M"
                Gender.Female -> "F"
            },
            disease = when (healthState.adultDisease) {
                null -> "null"
                AdultDisease.Diabetes -> "diabetes"
                AdultDisease.HighBloodPressure -> "highbloodpressure"
            }
        ))
        Log.d("ServerApi", "postHealthState($id, $token, healthState)")
        return response.toLong()
    }

    suspend fun putNutritionRecommend(id: Long, nutrition: Map<Nutrition, Float>) {
        Log.d("ServerApi", nutrition.toServerNutritionFormat(id).toString())
        retrofitService.putNutritionRecommend(id, nutrition.toServerNutritionFormat(id))
        Log.d("ServerApi", "putNutritionRecommend($id, $nutrition)")
    }
}

