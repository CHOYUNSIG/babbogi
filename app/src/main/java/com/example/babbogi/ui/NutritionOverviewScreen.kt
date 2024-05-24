package com.example.babbogi.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.babbogi.ui.model.BabbogiViewModel
import com.example.babbogi.ui.view.TitleBar
import com.example.babbogi.util.NutritionState
import com.example.babbogi.util.testNutritionState
import com.example.babbogi.util.nutrition
import com.example.babbogi.util.nutritionNameResource

@Composable
fun NutritionOverviewScreen(viewModel: BabbogiViewModel, navController: NavController) {
    NutritionOverview()
}

@Composable
//영양성분들 원그래프 만드는 함수
fun CircularGraphCard(percentage: Float, color: Color, name: String) {
    ElevatedCard(
        modifier = Modifier
            .padding(16.dp)
            .background(color = Color.White),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 5.dp)
    ) {
        Box(modifier = Modifier
            .fillMaxWidth()
            .background(color = Color(0xFFE5E5E5))
        ) {
            Box(modifier = Modifier
                .fillMaxWidth()
                .background(color = Color(0xFFE5E5E5))
            ) {
                Row {
                    Canvas(
                        modifier = Modifier
                            .size(100.dp)
                            .padding(16.dp)
                    ) {
                        drawArc(
                            color = color,
                            startAngle = -90f,
                            sweepAngle = percentage * 360,
                            useCenter = false,
                            size = Size(width = size.width, height = size.width),
                            style = Stroke(width = 8.dp.toPx())
                        )
                    }
                    Text(text = name)
                }
            }
        }
    }
}

@Preview
@Composable
fun NutritionOverview(
    nutritionState: NutritionState = testNutritionState
) {
    Column(modifier = Modifier.background(color = Color.White)) {
        TitleBar("영양 정보")
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(color = Color.White)
        ) {
            items(9) { index ->
                val nutritionInfo = nutritionState[index]
                CircularGraphCard(
                    percentage = nutritionInfo.getPercentage(),
                    color = Color(0xFF7FE26E),
                    name = stringResource(nutritionNameResource[nutrition[index]]!!)
                )
            }
        }
    }
}