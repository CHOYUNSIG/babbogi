package com.example.babbogi.util

import kotlinx.serialization.Serializable
import kotlin.random.Random


// 상품 정보
@Serializable
data class Product(
    val name: String,
    val nutrition: NutritionMap<Float>?,
    val servingSize: Float,
)


// 테스트용 제품 데이터
val testProduct1 = Product(
    "아카페라카라멜마끼아또 2024 리뉴얼에디션",
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
    ),
    100f,
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
    ),
    100f,
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
    ),
    100f,
)

val testProductNull = Product(
    "코카콜라 제로",
    null,
    10f,
)

fun getRandomTestProduct(includeNull: Boolean = false) = listOf(testProduct1, testProduct2, testProduct3)
    .plus(if (includeNull) listOf(testProductNull) else emptyList())
    .random()

val testProductPairList = List(10) {
    getRandomTestProduct() to Random.nextFloat() * 2
}

val testProductTripleList = List(10) {
    Triple(getRandomTestProduct(), Random.nextFloat() * 2, Random.nextBoolean())
}
