package com.example.babbogi.ui

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.babbogi.R
import com.example.babbogi.Screen
import com.example.babbogi.ui.model.BabbogiViewModel
import com.example.babbogi.ui.view.NutritionCircularGraph
import com.example.babbogi.ui.view.TitleBar
import com.example.babbogi.util.HealthState
import com.example.babbogi.util.Nutrition
import com.example.babbogi.util.NutritionState
import com.example.babbogi.util.Product
import com.example.babbogi.util.testHealthState
import com.example.babbogi.util.testNutritionState
import java.time.LocalDate


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeScreen(viewModel: BabbogiViewModel, navController: NavController) {
    var today by remember { mutableStateOf(LocalDate.now()) }
    LaunchedEffect(key1 = null) { viewModel.asyncGetFoodListFromServer(today) }

    Home(
        today = today,
        healthState = viewModel.healthState,
        nutritionState = viewModel.nutritionState,
        foodList = viewModel.dailyFoodList,
        onDateChanged = { today = today.plusDays(it.toLong()) },
        onCardClicked = { navController.navigate(Screen.NutritionOverview.name) },
        onInputUserDataClicked = { navController.navigate(Screen.HealthProfile.name) },
        onEnrollClicked = { navController.navigate(Screen.Camera.name) }
    )
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
//날짜 선택
fun DateSelector(
    today: LocalDate,
    onDateChanged: (Int) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,  // 가로 중앙 정렬
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = { onDateChanged(-1) }) {
            Icon(
                painter = painterResource(id = R.drawable.ic_chevron_left_24),
                contentDescription = "이전",
            )
        }
        ElevatedCard(
            modifier = Modifier.width(250.dp),
            colors = CardDefaults.elevatedCardColors(containerColor = Color(0xF7F7F7FF)),
            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 1.dp)
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
// 건강정보 추가하기(메인 화면)
fun InputUserData(onInputUserDataClicked: () -> Unit) {
    Box(modifier = Modifier.padding(16.dp)) {
        Canvas(modifier = Modifier.matchParentSize()) {
            val pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
            drawRoundRect(
                color = Color.Black,
                topLeft = Offset.Zero,
                size = Size(size.width, size.height),
                cornerRadius = CornerRadius(0f, 0f),
                style = Stroke(width = 2.dp.toPx(), pathEffect = pathEffect),
                alpha = 0.5f
            )
        }
        ElevatedButton(
            onClick = onInputUserDataClicked,
            modifier = Modifier.fillMaxWidth(),
            shape = RectangleShape,
            colors = ButtonDefaults.elevatedButtonColors(containerColor = Color.White),
            elevation = ButtonDefaults.elevatedButtonElevation(
                defaultElevation = 3.dp,  // 기본 고도
                pressedElevation = 8.dp,  // 버튼이 눌렸을 때의 고도
                focusedElevation = 6.dp,  // 포커스가 맞춰졌을 때의 고도
                hoveredElevation = 6.dp   // 호버링 했을 때의 고도
            )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = Color(0xEDEFEDFF))
            ) {
                Column {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White)
                    ) {
                        Icon(
                            tint = Color.Green,
                            modifier = Modifier
                                .size(160.dp)
                                .padding(20.dp)
                                .alpha(0.4f),
                            painter = painterResource(id = R.drawable.ic_add_box_24),
                            contentDescription = "정보 추가하기 아이콘"
                        )
                    }
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White)
                    ) {
                        Text(
                            text = "사용자 건강 정보 추가하기",
                            color = Color.Black.copy(alpha = 0.5f),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Normal,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun NutritionAbstraction(nutritionState: NutritionState, onClick: () -> Unit) {
    Box(modifier = Modifier.padding(16.dp)) {
        ElevatedButton(
            onClick = onClick,
            modifier = Modifier.fillMaxWidth(),
            shape = RectangleShape,
            colors = ButtonDefaults.elevatedButtonColors(containerColor = Color.White),
            elevation = ButtonDefaults.elevatedButtonElevation(
                defaultElevation = 3.dp,  // 기본 고도
                pressedElevation = 8.dp,  // 버튼이 눌렸을 때의 고도
                focusedElevation = 6.dp,  // 포커스가 맞춰졌을 때의 고도
                hoveredElevation = 6.dp   // 호버링 했을 때의 고도
            )
        ) {
            Row {
                listOf(Nutrition.Carbohydrate, Nutrition.Protein, Nutrition.Fat).forEach {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = stringResource(id = it.res))
                        NutritionCircularGraph(nutrition = it, intake = nutritionState[it])
                    }
                }
            }
        }
    }
}

@Composable
fun MealList(
    foodList: List<Product>,
    onEnrollClicked: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier
            .padding(16.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = Color(0xF7F7F7FF)),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 1.dp),
    ) {
        Column {
            Box(
                contentAlignment = Alignment.TopStart,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        text = "사용자 식사 정보",
                        color = Color.Black.copy(alpha = 0.5f),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.W600,
                    )
                    IconButton(
                        onClick = onEnrollClicked,
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            tint = Color.Green,
                            modifier = Modifier.size(160.dp),
                            painter = painterResource(id = R.drawable.ic_add_box_24),
                            contentDescription = "정보 추가하기 아이콘"
                        )
                    }
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview
@Composable
fun Home(
    today: LocalDate = LocalDate.now(),
    healthState: HealthState? = testHealthState,
    nutritionState: NutritionState = testNutritionState,
    foodList: List<Product> = emptyList(),
    onCardClicked: () -> Unit = {},
    onDateChanged: (Int) -> Unit = {},
    onInputUserDataClicked: () -> Unit = {},
    onEnrollClicked: () -> Unit = {}
) {
    Column {
        TitleBar(stringResource(id = R.string.app_name))
        DateSelector(today, onDateChanged)
        if (healthState == null)
            InputUserData(onInputUserDataClicked)
        else
            NutritionAbstraction(nutritionState, onCardClicked)
        MealList(foodList, onEnrollClicked)
    }
}