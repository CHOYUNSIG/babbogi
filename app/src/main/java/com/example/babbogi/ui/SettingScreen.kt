package com.example.babbogi.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.babbogi.ui.view.ColumnWithDefault
import com.example.babbogi.ui.view.HealthAbstraction
import com.example.babbogi.ui.view.NutritionRecommendationAbstraction
import com.example.babbogi.ui.view.TitleBar
import com.example.babbogi.util.HealthState
import com.example.babbogi.util.NutritionState
import com.example.babbogi.util.testHealthState
import com.example.babbogi.util.testNutritionState

@Composable
fun SettingScreen() {
    
}

@Composable
fun Setting(
    healthState: HealthState,
    nutritionState: NutritionState,
    onHealthCardClicked: () -> Unit,
    onRecommendationCardClicked: () -> Unit,
) {
    Column {
        TitleBar(title = "설정")
        ColumnWithDefault {
            HealthAbstraction(
                healthState = healthState,
                onClick = onHealthCardClicked,
            )
            NutritionRecommendationAbstraction(
                nutritionState = nutritionState,
                onClick = onRecommendationCardClicked
            )
        }
    }
}

@Preview
@Composable
fun PreviewSetting() {
    Scaffold {
        Box(modifier = Modifier.padding(it)) {
            Setting(
                healthState = testHealthState,
                nutritionState = testNutritionState,
                onHealthCardClicked = {},
                onRecommendationCardClicked = {},
            )
        }
    }
}