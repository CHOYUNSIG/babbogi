package com.example.babbogi.ui

import android.Manifest
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavController
import com.example.babbogi.R
import com.example.babbogi.Screen
import com.example.babbogi.model.BabbogiViewModel
import com.example.babbogi.model.DataPreference
import com.example.babbogi.ui.view.Calendar
import com.example.babbogi.ui.view.HealthAbstraction
import com.example.babbogi.ui.view.NutritionAbstraction
import com.example.babbogi.ui.view.TitleBar
import com.example.babbogi.util.HealthState
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
    var showDatePopup by remember { mutableStateOf(false) }
    LaunchedEffect(key1 = null) { viewModel.asyncGetFoodListFromServer(today) }

    /*
    // 튜토리얼 페이지
    if (!DataPreference.isTutorialComplete())
        navController.navigate(Screen.Tutorial.name)

    // 알림 권한 설정
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        val notificationPermission = rememberPermissionState(Manifest.permission.POST_NOTIFICATIONS)
        if (!notificationPermission.hasPermission)
            LaunchedEffect(key1 = null) { notificationPermission.launchPermissionRequest() }
    }
    */

    Home(
        today = today,
        showDatePopup = showDatePopup,
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
        },
        onDateBarClicked = { showDatePopup = true },
        onDateSelected = {
            today = it
            if (!viewModel.dailyFoodList.containsKey(today))
                viewModel.asyncGetFoodListFromServer(today)
            showDatePopup = false
        }
    )
}

@Composable
fun InputUserData(onInputUserDataClicked: () -> Unit) {
    Box {
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



@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Home(
    today: LocalDate,
    showDatePopup: Boolean,
    healthState: HealthState?,
    nutritionState: NutritionState,
    foodList: List<Pair<Product, Int>>?,
    onRefresh: () -> Unit,  // 아래로 당겨 새로고침될 시
    onNutritionCardClicked: () -> Unit, // 영양 상태 카드 클릭 시
    onHealthCardClicked: () -> Unit,  // 건강 정보 카드 클릭 시
    onDateChanged: (Int) -> Unit,  // 화살표 버튼으로 날짜 변경 시
    onDateBarClicked: () -> Unit,  // 날짜바 클릭 시
    onDateSelected: (LocalDate) -> Unit,  // 날짜바 클릭 후 표시된 달력에서 날짜 선택 시
    onInputUserDataClicked: () -> Unit,  // 사용자 건강 정보 입력창 클릭 시
    onEnrollClicked: () -> Unit,  // 사용자 식사 정보 추가 버튼 클릭 시
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
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
            ) {
                if (healthState == null)
                    InputUserData(onInputUserDataClicked)
                else {
                    NutritionAbstraction(nutritionState, onNutritionCardClicked)
                    HealthAbstraction(healthState, onHealthCardClicked)
                    DateSelector(
                        today = today,
                        onDateBarClicked = onDateBarClicked,
                        onDateChanged = onDateChanged,
                    )
                    MealList(foodList)
                }
            }
        }
        PullToRefreshContainer(
            state = refreshState,
            modifier = Modifier.align(Alignment.TopCenter)
        )
    }

    if (showDatePopup) {
        Dialog(
            onDismissRequest = {},
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Box(modifier = Modifier.padding(16.dp)) {
                Calendar(onSubmit = { onDateSelected(it) })
            }
        }
    }
}

    @Preview
@Composable
fun PreviewHome() {
    var today by remember { mutableStateOf(LocalDate.now()) }
    var showDatePopup by remember { mutableStateOf(false) }

    Home(
        today = today,
        showDatePopup = showDatePopup,
        healthState = testHealthState,
        nutritionState = testNutritionState,
        foodList = testProductList,
        onRefresh = {},
        onNutritionCardClicked = {},
        onHealthCardClicked = {},
        onDateChanged = {},
        onInputUserDataClicked = {},
        onEnrollClicked = {},
        onDateBarClicked = { showDatePopup = true },
        onDateSelected = {
            today = it
            showDatePopup = false
        },
    )
}