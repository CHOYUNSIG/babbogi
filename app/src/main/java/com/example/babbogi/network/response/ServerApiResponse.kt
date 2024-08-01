package com.example.babbogi.network.response

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.babbogi.util.AdultDisease
import com.example.babbogi.util.Consumption
import com.example.babbogi.util.Gender
import com.example.babbogi.util.HealthState
import com.example.babbogi.util.Nutrition
import com.example.babbogi.util.NutritionMap
import com.example.babbogi.util.Product
import com.example.babbogi.util.SearchResult
import kotlinx.serialization.Serializable
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Serializable
sealed class ServerUserFormat {
    abstract val name: String?
    abstract val height: Double?
    abstract val weight: Double?
    abstract val age: Int?
    abstract val gender: String?
    abstract val disease: String?

    fun toHealthState(): HealthState = HealthState(
        height = this.height!!.toFloat(),
        weight = this.weight!!.toFloat(),
        gender = when (this.gender) {
            "M" -> Gender.Male
            "F" -> Gender.Female
            else -> Gender.entries.random()
        },
        age = this.age!!,
        adultDisease = AdultDisease.entries.map {
            it.name.lowercase()
        }.mapIndexed { index, s ->
            s to AdultDisease.entries[index]
        }.toMap()[this.disease]
    )
}

@Serializable
sealed class ServerNutritionFormat {
    abstract val kcal: Double?
    abstract val carbohydrate: Double?
    abstract val sugar: Double?
    abstract val protein: Double?
    abstract val fat: Double?
    abstract val transfat: Double?
    abstract val saturatedfat: Double?
    abstract val cholesterol: Double?
    abstract val natrium: Double?

    fun toNutritionMap(): NutritionMap<Float> = mapOf(
        Nutrition.Fat to fat!!.toFloat(),
        Nutrition.Salt to natrium!!.toFloat(),
        Nutrition.Sugar to sugar!!.toFloat(),
        Nutrition.Calorie to kcal!!.toFloat(),
        Nutrition.Protein to protein!!.toFloat(),
        Nutrition.TransFat to transfat!!.toFloat(),
        Nutrition.Cholesterol to cholesterol!!.toFloat(),
        Nutrition.Carbohydrate to carbohydrate!!.toFloat(),
        Nutrition.SaturatedFat to saturatedfat!!.toFloat(),
    )
}

@Serializable
data class ServerHealthPostFormat(
    val id: Long? = null,
    override val name: String,
    override val height: Double,
    override val weight: Double,
    override val age: Int,
    override val gender: String,
    override val disease: String,
): ServerUserFormat() {
    companion object {
        fun fromHealthState(id: Long?, healthState: HealthState): ServerHealthPostFormat {
            return ServerHealthPostFormat(
                id = id,
                name = "babbogi_session",
                height = healthState.height.toDouble(),
                weight = healthState.weight.toDouble(),
                age = healthState.age,
                gender = healthState.gender.name.substring(0, 1),
                disease = healthState.adultDisease?.name?.lowercase() ?: "null"
            )
        }
    }
}

@Serializable
data class ServerHealthGetFormat(
    val id: Long? = null,
    val date: String? = null,
    override val name: String? = null,
    override val height: Double? = null,
    override val weight: Double? = null,
    override val age: Int? = null,
    override val gender: String? = null,
    override val disease: String? = null,
): ServerUserFormat()

@Serializable
data class ServerConsumptionPostFormat(
    val foodCount: Double,
    val foodName: String,
    val weight: Double,
    override val kcal: Double,
    override val carbohydrate: Double,
    override val sugar: Double,
    override val protein: Double,
    override val fat: Double,
    override val transfat: Double,
    override val saturatedfat: Double,
    override val cholesterol: Double,
    override val natrium: Double,
): ServerNutritionFormat() {
    companion object {
        fun fromProduct(product: Product, intakeRatio: Float): ServerConsumptionPostFormat {
            val nutrition = product.nutrition!!
            return ServerConsumptionPostFormat(
                foodCount =intakeRatio.toDouble(),
                foodName = product.name,
                weight = product.servingSize.toDouble(),
                kcal = nutrition[Nutrition.Calorie]!!.toDouble(),
                carbohydrate = nutrition[Nutrition.Carbohydrate]!!.toDouble(),
                sugar = nutrition[Nutrition.Sugar]!!.toDouble(),
                protein = nutrition[Nutrition.Protein]!!.toDouble(),
                fat = nutrition[Nutrition.Fat]!!.toDouble(),
                transfat = nutrition[Nutrition.TransFat]!!.toDouble(),
                saturatedfat = nutrition[Nutrition.SaturatedFat]!!.toDouble(),
                cholesterol = nutrition[Nutrition.Cholesterol]!!.toDouble(),
                natrium = nutrition[Nutrition.Salt]!!.toDouble(),
            )
        }
    }
}

