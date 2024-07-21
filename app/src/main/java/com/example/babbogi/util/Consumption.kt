package com.example.babbogi.util

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDateTime
import kotlin.random.Random

data class Consumption (
    val id: Long,
    val product: Product,
    val amount: Int,
    val time: LocalDateTime,
)

fun List<Consumption>.toNutritionIntake(): NutritionIntake = Nutrition.entries.associateWith { nutrition ->
    this.sumOf {
        ((it.product.nutrition?.get(nutrition) ?: 0f) * it.amount).toDouble()
    }.toFloat()
}


// 테스트용 데이터
@RequiresApi(Build.VERSION_CODES.O)
fun getRandomTestConsumption() = Consumption(
    id = Random.nextLong(),
    product = getRandomTestProduct(),
    amount = Random.nextInt(1, 10),
    time = LocalDateTime.now(),
)

@RequiresApi(Build.VERSION_CODES.O)
val testConsumptionList = List(10) { getRandomTestConsumption() }