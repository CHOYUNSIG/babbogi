package com.example.babbogi

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.babbogi.model.BabbogiViewModel
import com.example.babbogi.model.BabbogiModel
import com.example.babbogi.ui.CameraViewScreen
import com.example.babbogi.ui.FoodListScreen
import com.example.babbogi.ui.FoodSearchScreen
import com.example.babbogi.ui.GuidePageScreen
import com.example.babbogi.ui.HealthProfileScreen
import com.example.babbogi.ui.LoadingScreen
import com.example.babbogi.ui.NutritionDailyAnalyzeScreen
import com.example.babbogi.ui.NutritionOverviewScreen
import com.example.babbogi.ui.NutritionPeriodAnalyzeScreen
import com.example.babbogi.ui.SettingScreen
import com.example.babbogi.ui.theme.BabbogiTheme
import com.example.babbogi.ui.view.CustomNavigationBar

enum class Screen {
    Tutorial,
    Loading,
    NutritionDailyAnalyze,
    NutritionPeriodAnalyze,
    NutritionOverview,
    FoodList,
    Camera,
    FoodSearch,
    Setting,
    HealthProfile,
}

class MainActivity : ComponentActivity() {
    private lateinit var viewModel: BabbogiViewModel

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        BabbogiModel.init(this)
        viewModel = BabbogiViewModel()

        setContent {
            BabbogiTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.White,
                ) {
                    MainApp(viewModel)
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MainApp(viewModel: BabbogiViewModel) {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            CustomNavigationBar(
                navController = navController,
                screens = listOf(
                    Screen.NutritionDailyAnalyze,
                    Screen.FoodList,
                    Screen.NutritionPeriodAnalyze,
                ),
                labels = listOf(
                    "일일 분석",
                    "음식 추가",
                    "기간 분석"
                ),
                icons = listOf(
                    R.drawable.baseline_today_24,
                    R.drawable.baseline_add_box_24,
                    R.drawable.baseline_bar_chart_24,
                )
            )
        },
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.NutritionDailyAnalyze.name,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(route = Screen.Tutorial.name) { GuidePageScreen(viewModel, navController) }
            composable(route = Screen.Loading.name) { LoadingScreen(viewModel, navController) }
            composable(route = Screen.NutritionDailyAnalyze.name) { NutritionDailyAnalyzeScreen(viewModel, navController) }
            composable(route = Screen.NutritionPeriodAnalyze.name) { NutritionPeriodAnalyzeScreen(viewModel, navController) }
            composable(route = Screen.NutritionOverview.name) { NutritionOverviewScreen(viewModel, navController) }
            composable(route = Screen.FoodList.name) { FoodListScreen(viewModel, navController) }
            composable(route = Screen.FoodSearch.name) { FoodSearchScreen(viewModel, navController) }
            composable(route = Screen.Camera.name) { CameraViewScreen(viewModel, navController) }
            composable(route = Screen.HealthProfile.name) { HealthProfileScreen(viewModel, navController) }
            composable(route = Screen.Setting.name) { SettingScreen(viewModel, navController) }
        }
    }
}