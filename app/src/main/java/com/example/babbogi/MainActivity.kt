package com.example.babbogi

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.babbogi.ui.CameraViewScreen
import com.example.babbogi.ui.FoodListScreen
import com.example.babbogi.ui.GuidePageScreen
import com.example.babbogi.ui.HealthProfileScreen
import com.example.babbogi.ui.HomeScreen
import com.example.babbogi.ui.LoadingScreen
import com.example.babbogi.ui.NutritionOverviewScreen
import com.example.babbogi.ui.model.BabbogiViewModel
import com.example.babbogi.ui.model.DataPreference
import com.example.babbogi.ui.theme.BabbogiTheme

enum class Screen {
    Tutorial,
    Loading,
    Home,
    NutritionOverview,
    Camera,
    FoodList,
    HealthProfile,
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

    NavHost(
        navController = navController,
        startDestination = Screen.Home.name
    ) {
        composable(route = Screen.Tutorial.name) { GuidePageScreen(viewModel, navController) }
        composable(route = Screen.Loading.name) { LoadingScreen(viewModel, navController) }
        composable(route = Screen.Home.name) { HomeScreen(viewModel, navController) }
        composable(route = Screen.NutritionOverview.name) { NutritionOverviewScreen(viewModel, navController) }
        composable(route = Screen.Camera.name) { CameraViewScreen(viewModel, navController) }
        composable(route = Screen.FoodList.name) { FoodListScreen(viewModel, navController) }
        composable(route = Screen.HealthProfile.name) { HealthProfileScreen(viewModel, navController) }
    }
}