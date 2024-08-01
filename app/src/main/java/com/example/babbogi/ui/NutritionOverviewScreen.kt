package com.example.babbogi.ui

import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.EaseOutCubic
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.babbogi.Screen
import com.example.babbogi.model.BabbogiViewModel
import com.example.babbogi.ui.view.ColumnScreen
import com.example.babbogi.ui.view.FloatingContainer
import com.example.babbogi.ui.view.NutritionCircularGraph
import com.example.babbogi.ui.view.ScreenPreviewer
import com.example.babbogi.util.Nutrition
import com.example.babbogi.util.NutritionIntake
import com.example.babbogi.util.NutritionRecommendation
import com.example.babbogi.util.getRandomNutritionIntake
import com.example.babbogi.util.testNutritionRecommendation
import com.example.babbogi.util.toNutritionIntake
import kotlin.math.abs
import kotlin.math.roundToInt

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NutritionOverviewScreen(
    viewModel: BabbogiViewModel,
    navController: NavController,
    showSnackBar: (message: String) -> Unit,
    showAlertPopup: (title: String, message: String, icon: Int) -> Unit,
) {
    var intake by remember { mutableStateOf<NutritionIntake?>(null) }

    LaunchedEffect(true) {
        viewModel.getFoodLists(viewModel.today) { foodList ->
            if (foodList != null) intake = foodList[viewModel.today]?.toNutritionIntake()
        }
    }

    NutritionOverview(
        recommendation = viewModel.nutritionRecommendation,
        intake = intake
    )
}

@Composable
private fun CircularGraphCard(
    nutrition: Nutrition,
    recommendation: Float,
    intake: Float,
) {
    val targetValue = remember { (intake / recommendation * 100).roundToInt() }
    var animatedValue by remember { mutableIntStateOf(0) }

    LaunchedEffect(targetValue) {
        animate(
            initialValue = 0f,
            targetValue = targetValue.toFloat(),
            animationSpec = tween(durationMillis = 2000, easing = EaseOutCubic)
        ) { value, _ ->
            animatedValue = value.toInt()
        }
    }

    FloatingContainer {
        Row(verticalAlignment = Alignment.CenterVertically) {
            NutritionCircularGraph(
                recommendation = recommendation,
                intake = intake,
            )
            Column(
                verticalArrangement = Arrangement.spacedBy(5.dp, alignment = Alignment.CenterVertically),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = stringResource(id = nutrition.res),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                    Text(text = "적정량보다", fontSize = 12.sp)
                    Text(text = "${"%.1f".format(abs(recommendation - intake))}${nutrition.unit}")
                    Text(text = if (recommendation < intake) "많습니다." else "적습니다", fontSize = 12.sp)
                }
                Text(
                    text = "${"%.1f".format(intake)}${nutrition.unit} / ${"%.1f".format(recommendation)}${nutrition.unit}",
                    fontSize = 12.sp,
                )
            }
        }
    }
}

@Composable
private fun NutritionOverview(
    recommendation: NutritionRecommendation,
    intake: NutritionIntake?,
) {
    ColumnScreen {
        if (intake != null) Nutrition.entries.forEach { nutrition ->
            CircularGraphCard(
                nutrition = nutrition,
                recommendation = recommendation[nutrition]!!,
                intake = intake[nutrition]!!,
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(uiMode = UI_MODE_NIGHT_NO)
@Preview(uiMode = UI_MODE_NIGHT_YES)
@Composable
fun PreviewNutritionOverview() {
    ScreenPreviewer(screen = Screen.NutritionOverview) {
        NutritionOverview(
            recommendation = testNutritionRecommendation,
            intake = getRandomNutritionIntake(),
        )
    }
}