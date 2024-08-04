package com.example.babbogi.ui

import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.babbogi.R
import com.example.babbogi.Screen
import com.example.babbogi.model.BabbogiViewModel
import com.example.babbogi.ui.theme.BabbogiTypography
import com.example.babbogi.ui.view.ColumnScreen
import com.example.babbogi.ui.view.CustomPopup
import com.example.babbogi.ui.view.DateSelector
import com.example.babbogi.ui.view.FloatingContainer
import com.example.babbogi.ui.view.GptAnalyzeReport
import com.example.babbogi.ui.view.NutritionAbstraction
import com.example.babbogi.ui.view.ProductAbstraction
import com.example.babbogi.ui.view.ScreenPreviewer
import com.example.babbogi.ui.view.WeightHistoryPopup
import com.example.babbogi.util.Consumption
import com.example.babbogi.util.NutritionRecommendation
import com.example.babbogi.util.WeightHistory
import com.example.babbogi.util.testConsumptionList
import com.example.babbogi.util.testNutritionRecommendation
import com.example.babbogi.util.toNutritionIntake
import java.time.LocalDate

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NutritionDailyAnalyzeScreen(
    viewModel: BabbogiViewModel,
    navController: NavController,
    showSnackBar: (message: String) -> Unit,
    showAlertPopup: (title: String, message: String, icon: Int) -> Unit,
) {
    var foodList by remember { mutableStateOf<List<Consumption>?>(null) }
    var report by remember { mutableStateOf<String?>(null) }
    val clipboard = LocalClipboardManager.current

    LaunchedEffect(viewModel.today) {
        foodList = null
        report = null
        viewModel.getFoodLists(viewModel.today) { foodList = it?.get(viewModel.today) }
        viewModel.getDailyReport(viewModel.today, generate = false) { report = it }
    }

    NutritionDailyAnalyze(
        today = viewModel.today,
        recommendation = viewModel.nutritionRecommendation,
        foodList = foodList,
        report = report,
        onNutritionCardClicked = { navController.navigate(Screen.NutritionOverview.name) },
        onDateChanged = { viewModel.today = it },
        onNewReportRequested = { onLoadingEnded ->
            viewModel.getDailyReport(viewModel.today, refresh = true) {
                if (it == null) showAlertPopup(
                    "레포트 생성 실패",
                    "레포트를 받아오지 못했습니다.",
                    R.drawable.baseline_cancel_24,
                )
                report = it
                onLoadingEnded()
            }
        },
        onCopyReportToClipboard = {
            clipboard.setText(AnnotatedString(it))
            showSnackBar("레포트가 클립보드에 복사되었습니다.")
        },
        onWeightClicked = { onLoaded ->
            viewModel.getWeightHistory(true) {
                onLoaded(it, viewModel.healthState?.height)
            }
        },
        onChangeWeightClicked = { navController.navigate(Screen.HealthProfile.name) },
        onDeleteFoodClicked = { id, onEnded ->
            viewModel.deleteConsumption(id) { onEnded(it) }
        },
        onRefresh = { endRefresh ->
            foodList = null
            viewModel.getFoodLists(viewModel.today, refresh = true) {
                foodList = it?.get(viewModel.today)
                if (it == null) showAlertPopup(
                    "오류",
                    "식사 정보를 받아오지 못했습니다.",
                    R.drawable.baseline_cancel_24,
                )
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
    foodList: List<Consumption>?,
    report: String?,
    onNutritionCardClicked: () -> Unit,
    onDateChanged: (LocalDate) -> Unit,
    onNewReportRequested: (onLoadingEnded: () -> Unit) -> Unit,
    onCopyReportToClipboard: (report: String) -> Unit,
    onWeightClicked: (onLoaded: (List<WeightHistory>?, Float?) -> Unit) -> Unit,
    onChangeWeightClicked: () -> Unit,
    onDeleteFoodClicked: (id: Long, onEnded: (success: Boolean) -> Unit) -> Unit,
    onRefresh: (endRefresh: () -> Unit) -> Unit,
) {
    var showWeightHistoryPopup by remember { mutableStateOf(false) }
    var deletionConsumption by remember { mutableStateOf<Consumption?>(null) }
    var isDeletionProcessing by remember { mutableStateOf(false) }
    val intake by remember(foodList) { mutableStateOf(foodList?.toNutritionIntake()) }
    val refreshState = rememberPullToRefreshState()

    if (refreshState.isRefreshing) {
        LaunchedEffect(true) { onRefresh { refreshState.endRefresh() } }
    }

    Box(modifier = Modifier.nestedScroll(refreshState.nestedScrollConnection)) {
        ColumnScreen {
            // 날짜 선택기
            FloatingContainer(innerPadding = 8.dp) {
                DateSelector(
                    initDate = today,
                    onDateChanged = onDateChanged,
                )
            }
            // 간략한 영양소 현황
            intake?.let {
                NutritionAbstraction(
                    recommendation = recommendation,
                    intake = it,
                    onClick = onNutritionCardClicked
                )
            }
            // 일일 분석 보고서
            GptAnalyzeReport(
                title = "일일 레포트",
                isDateIncludesToday = today == LocalDate.now(),
                report = report,
                onNewReportRequested = onNewReportRequested,
                onCopyReportToClipboard = onCopyReportToClipboard,
            )
            // 식사 정보
            MealList(
                foodList = foodList,
                onDeleteClicked = { deletionConsumption = it },
            )
        }

        // 새로고침 로딩 아이콘
        PullToRefreshContainer(
            state = refreshState,
            modifier = Modifier.align(Alignment.TopCenter)
        )
    }

    if (showWeightHistoryPopup) WeightHistoryPopup(
        onStarted = onWeightClicked,
        onDismissRequest = { showWeightHistoryPopup = false },
        onConfirmClicked = { showWeightHistoryPopup = false },
        onChangeWeightClicked = {
            showWeightHistoryPopup = false
            onChangeWeightClicked()
        }
    )

    // 음식 삭제시 확인 팝업 표시
    deletionConsumption?.let { consumption ->
        CustomPopup(
            callbacks = listOf(
                {
                    isDeletionProcessing = true
                    onDeleteFoodClicked(consumption.id) {
                        isDeletionProcessing = false
                        deletionConsumption = null
                        refreshState.startRefresh()
                        onRefresh { refreshState.endRefresh() }
                    }
                },
                { deletionConsumption = null }
            ),
            labels = listOf("삭제", "취소"),
            onDismiss = { deletionConsumption = null },
            title = "다음 섭취 기록을 삭제하시겠습니까?"
        ) {
            if (isDeletionProcessing)
                CircularProgressIndicator(modifier = Modifier.size(50.dp))
            else
                Text(text = consumption.product.name)
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun MealList(
    foodList: List<Consumption>?,
    onDeleteClicked: (Consumption) -> Unit,
) {
    var showingConsumption by remember { mutableStateOf<Consumption?>(null) }

    FloatingContainer {
        // 제목 행
        Row(modifier = Modifier.fillMaxWidth()) {
            Text(text = "사용자 식사 정보", style = BabbogiTypography.titleMedium)
        }
        // 로딩중임을 고지
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
        // 섭취한 음식이 없음을 고지
        else if (foodList.isEmpty()) Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(text = "섭취한 음식이 없어요!", style = BabbogiTypography.bodySmall)
        }
        // 섭취한 음식을 표시
        else LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.heightIn(max = 500.dp)
        ) {
            item(key = null) { Spacer(modifier = Modifier) }  // 그림자 크기만큼 패딩
            items(foodList.size, key = { foodList[it].id }) { index ->
                val food = foodList[index]
                ProductAbstraction(
                    product = food.product,
                    intakeRatio = food.intakeRatio,
                    onClick = { showingConsumption = food }
                ) {
                    Text(
                        text = food.time?.let { time ->
                            "${if (time.hour < 12) "오전" else "오후"} %02d:%02d"
                                .format((time.hour + 11) % 12 + 1, time.minute)
                        } ?: "나중에 추가됨",
                        style = BabbogiTypography.bodySmall,
                    )
                    IconButton(onClick = { onDeleteClicked(food) }) {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_delete_24),
                            contentDescription = "삭제"
                        )
                    }
                }
            }
            item(key = null) { Spacer(modifier = Modifier) }  // 그림자 크기만큼 패딩
        }
    }

    // 음식을 클릭할 시 팝업 표시
    showingConsumption?.let { consumption ->
        CustomPopup(
            callbacks = listOf(
                { showingConsumption = null },
                {
                    showingConsumption = null
                    onDeleteClicked(consumption)
                },
            ),
            labels = listOf("확인", "삭제"),
            onDismiss = { showingConsumption = null }
        ) {
            ProductAbstraction(
                product = consumption.product,
                intakeRatio = consumption.intakeRatio,
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(uiMode = UI_MODE_NIGHT_NO)
@Preview(uiMode = UI_MODE_NIGHT_YES)
@Composable
fun PreviewNutritionDailyAnalyze() {
    ScreenPreviewer(screen = Screen.NutritionDailyAnalyze) {
        NutritionDailyAnalyze(
            today = LocalDate.now(),
            recommendation = testNutritionRecommendation,
            foodList = testConsumptionList,
            report = null,
            onNutritionCardClicked = {},
            onDateChanged = {},
            onNewReportRequested = {},
            onCopyReportToClipboard = {},
            onWeightClicked = {},
            onChangeWeightClicked = {},
            onDeleteFoodClicked = { _, _ -> },
            onRefresh = {},
        )
    }
}