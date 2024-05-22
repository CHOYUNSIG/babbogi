package com.example.babbogi.util

import kotlin.random.Random


val nutritionName = listOf(
    "calorie",
    "carbohydrate",
    "protein",
    "fat",
    "sugar",
    "salt",
    "cholesterol",
    "saturated_fatty_acids",
    "trans_fat",
)

val nutritionNameKorean = mapOf(
    "calorie" to "열량",
    "carbohydrate" to "탄수화물",
    "protein" to "단백질",
    "fat" to "지방",
    "sugar" to "당",
    "salt" to "나트륨",
    "cholesterol" to "콜레스테롤",
    "saturated_fatty_acids" to "포화지방산",
    "trans_fat" to "트랜스지방",
)

val nutritionUnit = mapOf (
    "calorie" to "kcal",
    "carbohydrate" to "g",
    "protein" to "g",
    "fat" to "g",
    "sugar" to "g",
    "salt" to "mg",
    "cholesterol" to "mg",
    "saturated_fatty_acids" to "g",
    "trans_fat" to "g",
)


data class NutritionState(
    val recommended: Float,
    val ingested: Float,
) {
    fun getPercentage(): Float = ingested / recommended
}

val PersonalNutritionState = Array(9) {
    nutritionName[it] to NutritionState(100f, Random.nextFloat() * 100)
}.toMap()