package com.example.babbogi.util

import kotlinx.serialization.Serializable


enum class AdultDisease {
    Diabetes { override fun toString() = "당뇨" },
    HighBloodPressure { override fun toString() = "고혈압" },
}

enum class Gender {
    Male { override fun toString() = "남자" },
    Female { override fun toString() = "여자" },
}

@Serializable
data class HealthState (
    val height: Float,
    val weight: Float,
    val age: Int,
    val gender: Gender,
    val adultDisease: AdultDisease?,
)


// 테스트용 건강 상태
val testHealthState = HealthState(
    170f,
    60f,
    30,
    Gender.Male,
    AdultDisease.HighBloodPressure
)