package com.example.babbogi.util


enum class AdultDisease {
    Diabetes,
    HighBloodPressure,
}

enum class Gender {
    Male,
    Female,
}

data class HealthState (
    val height: Float?,
    val weight: Float?,
    val gender: Gender?,
    val adultDisease: List<AdultDisease> = emptyList(),
)