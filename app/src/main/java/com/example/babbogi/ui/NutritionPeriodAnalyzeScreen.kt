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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.babbogi.model.BabbogiViewModel
import com.example.babbogi.ui.view.ColumnWithDefault
import com.example.babbogi.ui.view.ElevatedCardWithDefault
import com.example.babbogi.ui.view.GptAnalyzeReport
import com.example.babbogi.ui.view.NutritionPeriodBarGraph
import com.example.babbogi.ui.view.PreviewCustomNavigationBar
import com.example.babbogi.ui.view.TitleBar
import com.example.babbogi.util.Nutrition
import java.time.LocalDate

@Composable
fun NutritionPeriodAnalyzeScreen(viewModel: BabbogiViewModel, navController: NavController) {

}

@Composable
fun PeriodSelector(
    startDate: LocalDate,
    endDate: LocalDate,
    onStartDateClicked: () -> Unit,
    onEndDateClicked: () -> Unit,
) {
    Row(
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(3.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(text = "시작일")
            ElevatedCardWithDefault(onClick = onStartDateClicked) {
                Box(modifier = Modifier.padding(8.dp)) {
                    Text(
                        text = startDate.toString(),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Normal,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
        Text(text = "~", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Column(
            verticalArrangement = Arrangement.spacedBy(3.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(text = "종료일")
            ElevatedCardWithDefault(onClick = onEndDateClicked) {
                Box(modifier = Modifier.padding(8.dp)) {
                    Text(
                        text = endDate.toString(),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Normal,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun NutritionCheckBox(onSelected: (Nutrition) -> Unit) {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
        modifier = Modifier.fillMaxWidth()
    ) {
        Nutrition.entries.forEach { nutrition ->
            ElevatedButton(
                onClick = { onSelected(nutrition) },
                contentPadding = PaddingValues(5.dp)
            ) {
                Text(stringResource(id = nutrition.res), color = Color.Black)
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NutritionPeriodAnalyze(
    startDate: LocalDate,
    endDate: LocalDate,
    nutrition: Nutrition,
    recommend: Float,
    data: Map<LocalDate, Float>,
    report: String,
    onNutritionSelected: (Nutrition) -> Unit,
    onStartDateClicked: () -> Unit,
    onEndDateClicked: () -> Unit,
    onDateSelected: (LocalDate, isStartDate: Boolean) -> Unit,
) {
    Column {
        TitleBar(title = "기간 분석")
        ColumnWithDefault(modifier = Modifier.verticalScroll(rememberScrollState())) {
            PeriodSelector(
                startDate = startDate,
                endDate = endDate,
                onStartDateClicked = onStartDateClicked,
                onEndDateClicked = onEndDateClicked,
            )
            ElevatedCardWithDefault {
                ColumnWithDefault {
                    NutritionPeriodBarGraph(nutrition = nutrition, recommend = recommend, data = data)
                    NutritionCheckBox(onSelected = onNutritionSelected)
                }
            }
            GptAnalyzeReport(title = "기간 레포트", report = report)
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview
@Composable
fun PreviewNutritionPeriodAnalyze() {
    Scaffold(
        bottomBar = { PreviewCustomNavigationBar() }
    ) {
        Box(modifier = Modifier.padding(it)) {
            NutritionPeriodAnalyze(
                startDate = LocalDate.now(),
                endDate = LocalDate.now(),
                nutrition = Nutrition.Calorie,
                recommend = 2200f,
                data = mapOf(
                    LocalDate.parse("2024-01-01") to 2000f,
                    LocalDate.parse("2024-01-02") to 1000f,
                    LocalDate.parse("2024-01-03") to 1500f,
                    LocalDate.parse("2024-01-04") to 2200f,
                    LocalDate.parse("2024-01-05") to 2100f,
                    LocalDate.parse("2024-01-06") to 1400f,
                    LocalDate.parse("2024-01-07") to 1600f,
                ),
                report = "이것은 챗지피티가 제작한 일일 영양소 레포트입니다. ".repeat(100),
                onNutritionSelected = {},
                onStartDateClicked = {},
                onEndDateClicked = {},
                onDateSelected = { _, _ -> }
            )
        }
    }
}