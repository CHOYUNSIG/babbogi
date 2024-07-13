package com.example.babbogi

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.babbogi.model.BabbogiViewModel
import com.example.babbogi.model.DataPreference
import com.example.babbogi.ui.CameraViewScreen
import com.example.babbogi.ui.FoodListScreen
import com.example.babbogi.ui.GuidePageScreen
import com.example.babbogi.ui.HealthProfileScreen
import com.example.babbogi.ui.HomeScreen
import com.example.babbogi.ui.LoadingScreen
import com.example.babbogi.ui.NutritionDailyAnalyzeScreen
import com.example.babbogi.ui.NutritionOverviewScreen
import com.example.babbogi.ui.NutritionPeriodAnalyze
import com.example.babbogi.ui.NutritionPeriodAnalyzeScreen
import com.example.babbogi.ui.theme.BabbogiTheme
import com.example.babbogi.ui.view.CustomNavigationBar

enum class Screen {
    Tutorial,
    Loading,
    Home,
    NutritionDailyAnalyze,
    NutritionPeriodAnalyze,
    NutritionOverview,
    Camera,
    FoodList,
    HealthProfile,
    Setting,
}

class MainActivity : ComponentActivity() {
    private lateinit var viewModel: BabbogiViewModel

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        DataPreference.init(this)
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
    // 전역 내비게이션 컨트롤러
    val navController = rememberNavController()
    val navScreens = listOf(
        Screen.NutritionDailyAnalyze,
        Screen.FoodList,
        Screen.NutritionPeriodAnalyze
    )

    Scaffold(
        bottomBar = {
            if (navScreens.map { it.name }.contains(navController.currentDestination?.route))
            CustomNavigationBar(
                navController = navController,
                screens = navScreens,
                labels = listOf("일일 분석", "추가", "기간 분석"),
                icons = listOf(R.drawable.baseline_list_24, R.drawable.ic_add_box_24, R.drawable.baseline_list_24)
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
            composable(route = Screen.Home.name) { HomeScreen(viewModel, navController) }
            composable(route = Screen.NutritionDailyAnalyze.name) { NutritionDailyAnalyzeScreen(viewModel, navController) }
            composable(route = Screen.NutritionPeriodAnalyze.name) { NutritionPeriodAnalyzeScreen(viewModel, navController) }
            composable(route = Screen.NutritionOverview.name) { NutritionOverviewScreen(viewModel, navController) }
            composable(route = Screen.Camera.name) { CameraViewScreen(viewModel, navController) }
            composable(route = Screen.FoodList.name) { FoodListScreen(viewModel, navController) }
            composable(route = Screen.HealthProfile.name) { HealthProfileScreen(viewModel, navController) }
        }
    }
}