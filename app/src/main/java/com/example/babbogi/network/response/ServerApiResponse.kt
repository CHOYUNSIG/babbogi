package com.example.babbogi.network.response

import com.example.babbogi.util.AdultDisease
import com.example.babbogi.util.Gender
import com.example.babbogi.util.HealthState
import com.example.babbogi.util.Nutrition
import com.example.babbogi.util.NutritionMap
import com.example.babbogi.util.Product
import com.example.babbogi.util.SearchResult
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
) {
    fun toHealthState(): HealthState = HealthState(
        height = this.height.toFloat(),
        weight = this.weight.toFloat(),
        gender = when (this.gender) {
            "M" -> Gender.Male
            "F" -> Gender.Female
            else -> Gender.entries.random()
        },
        age = this.age,
        adultDisease = AdultDisease.entries.map {
            it.name.lowercase()
        }.mapIndexed { index, s ->
            s to AdultDisease.entries[index]
        }.toMap()[this.disease]
    )
}

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
) {
    fun toMap(): NutritionMap<Float> = mapOf(
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

@Serializable
data class ServerConsumeFormat(
    val id: Long,
    val userId: Long,
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
    val remainingkcal: Double,
    val remainingCarbohydrate: Double,
    val remainingSugar: Double,
    val remainingProtein: Double,
    val remainingFat: Double,
    val remainingTransfat: Double,
    val remainingSaturatedfat: Double,
    val remainingCholesterol: Double,
    val remainingNatrium: Double,
    val date: String,
) {
    fun toMap(): NutritionMap<Float> = mapOf(
        Nutrition.Fat to this.fat!!.toFloat(),
        Nutrition.Salt to this.natrium!!.toFloat(),
        Nutrition.Sugar to this.sugar!!.toFloat(),
        Nutrition.Calorie to this.kcal!!.toFloat(),
        Nutrition.Protein to this.protein!!.toFloat(),
        Nutrition.TransFat to this.transfat!!.toFloat(),
        Nutrition.Cholesterol to this.cholesterol!!.toFloat(),
        Nutrition.Carbohydrate to this.carbohydrate!!.toFloat(),
        Nutrition.SaturatedFat to this.saturatedfat!!.toFloat(),
    )

    fun toProduct(): Product = Product(
        name = this.foodName!!,
        this.toMap()
    )
}

@Serializable
data class ServerSearchResultFormat(
    val foodname: String,
    val foodgroup: String,
    val food: String,
    val company: String,
) {
    fun toSearchResult(): SearchResult = SearchResult(
        name = foodname,
        mainCategory = foodgroup,
        subCategory = food,
        company = if (company.startsWith("해당없음")) null else company,
    )
}

@Serializable
data class ServerFoodFormat(
    val foodnum: Int,
    val foodcode: String,
    val foodname: String,
    val foodgroup: String,
    val food: String,
    val nutrientcontentper100: String,
    val kcal: Double,
    val protein: Double,
    val fat: Double,
    val carbohydrate: Double,
    val sugar: Double,
    val natrium: Double,
    val cholesterol: Double,
    val saturatedfat: Double,
    val transfat: Double,
) {
    fun toMap(): NutritionMap<Float> = mapOf(
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

    fun toProduct(): Product = Product(
        name = this.foodname,
        nutrition = this.toMap()
    )
}

fun HealthState.toServerUserStateFormat(id: Long?): ServerUserStateFormat {
    return ServerUserStateFormat(
        id = id,
        name = "babbogi_app",
        height = this.height.toDouble(),
        weight = this.weight.toDouble(),
        age = this.age,
        gender = when (this.gender) {
            Gender.Male -> "M"
            Gender.Female -> "F"
        },
        disease = this.adultDisease?.name?.lowercase() ?: "null"
    )
}

fun NutritionMap<Float>.toServerNutritionFormat(
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