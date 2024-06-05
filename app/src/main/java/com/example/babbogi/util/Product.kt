package com.example.babbogi.util

import kotlin.random.Random
import kotlin.random.nextUInt


// 상품 정보
data class Product(
    val name: String,
    val barcode: String,
    val nutrition: ProductNutritionInfo?
)

// 상품의 영양 정보
data class ProductNutritionInfo(
    val map: Map<Nutrition, Float> = Array(Nutrition.entries.size) {
        Nutrition.entries[it] to 0f
    }.toMap()
) {
    operator fun get(nutrition: Nutrition): Float = map[nutrition]!!
    operator fun get(index: Int): Float = map[Nutrition.entries[index]]!!

    fun toString(nutritionName: List<String>) = Array(Nutrition.entries.size) {
        "${nutritionName[it]}: ${map[Nutrition.entries[it]]}${Nutrition.entries[it].unit}"
    }.joinToString("\n")
}

fun List<String>.toProductNutritionInfo(pre: ProductNutritionInfo = ProductNutritionInfo()) =
    this.mapIndexed { i, v -> v.toFloat2(pre[i])}.toProductNutritionInfo()

fun List<Float>.toProductNutritionInfo() = ProductNutritionInfo(
    Array(Nutrition.entries.size) { Nutrition.entries[it] to this[it] }.toMap()
)



// 테스트용 제품 데이터
val testProduct = Product(
    "아카페라카라멜마끼아또",
    "8801104212403",
    ProductNutritionInfo(
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
)

val testProductList: List<Pair<Product, Int>> = emptyList<Pair<Product, Int>>().toMutableList().also {
    repeat(10) { _ -> it.add(testProduct to Random.nextUInt().mod(5u).toInt() + 1) }
}.toList()