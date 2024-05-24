package com.example.babbogi.util

import kotlin.random.Random


data class NutritionState(
    val recommended: Float,
    val ingested: Float,
) {
    fun getPercentage(): Float = ingested / recommended
}



// 테스트용 데이터
val PersonalNutritionState = Array(9) {
    nutrition[it] to NutritionState(100f, Random.nextFloat() * 100)
}.toMap()