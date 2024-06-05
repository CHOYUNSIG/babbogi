package com.example.babbogi.ui

import android.Manifest
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ElevatedCard
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.nestedscroll.nestedScroll
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
import com.example.babbogi.ui.model.DataPreference
import com.example.babbogi.ui.view.NutritionBarGraph
import com.example.babbogi.ui.view.NutritionCircularGraph
import com.example.babbogi.ui.view.TitleBar
import com.example.babbogi.util.HealthState
import com.example.babbogi.util.Nutrition
import com.example.babbogi.util.NutritionState
import com.example.babbogi.util.Product
import com.example.babbogi.util.testHealthState
import com.example.babbogi.util.testNutritionState
import com.example.babbogi.util.testProductList
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import java.time.LocalDate


@OptIn(ExperimentalPermissionsApi::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeScreen(viewModel: BabbogiViewModel, navController: NavController) {
    var today by remember { mutableStateOf(LocalDate.now()) }
    LaunchedEffect(key1 = null) { viewModel.asyncGetFoodListFromServer(today) }

    // 튜토리얼 페이지
    if (!DataPreference.isTutorialComplete())
        navController.navigate(Screen.Tutorial.name)

    // 알림 권한 설정
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        val notificationPermission = rememberPermissionState(Manifest.permission.POST_NOTIFICATIONS)
        if (!notificationPermission.hasPermission)
            LaunchedEffect(key1 = null) { notificationPermission.launchPermissionRequest() }
    }

    Home(
        today = today,
        healthState = viewModel.healthState,
        nutritionState = viewModel.nutritionState,
        foodList = viewModel.dailyFoodList[today],
        onRefresh = {
            viewModel.asyncGetHealthStateFromServer()
            viewModel.asyncGetNutritionStateFromServer()
        },
        onDateChanged = {
            today = today.plusDays(it.toLong())
            if (!viewModel.dailyFoodList.containsKey(today))
                viewModel.asyncGetFoodListFromServer(today)
        },
        onNutritionCardClicked = { navController.navigate(Screen.NutritionOverview.name) },
        onHealthCardClicked = { navController.navigate(Screen.HealthProfile.name) },
        onInputUserDataClicked = { navController.navigate(Screen.HealthProfile.name) },
        onEnrollClicked = {
            if (viewModel.healthState == null)
                navController.navigate(Screen.HealthProfile.name)
            else
                navController.navigate(Screen.Camera.name)
        }
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

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NutritionAbstraction(nutritionState: NutritionState, onClick: () -> Unit) {
    Box(modifier = Modifier.padding(16.dp)) {
        ElevatedCard(
            onClick = onClick,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "오늘(${LocalDate.now()})의 영양 상태",
                    modifier = Modifier.padding(16.dp),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                )
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = stringResource(id = Nutrition.Calorie.res))
                    NutritionBarGraph(nutrition = Nutrition.Calorie, intake = nutritionState[Nutrition.Calorie])
                }
                Row(modifier = Modifier.padding(16.dp)) {
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
}

@Composable
fun HealthAbstraction(
    healthState: HealthState,
    onClick: () -> Unit
) {
    Box(modifier = Modifier.padding(16.dp)) {
        ElevatedCard {
            Column {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp, horizontal = 16.dp)
                ) {
                    Text(
                        text = "사용자 건강 정보",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.W600,
                    )
                    IconButton(onClick = onClick) {
                        Icon(
                            modifier = Modifier.size(30.dp),
                            painter = painterResource(id = R.drawable.baseline_mode_24),
                            contentDescription = "정보 수정하기 아이콘"
                        )
                    }
                }
                Column(modifier = Modifier.padding(16.dp)) {
                    listOf(
                        listOf("키", healthState.height.toString(), "cm"),
                        listOf("몸무게", healthState.weight.toString(), "kg"),
                        listOf("나이", healthState.age.toString(), "세"),
                        listOf("성별", healthState.gender.toString(), ""),
                        listOf("성인병", healthState.adultDisease?.toString() ?: "없음", ""),
                    ).forEach { (attribute, value, unit) ->
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(text = attribute)
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = value)
                                Text(text = unit)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MealList(
    foodList: List<Pair<Product, Int>>?,
    onEnrollClicked: () -> Unit
) {
    ElevatedCard(modifier = Modifier.padding(16.dp)) {
        Column {
            Box(
                contentAlignment = Alignment.TopStart,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp, horizontal = 16.dp),
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
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(500.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                if (foodList == null)
                    Row (
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        CircularProgressIndicator(modifier = Modifier
                            .size(50.dp)
                            .padding(16.dp))
                    }
                else if (foodList.isEmpty())
                    Text("섭취한 음식이 없어요!", color = Color.Gray, modifier = Modifier.padding(16.dp))
                else
                    foodList.forEach { (product, amount) ->
                        Row(modifier = Modifier.padding(16.dp)) {
                            ElevatedCard(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 16.dp),
                                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 5.dp),
                            ) {
                                Column (
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(8.dp)
                                ) {
                                    Column (
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp)
                                    ) {
                                        Row(
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Text(
                                                text = product.name,
                                                fontSize = 16.sp,
                                                fontWeight = FontWeight.Bold,
                                            )
                                            Text(
                                                text = "x$amount",
                                                fontSize = 20.sp,
                                            )
                                        }
                                        Text(
                                            text = product.nutrition?.toString(List(Nutrition.entries.size) { stringResource(id = Nutrition.entries[it].res) }) ?: "",
                                            modifier = Modifier.padding(vertical = 8.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Preview
@Composable
fun Home(
    today: LocalDate = LocalDate.now(),
    healthState: HealthState? = testHealthState,
    nutritionState: NutritionState = testNutritionState,
    foodList: List<Pair<Product, Int>>? = testProductList,
    onRefresh: () -> Unit = {},
    onNutritionCardClicked: () -> Unit = {},
    onHealthCardClicked: () -> Unit = {},
    onDateChanged: (Int) -> Unit = {},
    onInputUserDataClicked: () -> Unit = {},
    onEnrollClicked: () -> Unit = {}
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
            TitleBar(stringResource(id = R.string.app_name))
            if (healthState == null)
                InputUserData(onInputUserDataClicked)
            else {
                NutritionAbstraction(nutritionState, onNutritionCardClicked)
                HealthAbstraction(healthState, onHealthCardClicked)
            }
            DateSelector(today, onDateChanged)
            MealList(if (healthState == null) emptyList() else foodList, onEnrollClicked)
        }
        PullToRefreshContainer(
            state = refreshState,
            modifier = Modifier.align(Alignment.TopCenter)
        )
    }
}