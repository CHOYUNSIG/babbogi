package com.example.babbogi.model

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.example.babbogi.util.HealthState
import com.example.babbogi.util.NutritionMap
import com.example.babbogi.util.Product
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

object BabbogiModel {
    private lateinit var sharedPreferences: SharedPreferences

    fun init(context: Context) {
        sharedPreferences = context.getSharedPreferences("babbogi", Context.MODE_PRIVATE)
    }

    var isTutorialDone: Boolean
        get() = sharedPreferences.getBoolean("tutorial", false)
        set(isTutorialDone) {
            sharedPreferences.edit().putBoolean("tutorial", isTutorialDone).apply()
        }

    var notificationActivation: Boolean
        get() = sharedPreferences.getBoolean("notification_activation", false)
        set(notificationActivation) {
            sharedPreferences.edit().putBoolean("notification_activation", notificationActivation).apply()
        }

    var token: String?
        get() = sharedPreferences.getString("token", null)
        set(token) {
            sharedPreferences.edit().putString("token", token).apply()
            Log.d("DataPreference", "Token is saved.\nToken: $token")
        }

    var id: Long?
        get() = sharedPreferences.getLong("ID", -1).let { if (it >= 0) it else null }
        set(id) {
            if (id == null) return
            sharedPreferences.edit().putLong("ID", id).apply()
            Log.d("DataPreference", "ID is saved.\nID: $id")
        }

    var healthState: HealthState?
        get() = sharedPreferences.getString("health_state", null).let { if (it != null) Json.decodeFromString(it) else null }
        set(healthState) {
            if (healthState == null) return
            sharedPreferences.edit().putString("health_state", Json.encodeToString(healthState)).apply()
            Log.d("DataPreference", "Health State is saved.")
        }

    var nutritionRecommendation: NutritionMap<Float>?
        get() = sharedPreferences.getString("nutrition_recommendation", null).let { if (it != null) Json.decodeFromString(it) else null }
        set(nutritionRecommendation) {
            if (nutritionRecommendation == null) return
            sharedPreferences.edit().putString("nutrition_recommendation", Json.encodeToString(nutritionRecommendation)).apply()
            Log.d("DataPreference", "Nutrition Recommendation is saved.")
        }

    var productList: List<Pair<Product, Int>>
        get() = sharedPreferences.getString("food_list", null).let { if (it != null) Json.decodeFromString<List<Pair<Product, Int>>>(it) else emptyList() }
        set(productList) {
            sharedPreferences.edit().putString("food_list", Json.encodeToString(productList)).apply()
            Log.d("DataPreference", "Food List is saved.")
        }
}