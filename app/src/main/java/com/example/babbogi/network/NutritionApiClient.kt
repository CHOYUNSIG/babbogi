package com.example.babbogi.network

import com.example.babbogi.BuildConfig
import com.example.babbogi.network.response.NutritionApiResponse
import com.example.babbogi.network.response.NutritionInfo
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Path

private const val BASE_URL = "https://openapi.foodsafetykorea.go.kr/api/${BuildConfig.NUTRITION_API_KEY}/I2790/json/1/5/"
private val json = Json { ignoreUnknownKeys = true }
private val retrofit = Retrofit.Builder()
    .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
    .baseUrl(BASE_URL)
    .build()


interface NutritionApiService {
    @GET("DESC_KOR={name}")
    suspend fun getNutrition(@Path(value = "name") prodName: String): NutritionApiResponse
}


object NutritionApi {
    private val retrofitService : NutritionApiService by lazy {
        retrofit.create(NutritionApiService::class.java)
    }

    suspend fun getNutrition(prodName: String): NutritionInfo {
        return retrofitService.getNutrition(prodName.replace(" ", "_")).I2790
    }
}
