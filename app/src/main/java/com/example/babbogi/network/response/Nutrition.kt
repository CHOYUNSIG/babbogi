package com.example.babbogi.network.response

import kotlinx.serialization.Serializable

@Serializable
data class NutritionApiResponse(
    val I2790: NutritionInfo
)

@Serializable
data class NutritionInfo(
    val total_count: Int,
    val row: List<Nutrition>? = null
)

@Serializable
data class Nutrition(
    val NUM: Int,

    val NUTR_CONT1: String,
    val NUTR_CONT2: String,
    val NUTR_CONT3: String,
    val NUTR_CONT4: String,
    val NUTR_CONT5: String,
    val NUTR_CONT6: String,
    val NUTR_CONT7: String,
    val NUTR_CONT8: String,
    val NUTR_CONT9: String,

    val SUB_REF_NAME: String,
    val RESEARCH_YEAR: String,
    val MAKER_NAME: String,
    val GROUP_NAME: String,
    val SERVING_SIZE: String,
    val SERVING_UNIT: String,
    val SAMPLING_REGION_NAME: String,
    val SAMPLING_MONTH_CD: String,
    val SAMPLING_MONTH_NAME: String,
    val DESC_KOR: String,
    val SAMPLING_REGION_CD: String,
    val FOOD_CD: String
)