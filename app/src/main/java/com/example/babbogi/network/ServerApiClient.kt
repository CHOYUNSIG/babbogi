package com.example.babbogi.network

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.babbogi.BuildConfig
import com.example.babbogi.network.response.ServerConsumeFormat
import com.example.babbogi.network.response.ServerNutritionFormat
import com.example.babbogi.network.response.ServerProductFormat
import com.example.babbogi.network.response.ServerUserStateFormat
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
        @Body body: List<ServerProductFormat>
    ): List<ServerConsumeFormat>

    @POST("User")
    suspend fun postUserData(
        @Query(value = "token") token: String,
        @Body body: ServerUserStateFormat
    ): String
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
                        Nutrition.SaturatedFattyAcids to it.saturatedfat!!.toFloat(),
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
            adultDisease = if (response.disease == "null") null else AdultDisease.valueOf(response.disease)
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun getNutritionState(id: Long): NutritionState {
        val recommend = retrofitService.getNutritionRecommend(id)
        val response = retrofitService.getConsumeList(id, LocalDate.now().toString()).drop(1)
        Log.d("ServerApi", "getNutritionState($id)")
        if (response.isEmpty())
            return NutritionState(
                mapOf(
                    Nutrition.Fat to IntakeState(recommend.fat.toFloat()),
                    Nutrition.Salt to IntakeState(recommend.natrium.toFloat()),
                    Nutrition.Sugar to IntakeState(recommend.sugar.toFloat()),
                    Nutrition.Calorie to IntakeState(recommend.kcal.toFloat()),
                    Nutrition.Protein to IntakeState(recommend.protein.toFloat()),
                    Nutrition.TransFat to IntakeState(recommend.transfat.toFloat()),
                    Nutrition.Cholesterol to IntakeState(recommend.cholesterol.toFloat()),
                    Nutrition.Carbohydrate to IntakeState(recommend.carbohydrate.toFloat()),
                    Nutrition.SaturatedFattyAcids to IntakeState(recommend.saturatedfat.toFloat()),
                )
            )
        val remain = response.maxBy { it.id }
        Log.d("debug", remain.toString())
        return NutritionState(
            mapOf(
                Nutrition.Fat to IntakeState(recommend.fat.toFloat(), recommend.fat.toFloat() - remain.remainingFat.toFloat()),
                Nutrition.Salt to IntakeState(recommend.natrium.toFloat(), recommend.natrium.toFloat() - remain.remainingNatrium.toFloat()),
                Nutrition.Sugar to IntakeState(recommend.sugar.toFloat(), recommend.sugar.toFloat() - remain.remainingSugar.toFloat()),
                Nutrition.Calorie to IntakeState(recommend.kcal.toFloat(), recommend.kcal.toFloat() - remain.remainingkcal.toFloat()),
                Nutrition.Protein to IntakeState(recommend.protein.toFloat(), recommend.protein.toFloat() - remain.remainingProtein.toFloat()),
                Nutrition.TransFat to IntakeState(recommend.transfat.toFloat(), recommend.transfat.toFloat() - remain.remainingTransfat.toFloat()),
                Nutrition.Cholesterol to IntakeState(recommend.cholesterol.toFloat(), recommend.cholesterol.toFloat() - remain.remainingCholesterol.toFloat()),
                Nutrition.Carbohydrate to IntakeState(recommend.carbohydrate.toFloat(), recommend.carbohydrate.toFloat() - remain.remainingCarbohydrate.toFloat()),
                Nutrition.SaturatedFattyAcids to IntakeState(recommend.saturatedfat.toFloat(), recommend.saturatedfat.toFloat() - remain.remainingSaturatedfat.toFloat()),
            )
        )
    }

    suspend fun postProductList(id: Long, productList: List<Pair<Product, Int>>) {
        retrofitService.postProductList(id, productList.map { (product, amount) ->
            ServerProductFormat(
                foodName = product.name,
                foodCount = amount,
                fat = product.nutrition?.get(Nutrition.Fat)?.toDouble() ?: 0.0,
                kcal = product.nutrition?.get(Nutrition.Cholesterol)?.toDouble() ?: 0.0,
                sugar = product.nutrition?.get(Nutrition.Sugar)?.toDouble() ?: 0.0,
                natrium = product.nutrition?.get(Nutrition.Salt)?.toDouble() ?: 0.0,
                protein = product.nutrition?.get(Nutrition.Protein)?.toDouble() ?: 0.0,
                transfat = product.nutrition?.get(Nutrition.TransFat)?.toDouble() ?: 0.0,
                cholesterol = product.nutrition?.get(Nutrition.Cholesterol)?.toDouble() ?: 0.0,
                carbohydrate = product.nutrition?.get(Nutrition.Carbohydrate)?.toDouble() ?: 0.0,
                saturatedfat = product.nutrition?.get(Nutrition.SaturatedFattyAcids)?.toDouble() ?: 0.0,
            )
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
            disease = healthState.adultDisease?.name ?: "null"
        ))
        Log.d("ServerApi", "postHealthState($id, $token, healthState)")
        return response.toLong()
    }
}

