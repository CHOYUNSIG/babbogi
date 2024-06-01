package com.example.babbogi.network

import com.example.babbogi.BuildConfig
import com.example.babbogi.network.response.NutritionApiResponse
import com.example.babbogi.util.Nutrition
import com.example.babbogi.util.ProductNutritionInfo
import com.example.babbogi.util.toFloat2
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

private val retrofit = Retrofit.Builder()
    .addConverterFactory(GsonConverterFactory.create())
    .baseUrl("https://openapi.foodsafetykorea.go.kr/api/${BuildConfig.NUTRITION_API_KEY}/I2790/json/1/5/")
    .build()


interface NutritionApiService {
    @GET("DESC_KOR={name}")
    suspend fun getNutrition(@Path(value = "name") prodName: String): NutritionApiResponse
}


object NutritionApi {
    private val retrofitService : NutritionApiService by lazy {
        retrofit.create(NutritionApiService::class.java)
    }

    suspend fun getNutrition(prodName: String): List<ProductNutritionInfo> {
        val response = retrofitService.getNutrition(prodName.replace(" ", "_")).I2790
        return if (response.row == null)
            emptyList()
        else
            response.row.map { row ->
                ProductNutritionInfo(
                    mapOf(
                        Nutrition.Calorie to row.NUTR_CONT1.toFloat2(),
                        Nutrition.Carbohydrate to row.NUTR_CONT2.toFloat2(),
                        Nutrition.Protein to row.NUTR_CONT3.toFloat2(),
                        Nutrition.Fat to row.NUTR_CONT4.toFloat2(),
                        Nutrition.Sugar to row.NUTR_CONT5.toFloat2(),
                        Nutrition.Salt to row.NUTR_CONT6.toFloat2(),
                        Nutrition.Cholesterol to row.NUTR_CONT7.toFloat2(),
                        Nutrition.SaturatedFattyAcids to row.NUTR_CONT8.toFloat2(),
                        Nutrition.TransFat to row.NUTR_CONT9.toFloat2(),
                    )
                )
            }
    }
}
