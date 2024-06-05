package com.example.babbogi.ui.model

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.example.babbogi.util.HealthState
import com.example.babbogi.util.NutritionState
import com.google.gson.Gson

object DataPreference {
    private lateinit var sharedPreferences: SharedPreferences
    private val gson = Gson()

    fun init(context: Context) {
        sharedPreferences = context.getSharedPreferences("babbogi", Context.MODE_PRIVATE)
    }

    fun completeTutorial() = sharedPreferences.edit().putBoolean("tutorial", true).apply()
    fun isTutorialComplete() = sharedPreferences.getBoolean("tutorial", false)

    fun saveToken(token: String) {
        sharedPreferences.edit().putString("token", token).apply()
        Log.d("DataPreference", "token is saved.\nToken: $token")
    }

    fun saveID(id: Long) {
        sharedPreferences.edit().putLong("ID", id).apply()
        Log.d("DataPreference", "ID is saved.\nID: $id")
    }

    fun saveHealthState(healthState: HealthState) {
        sharedPreferences.edit().putString("health_state", gson.toJson(healthState)).apply()
        Log.d("DataPreference", "Health State is saved.")
    }

    fun saveNutritionState(nutritionState: NutritionState) {
        sharedPreferences.edit().putString("nutrition_state", gson.toJson(nutritionState)).apply()
        Log.d("DataPreference", "Nutrition State is saved.")
    }

    fun getToken(): String? {
        return sharedPreferences.getString("token", null)
    }

    fun getID(): Long? {
        val id = sharedPreferences.getLong("ID", -1)
        return if (id < 0) null else id
    }

    fun getHealthState(): HealthState? {
        val healthState = sharedPreferences.getString("health_state", null)
        return if (healthState == null) null else gson.fromJson(healthState, HealthState::class.java)
    }

    fun getNutritionState(): NutritionState? {
        val nutritionState = sharedPreferences.getString("nutrition_state", null)
        return if (nutritionState == null) null else gson.fromJson(nutritionState, NutritionState::class.java)
    }
}