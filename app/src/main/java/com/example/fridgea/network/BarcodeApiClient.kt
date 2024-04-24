package com.example.fridgea.network

import com.example.fridgea.BuildConfig
import com.example.fridgea.network.response.BarcodeApiResponse
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Path

private const val BASE_URL = "https://openapi.foodsafetykorea.go.kr/api/${BuildConfig.BARCODE_API_KEY}/C005/json/1/5/"
private val json = Json { ignoreUnknownKeys = true }
private val retrofit = Retrofit.Builder()
    .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
    .baseUrl(BASE_URL)
    .build()


interface BarcodeApiService {
    @GET("BAR_CD={code}")
    suspend fun getProducts(@Path(value = "code") barcode: String): BarcodeApiResponse
}


object BarcodeApi {
    private val retrofitService : BarcodeApiService by lazy {
        retrofit.create(BarcodeApiService::class.java)
    }

    suspend fun getProducts(barcode: String) = retrofitService.getProducts(barcode).C005
}
