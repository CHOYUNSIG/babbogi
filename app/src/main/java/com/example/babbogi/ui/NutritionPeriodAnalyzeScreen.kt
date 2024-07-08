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
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.babbogi.R
import com.example.babbogi.model.BabbogiViewModel
import com.example.babbogi.ui.view.ElevatedCardWithDefault
import com.example.babbogi.ui.view.NutritionPeriodBarGraph
import com.example.babbogi.util.Nutrition
import java.time.LocalDate

@Composable
fun NutritionPeriodAnalyzeScreen(viewModel: BabbogiViewModel, navController: NavController) {

}

@Composable
fun ReportCard(report: String) {
    ElevatedCardWithDefault(modifier = Modifier.fillMaxWidth()) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "일일 레포트", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(3.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(text = "Powered by", fontSize = 12.sp)
                    Icon(
                        modifier = Modifier.size(20.dp),
                        painter = painterResource(id = R.drawable.chatgpt_logo),
                        contentDescription = "ChatGPT 로고"
                    )
                    Text(text = "ChatGPT")
                }
            }
            ElevatedCardWithDefault {
                Box(modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()) {
                    Text(text = report)
                }
            }
        }
    }
}

@Composable
fun PeriodSelector() {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(3.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(8.dp)
        ) {
            Text(text = "시작일")
            ElevatedCardWithDefault(onClick = { /*TODO*/ }) {
                Box(modifier = Modifier.padding(8.dp)) {
                    Text(text = "2024-01-01")
                }
            }
        }
        Text(text = "~", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Column(
            verticalArrangement = Arrangement.spacedBy(3.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(8.dp)
        ) {
            Text(text = "종료일")
            ElevatedCardWithDefault(onClick = { /*TODO*/ }) {
                Box(modifier = Modifier.padding(8.dp)) {
                    Text(text = "2024-01-01")
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
    nutrition: Nutrition,
    recommend: Float,
    data: Map<LocalDate, Float>,
    report: String,
    onNutritionSelected: (Nutrition) -> Unit,
    onDateSelected: (LocalDate, isStartDate: Boolean) -> Unit,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
    ) {
        ElevatedCardWithDefault(modifier = Modifier.fillMaxWidth()) {
            Column(
                verticalArrangement = Arrangement.spacedBy(20.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
            ) {
                Text(stringResource(id = nutrition.res), fontSize = 20.sp, fontWeight = FontWeight.Bold)
                NutritionPeriodBarGraph(nutrition = nutrition, recommend = recommend, data = data)
                PeriodSelector()
                NutritionCheckBox(onSelected = onNutritionSelected)
            }
        }
        ReportCard(report)
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview
@Composable
fun PreviewNutritionPeriodAnalyze() {
    NutritionPeriodAnalyze(
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
        report = "이것은 챗지피티가 제작한 일일 영양소 레포트입니다.",
        onNutritionSelected = {},
        onDateSelected = { _, _ -> }
    )
}