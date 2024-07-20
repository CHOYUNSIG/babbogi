package com.example.babbogi.util

import android.os.Build
import androidx.annotation.RequiresApi
import kotlinx.serialization.Serializable
import java.time.LocalDateTime
import kotlin.random.Random
import kotlin.random.nextUInt


// 상품 정보
@Serializable
data class Product(
    val name: String,
    val nutrition: NutritionMap<Float>?
)

fun List<Triple<Product, LocalDateTime, Int>>.toNutritionIntake(): NutritionIntake = Nutrition.entries.associateWith { nutrition ->
    this.sumOf {
        ((it.first.nutrition?.get(nutrition) ?: 0f) * it.third).toDouble()
    }.toFloat()
}


// 테스트용 제품 데이터
val testProduct1 = Product(
    "아카페라카라멜마끼아또",
    mapOf(
        Nutrition.Calorie to 140f,
        Nutrition.Carbohydrate to 23f,
        Nutrition.Protein to 3.5f,
        Nutrition.Fat to 4f,
        Nutrition.Sugar to 21f,
        Nutrition.Salt to 120f,
        Nutrition.Cholesterol to 15f,
        Nutrition.SaturatedFat to 3f,
        Nutrition.TransFat to 0f,
    )
)

val testProduct2 = Product(
    "진라면(매운맛)",
    mapOf(
        Nutrition.Calorie to 500f,
        Nutrition.Carbohydrate to 80f,
        Nutrition.Protein to 11f,
        Nutrition.Fat to 15f,
        Nutrition.Sugar to 4.6f,
        Nutrition.Salt to 120f,
        Nutrition.Cholesterol to 15f,
        Nutrition.SaturatedFat to 3f,
        Nutrition.TransFat to 0f,
    )
)

val testProduct3 = Product(
    "포카칩 오리지널",
    mapOf(
        Nutrition.Calorie to 377f,
        Nutrition.Carbohydrate to 0f,
        Nutrition.Protein to 0f,
        Nutrition.Fat to 0f,
        Nutrition.Sugar to 0f,
        Nutrition.Salt to 280f,
        Nutrition.Cholesterol to 0f,
        Nutrition.SaturatedFat to 8f,
        Nutrition.TransFat to 0f,
    )
)

val testProductNull = Product(
    "코카콜라 제로",
    null
)

fun getRandomTestProduct(includeNull: Boolean = false) = listOf(testProduct1, testProduct2, testProduct3)
    .plus(if (includeNull) listOf(testProductNull) else emptyList())
    .random()

val testProductPairList: List<Pair<Product, Int>> = List(10) {
    getRandomTestProduct() to Random.nextUInt().mod(5u).toInt() + 1
}

@RequiresApi(Build.VERSION_CODES.O)
val testProductTripleList: List<Triple<Product, LocalDateTime, Int>> = List(10) {
    Triple(getRandomTestProduct(), LocalDateTime.now(), Random.nextUInt().mod(5u).toInt())
}