@Serializable
data class ServerConsumptionGetFormat(
    val id: Long? = null,
    val foodCount: Double? = null,
    val foodName: String? = null,
    val weight: Double? = null,
    val date: String? = null,
    override val kcal: Double? = null,
    override val carbohydrate: Double? = null,
    override val sugar: Double? = null,
    override val protein: Double? = null,
    override val fat: Double? = null,
    override val transfat: Double? = null,
    override val saturatedfat: Double? = null,
    override val cholesterol: Double? = null,
    override val natrium: Double? = null,
): ServerNutritionFormat() {
    @RequiresApi(Build.VERSION_CODES.O)
    fun toConsumption(): Consumption = Consumption(
        id = id!!,
        product = toProduct(),
        intakeRatio = foodCount!!.toFloat(),
        time = if (date!!.endsWith("00:00:00")) null
        else LocalDateTime.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
    )

    fun toProduct(): Product = Product(
        name = foodName!!,
        nutrition = toNutritionMap(),
        servingSize = weight!!.toFloat(),
    )
}

@Serializable
data class ServerNutritionRecommendationGetFormat(
    override val kcal: Double? = null,
    override val carbohydrate: Double? = null,
    override val sugar: Double? = null,
    override val protein: Double? = null,
    override val fat: Double? = null,
    override val transfat: Double? = null,
    override val saturatedfat: Double? = null,
    override val cholesterol: Double? = null,
    override val natrium: Double? = null,
): ServerNutritionFormat()

@Serializable
data class ServerNutritionRecommendationPutFormat(
    override val kcal: Double,
    override val carbohydrate: Double,
    override val sugar: Double,
    override val protein: Double,
    override val fat: Double,
    override val transfat: Double,
    override val saturatedfat: Double,
    override val cholesterol: Double,
    override val natrium: Double,
): ServerNutritionFormat() {
    companion object {
        fun fromNutritionMap(nutrition: NutritionMap<Float>): ServerNutritionRecommendationPutFormat {
            return ServerNutritionRecommendationPutFormat(
                kcal = nutrition[Nutrition.Calorie]!!.toDouble(),
                carbohydrate = nutrition[Nutrition.Carbohydrate]!!.toDouble(),
                sugar = nutrition[Nutrition.Sugar]!!.toDouble(),
                protein = nutrition[Nutrition.Protein]!!.toDouble(),
                fat = nutrition[Nutrition.Fat]!!.toDouble(),
                transfat = nutrition[Nutrition.TransFat]!!.toDouble(),
                saturatedfat = nutrition[Nutrition.SaturatedFat]!!.toDouble(),
                cholesterol = nutrition[Nutrition.Cholesterol]!!.toDouble(),
                natrium = nutrition[Nutrition.Salt]!!.toDouble(),
            )
        }
    }
}

@Serializable
data class ServerSearchResultGetFormat(
    val foodcode: String? = null,
    val foodname: String? = null,
    val foodgroup: String? = null,
    val food: String? = null,
    val company: String? = null,
) {
    fun toSearchResult(): SearchResult = SearchResult(
        id = foodcode!!,
        name = foodname!!,
        mainCategory = foodgroup!!,
        subCategory = food!!,
        company = if (company!!.startsWith("해당 없음")) null else company,
    )
}

@Serializable
data class ServerMatchedFoodGetFormat(
    val foodcode: String? = null,
    val foodname: String? = null,
    val foodgroup: String? = null,
    val food: String? = null,
    val foodnum: Int? = null,
    val company: String? = null,
    val foodweight: Double? = null,
    val nutrientcontentper100: Double? = null,
    override val kcal: Double? = null,
    override val protein: Double? = null,
    override val fat: Double? = null,
    override val carbohydrate: Double? = null,
    override val sugar: Double? = null,
    override val natrium: Double? = null,
    override val cholesterol: Double? = null,
    override val saturatedfat: Double? = null,
    override val transfat: Double? = null,
): ServerNutritionFormat() {
    fun toProduct(): Product = Product(
        name = foodname!!,
        nutrition = toNutritionMap(),
        servingSize = foodweight!!.toFloat(),
    )
}