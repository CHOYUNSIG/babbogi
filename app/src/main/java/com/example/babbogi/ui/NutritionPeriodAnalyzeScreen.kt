package com.example.babbogi.ui

import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.babbogi.R
import com.example.babbogi.Screen
import com.example.babbogi.model.BabbogiViewModel
import com.example.babbogi.ui.theme.BabbogiTypography
import com.example.babbogi.ui.view.ColumnScreen
import com.example.babbogi.ui.view.DateSelector
import com.example.babbogi.ui.view.FixedColorButton
import com.example.babbogi.ui.view.FloatingContainer
import com.example.babbogi.ui.view.GptAnalyzeReport
import com.example.babbogi.ui.view.NutritionPeriodBarGraph
import com.example.babbogi.ui.view.ScreenPreviewer
import com.example.babbogi.util.Nutrition
import com.example.babbogi.util.NutritionIntake
import com.example.babbogi.util.NutritionRecommendation
import com.example.babbogi.util.getRandomNutritionIntake
import com.example.babbogi.util.testNutritionRecommendation
import com.example.babbogi.util.toNutritionIntake
import java.time.LocalDate

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NutritionPeriodAnalyzeScreen(
    viewModel: BabbogiViewModel,
    navController: NavController,
    showSnackBar: (message: String) -> Unit,
    showAlertPopup: (title: String, message: String, icon: Int) -> Unit,
) {
    var period by remember {
        mutableStateOf(
            viewModel.periodReport?.let {
                listOf(it.first.first(), it.first.last())
            } ?: LocalDate.now().let { listOf(it.minusDays(6), it) }
        )
    }
    var intakes by remember { mutableStateOf<Map<LocalDate, NutritionIntake>?>(null) }
    var report by remember { mutableStateOf(viewModel.periodReport?.second) }
    val getIntakes = remember {
        { endRefresh: (() -> Unit)? ->
            viewModel.getFoodLists(period.first(), period.last()) {
                if (it != null) intakes = it.mapValues { (_, foodList) -> foodList.toNutritionIntake() }
                else showAlertPopup(
                    "오류",
                    "영양 정보를 받아오지 못했습니다.",
                    R.drawable.baseline_cancel_24,
                )
                endRefresh?.invoke()
            }
        }
    }
    val clipboard = LocalClipboardManager.current

    NutritionPeriodAnalyze(
        period = period,
        recommendation = viewModel.nutritionRecommendation,
        intakes = intakes,
        report = report,
        onPeriodChanged = { newPeriod, onEnded ->
            val length = newPeriod.last().toEpochDay() - newPeriod.first().toEpochDay() + 1
            if (length > 7) {
                showAlertPopup(
                    "기간이 너무 김",
                    "조회 기간을 일주일 이내로 설정하세요.",
                    R.drawable.baseline_not_find_30,
                )
                onEnded()
            }
            else if (length < 1) {
                showAlertPopup(
                    "잘못된 기간",
                    "종료일이 시작일보다 앞설 수 없습니다.",
                    R.drawable.baseline_not_find_30,
                )
                onEnded()
            }
            else {
                period = newPeriod
                getIntakes.invoke(onEnded)
            }
        },
        onNewReportRequested = { onLoadingEnded ->
            viewModel.getPeriodReport(period.first(), period.last(), refresh = true) {
                report = it
                if (it == null) showAlertPopup(
                    "레포트 생성 실패",
                    "레포트를 받아오지 못했습니다.",
                    R.drawable.baseline_cancel_24,
                )
                onLoadingEnded()
            }
        },
        onCopyReportToClipboard = {
            clipboard.setText(AnnotatedString(it))
            showSnackBar("레포트가 클립보드에 복사되었습니다.")
        },
    )
}

@OptIn(ExperimentalLayoutApi::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun NutritionPeriodAnalyze(
    period: List<LocalDate>,
    recommendation: NutritionRecommendation,
    intakes: Map<LocalDate, NutritionIntake>?,
    report: String?,
    onPeriodChanged: (List<LocalDate>, onEnded: () -> Unit) -> Unit,
    onNewReportRequested: (onLoadingEnded: () -> Unit) -> Unit,
    onCopyReportToClipboard: (report: String) -> Unit,
) {
    var selectedNutrition by remember { mutableStateOf(Nutrition.Calorie) }
    var selectedPeriod by remember { mutableStateOf(period) }
    var isLoading by remember { mutableStateOf(false) }

    ColumnScreen {
        // 기간 선택기
        FloatingContainer(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            repeat(2) { index ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(text = listOf("시작일", "종료일")[index])
                    DateSelector(
                        initDate = selectedPeriod[index],
                        onDateChanged = {
                            selectedPeriod = selectedPeriod.mapIndexed { i, date ->
                                if (i == index) it else date
                            }
                        },
                    )
                }
            }
            Row(
                horizontalArrangement = Arrangement.End,
                modifier = Modifier.fillMaxWidth()
            ) {
                FixedColorButton(
                    onClick = {
                        isLoading = true
                        onPeriodChanged(selectedPeriod) { isLoading = false }
                    },
                    text = "조회"
                )
            }
        }
        // 영양소 섭취 그래프
        FloatingContainer {
            Text(
                text = stringResource(id = selectedNutrition.res),
                style = BabbogiTypography.titleMedium
            )
            if (isLoading) Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxWidth().padding(16.dp),
            ) {
                CircularProgressIndicator(modifier = Modifier.size(50.dp))
            }
            else if (intakes != null) NutritionPeriodBarGraph(
                nutrition = selectedNutrition,
                recommend = recommendation[selectedNutrition]!!,
                intakes = intakes.mapValues { (_, intake) -> intake[selectedNutrition]!! }
            )
            else Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            ) {
                Text(text = "기간을 설정하고\n조회 버튼을 클릭하세요.\n이곳에 그래프가 표시됩니다.", style = BabbogiTypography.bodySmall)
            }
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(
                    8.dp,
                    Alignment.CenterHorizontally
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Nutrition.entries.forEach { nutrition ->
                    FixedColorButton(
                        onClick = { selectedNutrition = nutrition },
                        text = stringResource(id = nutrition.res)
                    )
                }
            }
        }
        // 기간 분석 보고서
        GptAnalyzeReport(
            title = "기간 레포트",
            report = report,
            prohibitMessage = when {
                LocalDate.now() in period.first()..period.last() -> "오늘이 포함된 기간의 레포트는 생성할 수 없습니다."
                else -> null
            },
            onNewReportRequested = onNewReportRequested,
            onCopyReportToClipboard = onCopyReportToClipboard,
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(uiMode = UI_MODE_NIGHT_NO)
@Preview(uiMode = UI_MODE_NIGHT_YES)
@Composable
fun PreviewNutritionPeriodAnalyze() {
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

    ScreenPreviewer(screen = Screen.NutritionPeriodAnalyze) {
        NutritionPeriodAnalyze(
            period = listOf(data.keys.min(), data.keys.max()),
            recommendation = testNutritionRecommendation,
            intakes = data,
            report = "이것은 챗지피티가 제작한 일일 영양소 레포트입니다. ".repeat(100),
            onPeriodChanged = { _, _ -> },
            onNewReportRequested = {},
            onCopyReportToClipboard = {},
        )
    }
}