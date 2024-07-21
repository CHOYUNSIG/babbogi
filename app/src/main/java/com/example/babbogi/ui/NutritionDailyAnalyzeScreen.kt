package com.example.babbogi.ui

import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
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
import com.example.babbogi.ui.view.DescriptionText
import com.example.babbogi.ui.view.ElevatedCardWithDefault
import com.example.babbogi.ui.view.GptAnalyzeReport
import com.example.babbogi.ui.view.NutritionAbstraction
import com.example.babbogi.ui.view.PreviewCustomNavigationBar
import com.example.babbogi.ui.view.ProductAbstraction
import com.example.babbogi.ui.view.TitleBar
import com.example.babbogi.util.NutritionIntake
import com.example.babbogi.util.NutritionRecommendation
import com.example.babbogi.util.Product
import com.example.babbogi.util.getRandomNutritionIntake
import com.example.babbogi.util.testNutritionRecommendation
import com.example.babbogi.util.testProductTripleList
import com.example.babbogi.util.toNutritionIntake
import java.time.LocalDate
import java.time.LocalDateTime

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NutritionDailyAnalyzeScreen(
    viewModel: BabbogiViewModel,
    navController: NavController,
    showSnackBar: (message: String, actionLabel: String, duration: SnackbarDuration) -> Unit
) {
    var foodList by remember { mutableStateOf<List<Triple<Product, LocalDateTime, Int>>?>(null) }
    var intake by remember { mutableStateOf<NutritionIntake?>(null) }
    var report by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(viewModel.today) {
        viewModel.getFoodLists(viewModel.today) {
            if (it != null) {
                foodList = it[viewModel.today]
                intake = foodList?.toNutritionIntake()
            }
        }
        viewModel.getDailyReport(viewModel.today, generate = false) { report = it }
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
                if (it == null) showSnackBar(
                    "오류: 레포트를 받아오지 못했습니다.",
                    "확인",
                    SnackbarDuration.Short
                )
                report = it
                onLoadingEnded()
            }
        },
        onSettingClicked = { navController.navigate(Screen.Setting.name) },
        onRefresh = { endRefresh ->
            viewModel.getFoodLists(viewModel.today, refresh = true) {
                if (it != null) foodList = it[viewModel.today]
                else showSnackBar(
                    "오류: 식사 정보를 받아오지 못했습니다.",
                    "확인",
                    SnackbarDuration.Short
                )
                intake = foodList?.toNutritionIntake()
                endRefresh()
            }
        }
    )
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NutritionDailyAnalyze(
    today: LocalDate,
    recommendation: NutritionRecommendation,
    intake: NutritionIntake?,
    foodList: List<Triple<Product, LocalDateTime, Int>>?,
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
                            initDate = today,
                            onDateChanged = onDateChanged,
                        )
                    }
                }
                if (intake != null) NutritionAbstraction(
                    recommendation = recommendation,
                    intake = intake,
                    onClick = onNutritionCardClicked,
                )
                GptAnalyzeReport(
                    title = "일일 레포트",
                    date = today,
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

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun MealList(foodList: List<Triple<Product, LocalDateTime, Int>>?) {
    ElevatedCardWithDefault {
        ColumnWithDefault {
            Row(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "사용자 식사 정보",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
            if (foodList == null) Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(50.dp)
                        .padding(16.dp)
                )
            }
            else if (foodList.isEmpty()) Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                DescriptionText(text = "섭취한 음식이 없어요!")
            }
            else LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.heightIn(max = 500.dp)
            ) {
                items(foodList.size) { index ->
                    ProductAbstraction(
                        product = foodList[index].first,
                        amount = foodList[index].third
                    ) {
                        DescriptionText(
                            text = "${if (foodList[index].second.hour < 12) "오전" else "오후"} %02d:%02d"
                                .format(
                                    (foodList[index].second.hour + 11) % 12 + 1,
                                    foodList[index].second.minute
                                )
                        )
                    }
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(uiMode = UI_MODE_NIGHT_NO)
@Preview(uiMode = UI_MODE_NIGHT_YES)
@Composable
fun PreviewNutritionDailyAnalyze() {
    BabbogiTheme {
        Scaffold(bottomBar = { PreviewCustomNavigationBar() }) {
            Box(modifier = Modifier.padding(it)) {
                NutritionDailyAnalyze(
                    today = LocalDate.now(),
                    recommendation = testNutritionRecommendation,
                    intake = getRandomNutritionIntake(),
                    foodList = testProductTripleList,
                    report = null,
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