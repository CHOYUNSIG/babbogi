package com.example.babbogi.util

import kotlin.random.Random
import kotlin.random.nextUInt

// 상품 정보
data class ProductInfo(
    // 등록 상품 정보
    val name: String,
    val barcode: String,
    // 영양 정보
    val nutrition: ProductNutritionInfo?
)

// 상품의 영양 정보
data class ProductNutritionInfo(
    val servingVolume: Float,
    val servingUnit: Float,
    val calorie: Float,
    val carbohydrate: Float,
    val protein: Float,
    val fat: Float,
    val sugar: Float,
    val salt: Float,
    val cholesterol: Float,
    val saturated_fatty_acids: Float,
    val trans_fat: Float,
) {
    override fun toString(): String = arrayOf(
        "Calorie: ${calorie}kcal",
        "Carbohydrate: ${carbohydrate}g",
        "Protein: ${protein}g",
        "Fat: ${fat}g",
        "Sugar: ${sugar}g",
        "Salt: ${salt}mg",
        "Cholesterol: ${cholesterol}mg",
        "Saturated Fatty Acids: ${saturated_fatty_acids}g",
        "Trans Fat: ${trans_fat}g"
    ).joinToString("\n")
}

// 테스트 제품
val testProduct = ProductInfo(
    "아카페라카라멜마끼아또",
    "8801104212403",
    ProductNutritionInfo(240f, 180f, 140f, 23f, 3.5f, 4f, 21f, 120f, 15f, 3f, 0f)
)

val testProductList: List<Pair<ProductInfo, Int>> = emptyList<Pair<ProductInfo, Int>>().toMutableList().also {
    repeat(5) { _ -> it.add(testProduct to Random.nextUInt().mod(5u).toInt() + 1) }
}.toList()