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
import com.example.babbogi.ui.CameraScreen
import com.example.babbogi.ui.FoodListScreen
import com.example.babbogi.ui.MainScreen
import com.example.babbogi.ui.NutritionOverviewScreen
import com.example.babbogi.ui.model.BabbogiViewModel
import com.example.babbogi.ui.theme.BabbogiTheme

enum class BabbogiScreen {
    Home,
    NutritionOverview,
    Camera,
    FoodList,
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
        startDestination = BabbogiScreen.Home.name
    ) {
        composable(route = BabbogiScreen.Home.name) {
            MainScreen(viewModel, navController)
        }
        composable(route = BabbogiScreen.NutritionOverview.name) {
            NutritionOverviewScreen(viewModel, navController)
        }
        composable(route = BabbogiScreen.Camera.name) {
            CameraScreen(viewModel, navController)
        }
        composable(route = BabbogiScreen.FoodList.name) {
            FoodListScreen(viewModel, navController)
        }
    }
}