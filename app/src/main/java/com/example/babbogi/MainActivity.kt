package com.example.babbogi

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.babbogi.ui.CameraViewScreen
import com.example.babbogi.ui.FoodListScreen
import com.example.babbogi.ui.HealthProfileScreen
import com.example.babbogi.ui.HomeScreen
import com.example.babbogi.ui.NutritionOverviewScreen
import com.example.babbogi.ui.model.BabbogiViewModel
import com.example.babbogi.ui.theme.BabbogiTheme

enum class Screen {
    Home,
    NutritionOverview,
    Camera,
    FoodList,
    HealthProfile,
}

class MainActivity : ComponentActivity() {
    private val viewModel: BabbogiViewModel by viewModels()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            BabbogiTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
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
        composable(route = Screen.Home.name) { HomeScreen(viewModel, navController) }
        composable(route = Screen.NutritionOverview.name) { NutritionOverviewScreen(viewModel, navController) }
        composable(route = Screen.Camera.name) { CameraViewScreen(viewModel, navController) }
        composable(route = Screen.FoodList.name) { FoodListScreen(viewModel, navController) }
        composable(route = Screen.HealthProfile.name) { HealthProfileScreen(viewModel, navController) }
    }
}