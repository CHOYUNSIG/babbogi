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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.babbogi.model.BabbogiModel
import com.example.babbogi.model.BabbogiViewModel
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

enum class Screen(
    val screenComposable: @Composable (BabbogiViewModel, NavHostController) -> Unit
) {
    Tutorial(screenComposable = { v, n -> GuidePageScreen(v, n) }),
    Loading(screenComposable = { v, n -> LoadingScreen(v, n) }),
    NutritionDailyAnalyze(screenComposable = { v, n -> NutritionDailyAnalyzeScreen(v, n) }),
    NutritionPeriodAnalyze(screenComposable = { v, n -> NutritionPeriodAnalyzeScreen(v, n) }),
    NutritionOverview(screenComposable = { v, n -> NutritionOverviewScreen(v, n) }),
    FoodList(screenComposable = { v, n -> FoodListScreen(v, n) }),
    Camera(screenComposable = { v, n -> CameraViewScreen(v, n) }),
    FoodSearch(screenComposable = { v, n -> FoodSearchScreen(v, n) }),
    Setting(screenComposable = { v, n -> SettingScreen(v, n) }),
    HealthProfile(screenComposable = { v, n -> HealthProfileScreen(v, n) }),
}

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        BabbogiModel.init(this)
        val viewModel = BabbogiViewModel()

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
    var currentScreen by remember { mutableStateOf<String?>(null) }

    navController.addOnDestinationChangedListener { _, destination, _ ->
        currentScreen = destination.route
    }

    Scaffold(
        bottomBar = {
            if (currentScreen != Screen.Tutorial.name) CustomNavigationBar(
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
            Screen.entries.forEach { screen ->
                composable(screen.name) {
                    screen.screenComposable(viewModel, navController)
                }
            }
        }
    }
}