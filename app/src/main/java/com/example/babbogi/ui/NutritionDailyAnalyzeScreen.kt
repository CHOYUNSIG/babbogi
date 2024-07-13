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
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.example.babbogi.R
import com.example.babbogi.ui.view.ColumnWithDefault
import com.example.babbogi.ui.view.ElevatedCardWithDefault
import com.example.babbogi.ui.view.GptAnalyzeReport
import com.example.babbogi.ui.view.NutritionAbstraction
import com.example.babbogi.ui.view.PreviewCustomNavigationBar
import com.example.babbogi.ui.view.ProductAbstraction
import com.example.babbogi.ui.view.TitleBar
import com.example.babbogi.util.NutritionState
import com.example.babbogi.util.Product
import com.example.babbogi.util.testNutritionState
import com.example.babbogi.util.testProductList
import java.time.LocalDate

@Composable
fun NutritionDailyAnalyzeScreen(viewModel: ViewModel, navController: NavController) {

}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NutritionDailyAnalyze(
    today: LocalDate,
    nutritionState: NutritionState,
    foodList: List<Pair<Product, Int>>?,
    report: String,
    onDateBarClicked: () -> Unit,
    onDateChanged: (Int) -> Unit,
    onNutritionCardClicked: () -> Unit,
    onRefresh: () -> Unit,
) {
    val refreshState = rememberPullToRefreshState()
    if (refreshState.isRefreshing) {
        LaunchedEffect(true) {
            onRefresh()
            refreshState.endRefresh()
        }
    }

    Box(modifier = Modifier.nestedScroll(refreshState.nestedScrollConnection)) {
        Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
            TitleBar(title = "일일 분석")
            ColumnWithDefault {
                DateSelector(
                    today = today,
                    onDateBarClicked = onDateBarClicked,
                    onDateChanged = onDateChanged,
                )
                NutritionAbstraction(
                    nutritionState = nutritionState,
                    onClick = onNutritionCardClicked,
                )
                GptAnalyzeReport(
                    title = "일일 레포트",
                    report = report,
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
fun DateSelector(
    today: LocalDate,
    onDateBarClicked: () -> Unit,
    onDateChanged: (Int) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = { onDateChanged(-1) }) {
            Icon(
                painter = painterResource(id = R.drawable.ic_chevron_left_24),
                contentDescription = "이전",
            )
        }
        ElevatedCardWithDefault(
            onClick = onDateBarClicked,
            modifier = Modifier.width(250.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Text(
                    text = today.toString(),
                    color = Color.DarkGray,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Normal,
                    textAlign = TextAlign.Center
                )
            }
        }
        IconButton(onClick = { onDateChanged(1) }) {
            Icon(
                painter = painterResource(id = R.drawable.ic_chevron_right_24),
                contentDescription = "다음"
            )
        }
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
        else if (foodList.isEmpty())
            Text("섭취한 음식이 없어요!", color = Color.Gray, modifier = Modifier.padding(16.dp))
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
    Scaffold(
        bottomBar = { PreviewCustomNavigationBar() }
    ) {
        Box(modifier = Modifier.padding(it)) {
            NutritionDailyAnalyze(
                today = LocalDate.now(),
                nutritionState = testNutritionState,
                foodList = testProductList,
                report = "이것은 챗지피티가 생성한 일일 레포트입니다.".repeat(100),
                onDateBarClicked = {},
                onDateChanged = {},
                onNutritionCardClicked = {},
                onRefresh = {},
            )
        }
    }
}