package com.example.babbogi.util

import com.example.babbogi.R

enum class NutritionRecommendationType {
    Normal,
    UpperLimit,
    LowerLimit,
}

enum class Nutrition(
    val res: Int,
    val unit: String,
    val defaultRecommendation: Float,
    val recommendationType: NutritionRecommendationType,
) {
    Calorie(
        res = R.string.calorie,
        unit = "kcal",
        defaultRecommendation = 2200f,
        recommendationType = NutritionRecommendationType.Normal,
    ),
    Carbohydrate(
        res = R.string.carbohydrate,
        unit = "g",
        defaultRecommendation = 100f,
        recommendationType = NutritionRecommendationType.Normal,
    ),
    Protein(
        res = R.string.protein,
        unit = "g",
        defaultRecommendation = 50f,
        recommendationType = NutritionRecommendationType.Normal,
    ),
    Fat (
        res = R.string.fat,
        unit = "g",
        defaultRecommendation = 70f,
        recommendationType = NutritionRecommendationType.Normal,
    ),
    Sugar (
        res = R.string.sugar,
        unit = "g",
        defaultRecommendation = 25f,
        recommendationType = NutritionRecommendationType.UpperLimit,
    ),
    Salt (
        res = R.string.salt,
        unit = "mg",
        defaultRecommendation = 2.2f,
        recommendationType = NutritionRecommendationType.UpperLimit,
    ),
    Cholesterol (
        res = R.string.cholesterol,
        unit = "mg",
        defaultRecommendation = 300f,
        recommendationType = NutritionRecommendationType.UpperLimit,
    ),
    SaturatedFat (
        res = R.string.saturated_fat,
        unit = "g",
        defaultRecommendation = 15f,
        recommendationType = NutritionRecommendationType.UpperLimit,
    ),
    TransFat (
        res = R.string.trans_fat,
        unit = "g",
        defaultRecommendation = 2.2f,
        recommendationType = NutritionRecommendationType.UpperLimit,
    ),
}
