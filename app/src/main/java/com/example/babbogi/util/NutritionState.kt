package com.example.babbogi.util

import kotlin.random.Random


// 개인 영양 정보
data class NutritionState(
    val map: Map<Nutrition, IntakeState> = Array(Nutrition.entries.size) {
        Nutrition.entries[it] to IntakeState(Nutrition.entries[it].defaultRecommend)
    }.toMap()
) {
    operator fun get(nutrition: Nutrition) = map[nutrition]!!
    operator fun get(index: Int) = map[Nutrition.entries[index]]!!
}

// 영양소 별 정보
data class IntakeState(
    val recommended: Float,
    val ingested: Float = 0.0f,
) {
    fun getRatio(): Float {
        val result = ingested / recommended
        return if (result.isNaN()) 0.0f else result
    }
}


// 테스트용 데이터
val testNutritionState = NutritionState(
    Array(Nutrition.entries.size) {
        val nutrition = Nutrition.entries[it]
        val recommended = nutrition.defaultRecommend
        nutrition to IntakeState(recommended, Random.nextFloat() * recommended * 1.5f)
    }.toMap()
)