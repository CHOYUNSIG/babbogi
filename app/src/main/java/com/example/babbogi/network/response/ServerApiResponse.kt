package com.example.babbogi.network.response

import com.example.babbogi.util.Nutrition
import com.example.babbogi.util.Product
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
    val id: Long? = null,
    val foodName: String? = null,
    val foodCount: Int? = null,
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

fun ServerNutritionFormat.toMap(): Map<Nutrition, Float> {
    return mapOf(
        Nutrition.Fat to this.fat.toFloat(),
        Nutrition.Salt to this.natrium.toFloat(),
        Nutrition.Sugar to this.sugar.toFloat(),
        Nutrition.Calorie to this.kcal.toFloat(),
        Nutrition.Protein to this.protein.toFloat(),
        Nutrition.TransFat to this.transfat.toFloat(),
        Nutrition.Cholesterol to this.cholesterol.toFloat(),
        Nutrition.Carbohydrate to this.carbohydrate.toFloat(),
        Nutrition.SaturatedFat to this.saturatedfat.toFloat(),
    )
}

fun ServerConsumeFormat.toRemainingMap(): Map<Nutrition, Float> {
    return mapOf(
        Nutrition.Fat to this.remainingFat.toFloat(),
        Nutrition.Salt to this.remainingNatrium.toFloat(),
        Nutrition.Sugar to this.remainingSugar.toFloat(),
        Nutrition.Calorie to this.remainingkcal.toFloat(),
        Nutrition.Protein to this.remainingProtein.toFloat(),
        Nutrition.TransFat to this.remainingTransfat.toFloat(),
        Nutrition.Cholesterol to this.remainingCholesterol.toFloat(),
        Nutrition.Carbohydrate to this.remainingCarbohydrate.toFloat(),
        Nutrition.SaturatedFat to this.remainingSaturatedfat.toFloat(),
    )
}

fun Map<Nutrition, Float>.toServerNutritionFormat(
    id: Long? = null,
): ServerNutritionFormat {
    return ServerNutritionFormat(
        id = id,
        foodName = null,
        foodCount = null,
        kcal = this[Nutrition.Calorie]!!.toDouble(),
        carbohydrate = this[Nutrition.Carbohydrate]!!.toDouble(),
        sugar = this[Nutrition.Sugar]!!.toDouble(),
        protein = this[Nutrition.Protein]!!.toDouble(),
        fat = this[Nutrition.Fat]!!.toDouble(),
        saturatedfat = this[Nutrition.SaturatedFat]!!.toDouble(),
        transfat = this[Nutrition.TransFat]!!.toDouble(),
        natrium = this[Nutrition.Salt]!!.toDouble(),
        cholesterol = this[Nutrition.Cholesterol]!!.toDouble(),
    )
}

fun Product.toServerNutritionFormat(amount: Int): ServerNutritionFormat {
    return ServerNutritionFormat(
        foodName = this.name,
        foodCount = amount,
        fat = this.nutrition?.get(Nutrition.Fat)?.toDouble() ?: 0.0,
        kcal = this.nutrition?.get(Nutrition.Calorie)?.toDouble() ?: 0.0,
        sugar = this.nutrition?.get(Nutrition.Sugar)?.toDouble() ?: 0.0,
        natrium = this.nutrition?.get(Nutrition.Salt)?.toDouble() ?: 0.0,
        protein = this.nutrition?.get(Nutrition.Protein)?.toDouble() ?: 0.0,
        transfat = this.nutrition?.get(Nutrition.TransFat)?.toDouble() ?: 0.0,
        cholesterol = this.nutrition?.get(Nutrition.Cholesterol)?.toDouble() ?: 0.0,
        carbohydrate = this.nutrition?.get(Nutrition.Carbohydrate)?.toDouble() ?: 0.0,
        saturatedfat = this.nutrition?.get(Nutrition.SaturatedFat)?.toDouble() ?: 0.0,
    )
}