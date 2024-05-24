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
    val map: Map<String, Float> = Array(nutrition.size) {
        nutrition[it] to 0f
    }.toMap()
) {
    operator fun get(nutrition: String): Float = map[nutrition]?: 0f
    operator fun get(index: Int): Float = map[nutrition[index]]?: 0f
    override fun toString(): String = nutrition.joinToString("\n") { "${it}: ${map[it]!!}${nutritionUnit[it]!!}" }
}

fun List<String>.toProductNutritionInfo(pre: ProductNutritionInfo = ProductNutritionInfo()) =
    this.mapIndexed { i, v -> v.toFloat2(pre[i])}.toProductNutritionInfo()

fun List<Float>.toProductNutritionInfo() = ProductNutritionInfo(
    Array(nutrition.size) {
        nutrition[it] to this[it]
    }.toMap()
)


// 테스트용 제품 데이터
val testProduct = Product(
    "아카페라카라멜마끼아또",
    "8801104212403",
    listOf(140f, 23f, 3.5f, 4f, 21f, 120f, 15f, 3f, 0f).toProductNutritionInfo()
)

val testProductList: List<Pair<Product, Int>> = emptyList<Pair<Product, Int>>().toMutableList().also {
    repeat(5) { _ -> it.add(testProduct to Random.nextUInt().mod(5u).toInt() + 1) }
}.toList()