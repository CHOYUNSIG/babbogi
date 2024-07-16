package com.example.babbogi.ui

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.babbogi.R
import com.example.babbogi.Screen
import com.example.babbogi.model.BabbogiViewModel
import com.example.babbogi.ui.theme.BabbogiTheme
import com.example.babbogi.ui.view.ColumnWithDefault
import com.example.babbogi.ui.view.DateSelector
import com.example.babbogi.ui.view.ElevatedCardWithDefault
import com.example.babbogi.ui.view.GptAnalyzeReport
import com.example.babbogi.ui.view.NutritionAbstraction
import com.example.babbogi.ui.view.PreviewCustomNavigationBar
import com.example.babbogi.ui.view.ProductAbstraction
import com.example.babbogi.ui.view.TitleBar
import com.example.babbogi.util.Nutrition
import com.example.babbogi.util.NutritionIntake
import com.example.babbogi.util.NutritionRecommendation
import com.example.babbogi.util.Product
import com.example.babbogi.util.testNutritionIntake
import com.example.babbogi.util.testNutritionRecommendation
import com.example.babbogi.util.testProductList
import com.example.babbogi.util.toNutritionIntake
import java.time.LocalDate

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NutritionDailyAnalyzeScreen(viewModel: BabbogiViewModel, navController: NavController) {
    var foodList by remember { mutableStateOf<List<Pair<Product, Int>>>(emptyList()) }
    var intake by remember { mutableStateOf<NutritionIntake?>(null) }
    var report by remember { mutableStateOf<String?>(null) }

    if (!viewModel.isTutorialDone) navController.navigate(Screen.Tutorial.name)

    LaunchedEffect(viewModel.today) {
        viewModel.getFoodList(viewModel.today) {
            if (it != null) {
                foodList = it
                intake = foodList.toNutritionIntake()
            }
        }
    }

    NutritionDailyAnalyze(
        today = viewModel.today,
        recommendation = viewModel.nutritionRecommendation,
        intake = intake,
        foodList = foodList,
        report = report,
        onNutritionCardClicked = { navController.navigate(Screen.NutritionOverview.name) },
        onDateChanged = { viewModel.today = it },
        onNewReportRequested = { onLoadingEnded ->
            viewModel.getDailyReport(viewModel.today) {
                report = it
                onLoadingEnded()
            }
        },
        onSettingClicked = { navController.navigate(Screen.Setting.name) },
        onRefresh = { endRefresh ->
            viewModel.getFoodList(viewModel.today) { endRefresh() }
        }
    )
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NutritionDailyAnalyze(
    today: LocalDate,
    recommendation: NutritionRecommendation,
    intake: NutritionIntake?,
    foodList: List<Pair<Product, Int>>?,
    report: String?,
    onNutritionCardClicked: () -> Unit,
    onDateChanged: (LocalDate) -> Unit,
    onNewReportRequested: (onLoadingEnded: () -> Unit) -> Unit,
    onSettingClicked: () -> Unit,
    onRefresh: (endRefresh: () -> Unit) -> Unit,
) {
    val refreshState = rememberPullToRefreshState()

    if (refreshState.isRefreshing) {
        LaunchedEffect(true) { onRefresh { refreshState.endRefresh() } }
    }

    Box(modifier = Modifier.nestedScroll(refreshState.nestedScrollConnection)) {
        Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
            TitleBar(title = "일일 분석") {
                IconButton(onClick = onSettingClicked) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_settings_24),
                        contentDescription = "설정",
                    )
                }
            }
            ColumnWithDefault {
                ElevatedCardWithDefault {
                    ColumnWithDefault {
                        DateSelector(
                            today = today,
                            onDateChanged = onDateChanged,
                        )
                    }
                }
                NutritionAbstraction(
                    recommendation = recommendation,
                    intake = intake ?: Nutrition.entries.associateWith { 0f },
                    onClick = onNutritionCardClicked,
                )
                GptAnalyzeReport(
                    title = "일일 레포트",
                    report = report,
                    onNewReportRequested = onNewReportRequested,
                )
                MealList(foodList)
            }
        }

        PullToRefreshContainer(
            state = refreshState,
            modifier = Modifier.align(Alignment.TopCenter)
        )
    }
}

@Composable
fun MealList(foodList: List<Pair<Product, Int>>?, ) {
    ElevatedCardWithDefault {
        ColumnWithDefault {
            Row(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "사용자 식사 정보",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
        if (foodList == null) Row (
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            CircularProgressIndicator(modifier = Modifier
                .size(50.dp)
                .padding(16.dp))
        }
        else if (foodList.isEmpty()) Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        ) {
            Text("섭취한 음식이 없어요!", color = Color.Gray, modifier = Modifier.padding(16.dp))
        }
        else ColumnWithDefault(
            modifier = Modifier
                .height(500.dp)
                .verticalScroll(rememberScrollState())
        ) {
            foodList.forEach { (product, amount) ->
                ProductAbstraction(product = product, amount = amount)
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview
@Composable
fun PreviewNutritionDailyAnalyze() {
    BabbogiTheme {
        Scaffold(bottomBar = { PreviewCustomNavigationBar() }) {
            Box(modifier = Modifier.padding(it)) {
                NutritionDailyAnalyze(
                    today = LocalDate.now(),
                    recommendation = testNutritionRecommendation,
                    intake = testNutritionIntake,
                    foodList = testProductList,
                    report = "이것은 챗지피티가 생성한 일일 레포트입니다.".repeat(100),
                    onDateChanged = {},
                    onNutritionCardClicked = {},
                    onNewReportRequested = {},
                    onSettingClicked = {},
                    onRefresh = {},
                )
            }
        }
    }
}