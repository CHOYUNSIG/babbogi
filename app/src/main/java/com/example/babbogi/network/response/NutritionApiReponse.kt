package com.example.babbogi.network.response

import com.example.babbogi.util.Nutrition
import com.example.babbogi.util.ProductNutritionInfo
import com.example.babbogi.util.toFloat2
import kotlinx.serialization.Serializable

@Serializable
data class NutritionApiResponse(
    val I2790: I2790
)

@Serializable
data class I2790(
    val total_count: Int,
    val row: List<I2790_row>? = null
)

@Serializable
data class I2790_row(
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

fun I2790_row.toProductNutritionInfo(): ProductNutritionInfo = ProductNutritionInfo(
    mapOf(
        Nutrition.Calorie to NUTR_CONT1.toFloat2(),
        Nutrition.Carbohydrate to NUTR_CONT2.toFloat2(),
        Nutrition.Protein to NUTR_CONT3.toFloat2(),
        Nutrition.Fat to NUTR_CONT4.toFloat2(),
        Nutrition.Sugar to NUTR_CONT5.toFloat2(),
        Nutrition.Salt to NUTR_CONT6.toFloat2(),
        Nutrition.Cholesterol to NUTR_CONT7.toFloat2(),
        Nutrition.SaturatedFat to NUTR_CONT8.toFloat2(),
        Nutrition.TransFat to NUTR_CONT9.toFloat2(),
    )
)
