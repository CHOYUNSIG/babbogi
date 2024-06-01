package com.example.babbogi.network.response

import kotlinx.serialization.Serializable

@Serializable
data class ServerUserStateFormat (
    val key: Long? = null,
    val id: Long? = null,
    val name: String,
    val height: Double,
    val weight: Double,
    val age: Int,
    val gender: String,
    val disease: String,
    val date: String? = null,
)

@Serializable
data class ServerNutritionFormat(
    val id: Long,
    val kcal: Double,
    val carbohydrate: Double,
    val sugar: Double,
    val protein: Double,
    val fat: Double,
    val saturatedfat: Double,
    val transfat: Double,
    val natrium: Double,
    val cholesterol: Double,
)

@Serializable
data class ServerProductFormat(
    val foodName: String,
    val foodCount: Int,
    val carbohydrate: Double,
    val sugar: Double,
    val protein: Double,
    val fat: Double,
    val saturatedfat: Double,
    val transfat: Double,
    val natrium: Double,
    val cholesterol: Double,
    val kcal: Double,
)

@Serializable
data class ServerConsumeFormat(
    val id: Long,  // 필요 없음
    val userId: Long,  // 필요 없음
    val foodName: String?,
    val foodCount: Int,
    val kcal: Double?,
    val carbohydrate: Double?,
    val sugar: Double?,
    val protein: Double?,
    val fat: Double?,
    val transfat: Double?,
    val saturatedfat: Double?,
    val cholesterol: Double?,
    val natrium: Double?,
    val remainingkcal: Double,  // 각 시점마다 계산된 수치
    val remainingCarbohydrate: Double,  // 각 시점마다 계산된 수치
    val remainingSugar: Double,  // 각 시점마다 계산된 수치
    val remainingProtein: Double,  // 각 시점마다 계산된 수치
    val remainingFat: Double,  // 각 시점마다 계산된 수치
    val remainingTransfat: Double,  // 각 시점마다 계산된 수치
    val remainingSaturatedfat: Double,  // 각 시점마다 계산된 수치
    val remainingCholesterol: Double,  // 각 시점마다 계산된 수치
    val remainingNatrium: Double,  // 각 시점마다 계산된 수치
    val date: String,
)