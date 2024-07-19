package com.example.babbogi.network

import android.util.Log
import com.example.babbogi.BuildConfig
import com.example.babbogi.network.response.BarcodeApiResponse
import com.example.babbogi.util.Product
import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

private val retrofit = Retrofit.Builder()
    .addConverterFactory(GsonConverterFactory.create(GsonBuilder().setLenient().create()))
    .baseUrl("https://openapi.foodsafetykorea.go.kr/api/${BuildConfig.BARCODE_API_KEY}/C005/json/1/5/")
    .build()


interface BarcodeApiService {
    @GET("BAR_CD={code}")
    suspend fun getProducts(@Path(value = "code") barcode: String): BarcodeApiResponse
}


object BarcodeApi {
    private val retrofitService : BarcodeApiService by lazy {
        retrofit.create(BarcodeApiService::class.java)
    }

    suspend fun getProducts(barcode: String): List<Product> {
        val response = retrofitService.getProducts(barcode).C005.row
        Log.d("BarcodeApi", "$response")
        return response?.map { row -> row.toProduct() } ?: emptyList()
    }
}
