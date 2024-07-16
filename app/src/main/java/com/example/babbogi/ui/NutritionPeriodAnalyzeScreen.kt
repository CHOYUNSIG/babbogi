package com.example.babbogi.ui

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.babbogi.R
import com.example.babbogi.Screen
import com.example.babbogi.model.BabbogiViewModel
import com.example.babbogi.ui.theme.BabbogiTheme
import com.example.babbogi.ui.view.ColumnWithDefault
import com.example.babbogi.ui.view.CustomAlertDialog
import com.example.babbogi.ui.view.DateSelector
import com.example.babbogi.ui.view.ElevatedCardWithDefault
import com.example.babbogi.ui.view.GptAnalyzeReport
import com.example.babbogi.ui.view.NutritionPeriodBarGraph
import com.example.babbogi.ui.view.PreviewCustomNavigationBar
import com.example.babbogi.ui.view.TitleBar
import com.example.babbogi.util.Nutrition
import com.example.babbogi.util.NutritionIntake
import com.example.babbogi.util.NutritionRecommendation
import com.example.babbogi.util.getRandomNutritionIntake
import com.example.babbogi.util.testNutritionRecommendation
import com.example.babbogi.util.toNutritionIntake
import java.time.LocalDate

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NutritionPeriodAnalyzeScreen(viewModel: BabbogiViewModel, navController: NavController) {
    var period by remember { LocalDate.now().let { mutableStateOf(listOf(it.minusDays(6), it)) } }
    var intakes by remember { mutableStateOf<Map<LocalDate, NutritionIntake>>(emptyMap()) }
    var report by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(period) {
        val length = period.last().toEpochDay() - period.first().toEpochDay() + 1
        if (length > 7) return@LaunchedEffect
        val newIntakes = emptyMap<LocalDate, NutritionIntake>().toMutableMap()
        var date = period.first()
        while (date <= period.last()) {
            viewModel.getFoodList(date) { foodList ->
                newIntakes[date] = foodList?.toNutritionIntake() ?: Nutrition.entries.associateWith { 0f }
                if (newIntakes.size.toLong() == length) intakes = newIntakes
            }
            date = date.plusDays(1)
        }
    }

    NutritionPeriodAnalyze(
        period = period,
        recommendation = viewModel.nutritionRecommendation,
        intakes = intakes,
        report = report,
        onPeriodChanged = { period = it },
        onNewReportRequested = { /*TODO*/ },
        onSettingClicked = { navController.navigate(Screen.Setting.name) },
        onRefresh = { endRefresh ->
            /*TODO*/
            endRefresh()
        },
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun NutritionCheckBox(onSelected: (Nutrition) -> Unit) {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
        modifier = Modifier.fillMaxWidth()
    ) {
        Nutrition.entries.forEach { nutrition ->
            Button(
                onClick = { onSelected(nutrition) },
                contentPadding = PaddingValues(5.dp)
            ) {
                Text(stringResource(id = nutrition.res), color = Color.White)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NutritionPeriodAnalyze(
    period: List<LocalDate>,
    recommendation: NutritionRecommendation,
    intakes: Map<LocalDate, NutritionIntake>,
    report: String?,
    onPeriodChanged: (List<LocalDate>) -> Unit,
    onNewReportRequested: (onLoadingEnded: () -> Unit) -> Unit,
    onSettingClicked: () -> Unit,
    onRefresh: (endRefresh: () -> Unit) -> Unit
) {
    var selectedNutrition by remember { mutableStateOf<Nutrition?>(null) }
    var selectedPeriod by remember { mutableStateOf(period) }
    var showLongPeriodAlert by remember { mutableStateOf(false) }
    var showInvalidPeriodAlert by remember { mutableStateOf(false) }
    val refreshState = rememberPullToRefreshState()

    if (refreshState.isRefreshing) {
        LaunchedEffect(true) { onRefresh { refreshState.endRefresh() } }
    }

    Box(modifier = Modifier.nestedScroll(refreshState.nestedScrollConnection)) {
        Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
            TitleBar(title = "기간 분석") {
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
                        Column {
                            repeat(2) { index ->
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Spacer(modifier = Modifier.width(16.dp))
                                    Text(text = listOf("시작일", "종료일")[index])
                                    DateSelector(
                                        today = selectedPeriod[index],
                                        onDateChanged = {
                                            selectedPeriod = selectedPeriod.mapIndexed { i, date ->
                                                if (i == index) it else date
                                            }
                                        },
                                    )
                                }
                            }
                        }
                        Row(
                            horizontalArrangement = Arrangement.End,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Button(onClick = {
                                val length =
                                    selectedPeriod.last().toEpochDay() - selectedPeriod.first()
                                        .toEpochDay() + 1
                                if (length > 7)
                                    showLongPeriodAlert = true
                                else if (length < 1)
                                    showInvalidPeriodAlert = true
                                else
                                    onPeriodChanged(selectedPeriod)
                            }) {
                                Text(text = "적용")
                            }
                        }
                    }
                }
                ElevatedCardWithDefault {
                    ColumnWithDefault {
                        selectedNutrition?.let {
                            Text(text = stringResource(id = it.res), fontSize = 20.sp)
                            NutritionPeriodBarGraph(
                                nutrition = it,
                                recommend = recommendation[it]!!,
                                intakes = intakes.mapValues { (_, intake) -> intake[it]!! }
                            )
                        } ?: Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                        ) {
                            Text(text = "아래에서 영양소를 선택하세요", color = Color.Gray)
                        }
                        NutritionCheckBox(onSelected = { selectedNutrition = it })
                    }
                }
                GptAnalyzeReport(
                    title = "기간 레포트",
                    report = report,
                    onNewReportRequested = onNewReportRequested,
                )
            }
        }

        PullToRefreshContainer(
            state = refreshState,
            modifier = Modifier.align(Alignment.TopCenter)
        )
    }

    if (showLongPeriodAlert) CustomAlertDialog(
        onDismissRequest = { showLongPeriodAlert = false },
        onConfirmation = { showLongPeriodAlert = false },
        dialogTitle = "기간이 너무 김",
        dialogText = "조회 기간을 일주일 이내로 설정하세요.",
        iconResId = R.drawable.baseline_not_find_30,
    )

    if (showInvalidPeriodAlert) CustomAlertDialog(
        onDismissRequest = { showInvalidPeriodAlert = false },
        onConfirmation = { showInvalidPeriodAlert = false },
        dialogTitle = "잘못된 기간",
        dialogText = "종료일이 시작일보다 앞설 수 없습니다.",
        iconResId = R.drawable.baseline_not_find_30,
    )
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview
@Composable
fun PreviewNutritionPeriodAnalyze() {
    BabbogiTheme {
        Scaffold(bottomBar = { PreviewCustomNavigationBar() }) {
            val data by remember {
                mutableStateOf(
                    mapOf(
                        LocalDate.parse("2024-01-01") to getRandomNutritionIntake(),
                        LocalDate.parse("2024-01-02") to getRandomNutritionIntake(),
                        LocalDate.parse("2024-01-03") to getRandomNutritionIntake(),
                        LocalDate.parse("2024-01-04") to getRandomNutritionIntake(),
                        LocalDate.parse("2024-01-05") to getRandomNutritionIntake(),
                        LocalDate.parse("2024-01-06") to getRandomNutritionIntake(),
                        LocalDate.parse("2024-01-07") to getRandomNutritionIntake(),
                    )
                )
            }

            Box(modifier = Modifier.padding(it)) {
                NutritionPeriodAnalyze(
                    period = listOf(data.keys.min(), data.keys.max()),
                    recommendation = testNutritionRecommendation,
                    intakes = data,
                    report = "이것은 챗지피티가 제작한 일일 영양소 레포트입니다. ".repeat(100),
                    onPeriodChanged = {},
                    onNewReportRequested = {},
                    onSettingClicked = {},
                    onRefresh = {}
                )
            }
        }
    }
}