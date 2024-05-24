package com.example.babbogi.util

import kotlin.random.Random
import kotlin.random.nextUInt

// 상품 정보
data class Product(
    val name: String,
    val barcode: String,
    // 영양 정보
    val nutrition: ProductNutritionInfo?
)

// 상품의 영양 정보
data class ProductNutritionInfo(
    val calorie: Float = 0f,
    val carbohydrate: Float = 0f,
    val protein: Float = 0f,
    val fat: Float = 0f,
    val sugar: Float = 0f,
    val salt: Float = 0f,
    val cholesterol: Float = 0f,
    val saturatedFattyAcids: Float = 0f,
    val transFat: Float = 0f,
) {
    fun toMap(): Map<String, Float> {
        return mapOf(
            "calorie" to calorie,
            "carbohydrate" to carbohydrate,
            "protein" to protein,
            "fat" to fat,
            "sugar" to sugar,
            "salt" to salt,
            "cholesterol" to cholesterol,
            "saturated_fatty_acids" to saturatedFattyAcids,
            "trans_fat" to transFat,
        )
    }

    operator fun get(index: Int): Float = this.toMap()[nutrition[index]] ?: 0f
    override fun toString(): String = nutrition.joinToString("\n") { "${it}: ${this.toMap()[it]!!}${nutritionUnit[it]!!}" }
}

fun List<String>.toProductNutritionInfo(pre: ProductNutritionInfo = ProductNutritionInfo()) = this.mapIndexed { i, v -> v.toFloat2(pre[i])}.toProductNutritionInfo()
fun List<Float>.toProductNutritionInfo() = ProductNutritionInfo(this[0], this[1], this[2], this[3], this[4], this[5], this[6], this[7], this[8])


// 테스트용 제품 데이터
val testProduct = Product(
    "아카페라카라멜마끼아또",
    "8801104212403",
    ProductNutritionInfo(140f, 23f, 3.5f, 4f, 21f, 120f, 15f, 3f, 0f)
)

val testProductList: List<Pair<Product, Int>> = emptyList<Pair<Product, Int>>().toMutableList().also {
    repeat(5) { _ -> it.add(testProduct to Random.nextUInt().mod(5u).toInt() + 1) }
}.toList()