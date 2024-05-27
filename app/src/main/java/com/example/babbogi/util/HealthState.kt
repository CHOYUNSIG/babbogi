package com.example.babbogi.util


enum class AdultDisease {
    Diabetes { override fun toString() = "당뇨" },
    HighBloodPressure { override fun toString() = "고혈압" },
}

enum class Gender {
    Male { override fun toString() = "남자" },
    Female { override fun toString() = "여자" },
}

data class HealthState (
    val height: Float,
    val weight: Float,
    val gender: Gender,
    val adultDisease: AdultDisease?,
)


// 테스트용 건강 상태
val testHealthState = HealthState(
    170f,
    60f,
    Gender.Male,
    AdultDisease.HighBloodPressure
)