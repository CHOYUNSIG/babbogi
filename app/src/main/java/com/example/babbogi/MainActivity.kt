package com.example.babbogi

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
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
import com.example.babbogi.ui.view.CustomPopup
import com.example.babbogi.ui.view.TitleBar
import com.example.babbogi.ui.WeightHistoryManagementScreen
import kotlinx.coroutines.launch

enum class Screen(
    val title: String? = null,
    val screenComposable: @Composable (
        BabbogiViewModel,
        NavHostController,
        showSnackBar: (text: String) -> Unit,
        showAlertPopup: (title: String, message: String, icon: Int) -> Unit,
    ) -> Unit,
) {
    @RequiresApi(Build.VERSION_CODES.O)
    Tutorial(
        screenComposable = { v, n, s, a -> GuidePageScreen(v, n, s, a) }
    ),
    Loading(
        screenComposable = { v, n, s, a -> LoadingScreen(v, n, s, a) }
    ),
    @RequiresApi(Build.VERSION_CODES.O)
    NutritionDailyAnalyze(
        title = "일일 분석",
        screenComposable = { v, n, s, a -> NutritionDailyAnalyzeScreen(v, n, s, a) }
    ),
    @RequiresApi(Build.VERSION_CODES.O)
    NutritionPeriodAnalyze(
        title = "기간 분석",
        screenComposable = { v, n, s, a -> NutritionPeriodAnalyzeScreen(v, n, s, a) }
    ),
    @RequiresApi(Build.VERSION_CODES.O)
    WeightHistoryManagement(
        title = "몸무게 관리",
        screenComposable = { v, n, s, a -> WeightHistoryManagementScreen(v, n, s, a) },
    ),
    @RequiresApi(Build.VERSION_CODES.O)
    NutritionOverview(
        title = "전체 영양 정보",
        screenComposable = { v, n, s, a -> NutritionOverviewScreen(v, n, s, a) }
    ),
    @RequiresApi(Build.VERSION_CODES.O)
    FoodList(
        title = "음식 추가",
        screenComposable = { v, n, s, a -> FoodListScreen(v, n, s, a) }
    ),
    Camera(
        title = "바코드 입력",
        screenComposable = { v, n, s, a -> CameraViewScreen(v, n, s, a) }
    ),
    FoodSearch(
        title = "음식 검색",
        screenComposable = { v, n, s, a -> FoodSearchScreen(v, n, s, a) }
    ),
    @RequiresApi(Build.VERSION_CODES.O)
    Setting(
        title = "설정",
        screenComposable = { v, n, s, a -> SettingScreen(v, n, s, a) }
    ),
    @RequiresApi(Build.VERSION_CODES.O)
    HealthProfile(
        title = "건강 정보",
        screenComposable = { v, n, s, a -> HealthProfileScreen(v, n, s, a) }
    ),
}

class MainActivity : ComponentActivity() {
    private val viewModel by lazy { BabbogiViewModel() }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        BabbogiModel.init(this)

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
    // 내비게이션 설정
    val navController = rememberNavController()
    var currentScreen by remember { mutableStateOf<String?>(null) }

    navController.addOnDestinationChangedListener { _, destination, _ ->
        currentScreen = destination.route
    }
    
    // 스낵바 설정
    val scope = rememberCoroutineScope()
    val snackBarHostState = remember { SnackbarHostState() }
    val showSnackBar = remember {
        { text: String ->
            scope.launch {
                snackBarHostState.currentSnackbarData?.dismiss()
                snackBarHostState.showSnackbar(
                    message = text,
                    actionLabel = "확인",
                    duration = SnackbarDuration.Short,
                )
            }
        }
    }
    
    // 오류 팝업 설정
    var isShowingAlert by remember { mutableStateOf(false) }
    var alertTitle by remember { mutableStateOf("") }
    var alertMessage by remember { mutableStateOf("") }
    var alertIcon by remember { mutableIntStateOf(R.drawable.baseline_cancel_24) }
    val showAlertPopup = remember {
        { title: String, message: String, icon: Int ->
            alertTitle = title
            alertMessage = message
            alertIcon = icon
            isShowingAlert = true
        }
    }
    
    // 앱 실행
    Scaffold(
        topBar = {
            val title = currentScreen?.let { Screen.valueOf(it).title }
            if (title != null) TitleBar(title = title) {
                if (currentScreen != Screen.Setting.name && currentScreen != Screen.HealthProfile.name) Row(horizontalArrangement = Arrangement.End) {
                    IconButton(onClick = { navController.navigate(Screen.WeightHistoryManagement.name) }) {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_accessibility_24),
                            contentDescription = Screen.WeightHistoryManagement.title,
                        )
                    }
                    IconButton(onClick = { navController.navigate(Screen.Setting.name) }) {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_settings_24),
                            contentDescription = Screen.Setting.title,
                        )
                    }
                }
            }
        },
        bottomBar = {
            if (
                currentScreen != Screen.Tutorial.name &&
                currentScreen != Screen.Loading.name &&
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
                    screen.screenComposable(viewModel, navController, showSnackBar, showAlertPopup)
                }
            }
        }
    }

    if (isShowingAlert) CustomPopup(
        callbacks = listOf { isShowingAlert = false },
        labels = listOf("확인"),
        onDismiss = { isShowingAlert = false },
        title = alertTitle,
        icon = alertIcon,
        content = { Text(text = alertMessage) }
    )
}