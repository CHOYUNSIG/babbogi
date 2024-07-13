package com.example.babbogi.util

import com.example.babbogi.R


enum class Nutrition(
    val res: Int,
    val unit: String,
    val defaultRecommend: Float,
) {
    Calorie(
        res = R.string.calorie,
        unit = "kcal",
        defaultRecommend = 2200f,
    ),
    Carbohydrate(
        res = R.string.carbohydrate,
        unit = "g",
        defaultRecommend = 100f,
    ),
    Protein(
        res = R.string.protein,
        unit = "g",
        defaultRecommend = 50f,
    ),
    Fat (
        res = R.string.fat,
        unit = "g",
        defaultRecommend = 70f,
    ),
    Sugar (
        res = R.string.sugar,
        unit = "g",
        defaultRecommend = 25f,
    ),
    Salt (
        res = R.string.salt,
        unit = "mg",
        defaultRecommend = 2.2f,
    ),
    Cholesterol (
        res = R.string.cholesterol,
        unit = "mg",
        defaultRecommend = 300f,
    ),
    SaturatedFat (
        res = R.string.saturated_fat,
        unit = "g",
        defaultRecommend = 15f,
    ),
    TransFat (
        res = R.string.trans_fat,
        unit = "g",
        defaultRecommend = 2.2f,
    ),
}

typealias NutritionMap<T> = Map<Nutrition, T>
