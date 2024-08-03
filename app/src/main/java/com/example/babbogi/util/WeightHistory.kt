package com.example.babbogi.util

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDateTime
import kotlin.random.Random

data class WeightHistory(
    val id: Long,
    val weight: Float,
    val date: LocalDateTime,
)


// 테스트용 데이터
@RequiresApi(Build.VERSION_CODES.O)
val testWeightHistoryList = List(50) {
    WeightHistory(
        id = Random.nextLong(),
        weight = Random.nextFloat() * 30 + 50,
        date = LocalDateTime.now().minusDays(Random.nextLong(1000)),
    )
}