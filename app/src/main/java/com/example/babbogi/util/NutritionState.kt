package com.example.babbogi.util

import kotlin.random.Random


// 개인 영양 정보
data class NutritionState(
    val map: Map<String, IntakeState> = Array(nutrition.size) {
        nutrition[it] to IntakeState(nutritionRecommended[nutrition[0]]!!)
    }.toMap()
) {
    operator fun get(nutrition: String) = map[nutrition]?: IntakeState(0f)
    operator fun get(index: Int) = map[nutrition[index]]?: IntakeState(0f)
}

// 영양소 별 정보
data class IntakeState(
    val recommended: Float,
    val ingested: Float = 0.0f,
) {
    fun getPercentage() = ingested / recommended
}

fun List<Float>.toNutritionState(pre: NutritionState = NutritionState()) =
    NutritionState(
        Array(nutrition.size) {
            nutrition[it] to IntakeState(pre[it].recommended, this[it])
        }.toMap()
    )


// 테스트용 데이터
val testNutritionState = NutritionState(
    Array(nutrition.size) {
        val nutName = nutrition[it]
        val recommended = nutritionRecommended[nutName]!!
        nutName to IntakeState(recommended, Random.nextFloat() * recommended)
    }.toMap()
)