package com.example.babbogi.ui

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class NutritionInfo(val name: String, val color: Color, val percentage: Float)

val nutritionInfos: List<NutritionInfo> = listOf(
    NutritionInfo("단백질", Color(0xFFB71C1C), 120.0F), // 20%를 예로 듭니다
    NutritionInfo("탄수화물", Color(0xFF1B5E20), 180.0F), // 50%를 예로 듭니다
    NutritionInfo("지방", Color(0xFF0D47A1), 105.0F), // 25%를 예로 듭니다
    NutritionInfo("포화지방", Color(0xFFF57F17), 75.0F), // 5%를 예로 듭니다
    NutritionInfo("트랜스지방", Color(0xFF4A148C), 90.5F), // 0.5%를 예로 듭니다
    NutritionInfo("당류", Color(0xFF880E4F), 155.0F), // 15%를 예로 듭니다
    NutritionInfo("나트륨", Color(0xFF004D40), 110.0F), // 10%를 예로 듭니다
    NutritionInfo("콜레스테롤", Color(0xFF3E2723), 82.0F) // 2%를 예로 듭니다
)

@RequiresApi(Build.VERSION_CODES.O)
@Preview
@Composable
fun NutritionOverview() {
    Column(modifier = Modifier.background(color = Color.White)) {
        //맨 위 앱 이름
        Box(
            modifier = Modifier
            .fillMaxWidth()
            .background(color = Color.White)
        ) {
            Text(
                "밥보기",
                color = Color.DarkGray,
                fontSize = 26.sp,
                fontWeight = FontWeight.Black,
            )
        }

        //CircularGraphCard들을 넣을 박스
        LazyColumn(
            modifier = Modifier
            .fillMaxSize()
            .background(color = Color.White)
        ) {
            items(nutritionInfos.size) { index ->
                val nutritionInfo = nutritionInfos[index]
                CircularGraphCard(
                    percentage = nutritionInfo.percentage,
                    color = nutritionInfo.color,
                    name = nutritionInfo.name
                )
            }
        }
    }
}

@SuppressLint("RememberReturnType")
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
            .background(color = Color(0xE5E5E5))
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
                        sweepAngle = percentage,
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

