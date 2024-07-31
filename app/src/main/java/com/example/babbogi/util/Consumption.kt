package com.example.babbogi.util

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDateTime
import kotlin.random.Random

data class Consumption (
    val id: Long,
    val product: Product,
    val intakeRatio: Float,
    val time: LocalDateTime?,
) {
    val weight: Float = product.servingSize * intakeRatio
    val nutritionIntake: NutritionIntake = product.nutrition!!.mapValues { it.value * intakeRatio }
}

fun List<Consumption>.toNutritionIntake(): NutritionIntake = Nutrition.entries.associateWith { nutrition ->
    this.sumOf {
        it.product.nutrition?.get(nutrition)?.toDouble() ?: 0.0
    }.toFloat()
}


// 테스트용 데이터
@RequiresApi(Build.VERSION_CODES.O)
fun getRandomTestConsumption() = Consumption(
    id = Random.nextLong(),
    product = getRandomTestProduct(),
    intakeRatio = Random.nextFloat() * 2,
    time = LocalDateTime.now(),
)

@RequiresApi(Build.VERSION_CODES.O)
val testConsumptionList = List(10) { getRandomTestConsumption() }