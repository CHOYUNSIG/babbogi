package com.example.babbogi

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
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
    val screenComposable: @Composable (BabbogiViewModel, NavHostController, SnackbarHostState) -> Unit
) {
    @RequiresApi(Build.VERSION_CODES.O)
    Tutorial(screenComposable = { v, n, s -> GuidePageScreen(v, n, s) }),
    Loading(screenComposable = { v, n, s -> LoadingScreen(v, n, s) }),
    @RequiresApi(Build.VERSION_CODES.O)
    NutritionDailyAnalyze(screenComposable = { v, n, s -> NutritionDailyAnalyzeScreen(v, n, s) }),
    @RequiresApi(Build.VERSION_CODES.O)
    NutritionPeriodAnalyze(screenComposable = { v, n, s -> NutritionPeriodAnalyzeScreen(v, n, s) }),
    @RequiresApi(Build.VERSION_CODES.O)
    NutritionOverview(screenComposable = { v, n, s -> NutritionOverviewScreen(v, n, s) }),
    @RequiresApi(Build.VERSION_CODES.O)
    FoodList(screenComposable = { v, n, s -> FoodListScreen(v, n, s) }),
    Camera(screenComposable = { v, n, s -> CameraViewScreen(v, n, s) }),
    FoodSearch(screenComposable = { v, n, s -> FoodSearchScreen(v, n, s) }),
    @RequiresApi(Build.VERSION_CODES.O)
    Setting(screenComposable = { v, n, s -> SettingScreen(v, n, s) }),
    @RequiresApi(Build.VERSION_CODES.O)
    HealthProfile(screenComposable = { v, n, s -> HealthProfileScreen(v, n, s) }),
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
    val snackBarHostState = remember { SnackbarHostState() }
    val navController = rememberNavController()

    var currentScreen by remember { mutableStateOf<String?>(null) }
    navController.addOnDestinationChangedListener { _, destination, _ ->
        currentScreen = destination.route
    }

    Scaffold(
        bottomBar = {
            if (
                !listOf(Screen.Tutorial, Screen.Loading).map { it.name }.contains(currentScreen) &&
                viewModel.healthState != null
            ) CustomNavigationBar(
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
        snackbarHost = { SnackbarHost(hostState = snackBarHostState) }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = if (!viewModel.isTutorialDone)
                Screen.Tutorial.name
            else if (viewModel.healthState == null)
                Screen.HealthProfile.name
            else
                Screen.NutritionDailyAnalyze.name,
            modifier = Modifier.padding(innerPadding)
        ) {
            Screen.entries.forEach { screen ->
                composable(screen.name) {
                    screen.screenComposable(viewModel, navController, snackBarHostState)
                }
            }
        }
    }
}