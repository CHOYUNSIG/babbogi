package com.example.babbogi.util

import java.lang.Math.random

typealias NutritionMap<T> = Map<Nutrition, T>
typealias NutritionRecommendation = NutritionMap<Float>
typealias NutritionIntake = NutritionMap<Float>


// 테스트용 데이터
fun getRandomNutritionIntake(): NutritionIntake = Nutrition.entries.associateWith { (it.defaultRecommend * random()).toFloat() }

val testNutritionRecommendation: NutritionRecommendation = Nutrition.entries.associateWith { it.defaultRecommend }
val testNutritionIntake: NutritionIntake = getRandomNutritionIntake()

