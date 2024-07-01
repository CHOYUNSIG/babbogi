package com.example.babbogi.network

import android.util.Log
import com.example.babbogi.BuildConfig
import com.example.babbogi.network.response.NutritionApiResponse
import com.example.babbogi.network.response.toProductNutritionInfo
import com.example.babbogi.util.ProductNutritionInfo
import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

private val retrofit = Retrofit.Builder()
    .addConverterFactory(GsonConverterFactory.create(GsonBuilder().setLenient().create()))
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
        val response = retrofitService.getNutrition(prodName.replace(" ", "_")).I2790.row
        Log.d("NutritionApi", "$response")
        return response?.map { row -> row.toProductNutritionInfo() } ?: emptyList()
    }
}
