package com.example.babbogi.ui

import android.content.res.Configuration
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CardDefaults
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.babbogi.R
import com.example.babbogi.Screen
import com.example.babbogi.model.BabbogiViewModel
import com.example.babbogi.ui.theme.BabbogiGreen
import com.example.babbogi.ui.theme.BabbogiTypography
import com.example.babbogi.ui.view.ColumnScreen
import com.example.babbogi.ui.view.CustomPopup
import com.example.babbogi.ui.view.FloatingContainer
import com.example.babbogi.ui.view.LinearWeightHistoryGraph
import com.example.babbogi.ui.view.ScreenPreviewer
import com.example.babbogi.ui.view.TextInputHolder
import com.example.babbogi.util.WeightHistory
import com.example.babbogi.util.testWeightHistoryList
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import kotlin.math.pow

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun WeightHistoryManagementScreen(
    viewModel: BabbogiViewModel,
    navController: NavHostController,
    showSnackBar: (message: String) -> Unit,
    showAlertPopup: (title: String, message: String, icon: Int) -> Unit,
) {
    WeightHistoryManagement(
        history = viewModel.weightHistory,
        height = viewModel.healthState?.height,
        onStarted = { onEnded -> viewModel.getWeightHistory { onEnded() } },
        onWeightHistoryChanged = { id, weight ->
            viewModel.changeWeightHistory(id, weight) {
                if (it) showSnackBar("몸무게 기록이 변경되었습니다.")
                else showAlertPopup(
                    "오류",
                    "몸무게 기록을 변경하는 데 실패했습니다.",
                    R.drawable.baseline_cancel_24,
                )
            }
        },
        onWeightHistoryDeleted = { id ->
            viewModel.deleteWeightHistory(id) {
                if (it) showSnackBar("몸무게 기록이 삭제되었습니다.")
                else showAlertPopup(
                    "오류",
                    "몸무게 기록을 삭제하는 데 실패했습니다.",
                    R.drawable.baseline_cancel_24,
                )
            }
        },
        onRefresh = { onLoaded ->
            viewModel.getWeightHistory(refresh = true) {
                if (it == null) showAlertPopup(
                    "오류",
                    "몸무게 기록을 불러오는 데 실패했습니다.",
                    R.drawable.baseline_cancel_24,
                )
                onLoaded()
            }
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun WeightHistoryManagement(
    history: List<WeightHistory>?,
    height: Float?,
    onStarted: (onEnded: () -> Unit) -> Unit,
    onWeightHistoryChanged: (id: Long, weight: Float) -> Unit,
    onWeightHistoryDeleted: (id: Long) -> Unit,
    onRefresh: (onLoaded: () -> Unit) -> Unit,
) {
    val scope = rememberCoroutineScope()
    val lazyListState = rememberLazyListState()
    var selectedIndex by remember { mutableStateOf<Int?>(null) }
    LaunchedEffect(selectedIndex) {
        scope.launch {
            selectedIndex?.let { lazyListState.animateScrollToItem(it) }
        }
    }

    var isLoading by remember { mutableStateOf(false) }
    val refreshState = rememberPullToRefreshState()
    if (refreshState.isRefreshing) LaunchedEffect(true) {
        isLoading = true
        onRefresh {
            isLoading = false
            refreshState.endRefresh()
        }
    }

    LaunchedEffect(true) {
        isLoading = true
        onStarted { isLoading = false }
    }

    var showModifyPopup by remember { mutableStateOf(false) }
    var showDeletePopup by remember { mutableStateOf(false) }

    Box(modifier = Modifier.nestedScroll(refreshState.nestedScrollConnection)) {
        ColumnScreen {
            // 몸무게 변화 그래프 카드
            FloatingContainer {
                Row(modifier = Modifier.fillMaxWidth()) {
                    Text(text = "몸무게 변화", style = BabbogiTypography.titleMedium)
                }
                // 로딩중임을 고지
                if (isLoading) Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                ) {
                    CircularProgressIndicator(modifier = Modifier.size(50.dp))
                }
                // 데이터를 표현
                else if (history != null && height != null) {
                    val unit = (height / 100).pow(2)
                    val topLimit = unit * 23.0f
                    val bottomLimit = unit * 18.5f

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp)
                    ) {
                        LinearWeightHistoryGraph(
                            history = history,
                            selectedHistoryID = selectedIndex?.let { history[it].id },
                            bottomLimit = bottomLimit,
                            topLimit = topLimit,
                            onWeightAnkerClicked = { selectedIndex = history.indexOf(it) },
                        )
                    }
                    Column(
                        verticalArrangement = Arrangement.spacedBy(3.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = remember(height, bottomLimit, topLimit) {
                                buildAnnotatedString {
                                    val bold = SpanStyle(fontWeight = FontWeight.Bold)
                                    append("신체질량지수(BMI) 기준,\n키 ")
                                    withStyle(bold) { append("%.1fcm".format(height)) }
                                    append("의 적정 몸무게는\n")
                                    withStyle(bold) { append("%.1fkg".format(bottomLimit)) }
                                    append(" 이상 ")
                                    withStyle(bold) { append("%.1fkg".format(topLimit)) }
                                    append(" 이하입니다.")
                                }
                            },
                            textAlign = TextAlign.Center
                        )
                    }
                }
                // 데이터를 불러오지 못함을 고지
                else Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                ) {
                    Text(text = "정보를 불러오는 데 실패했습니다.", style = BabbogiTypography.bodySmall)
                }
            }
            // 몸무게 기록 정보 리스트
            FloatingContainer {
                Row(modifier = Modifier.fillMaxWidth()) {
                    Text(text = "상세 기록", style = BabbogiTypography.titleMedium)
                }
                if (history != null) {
                    if (history.size > 1) Text(
                        text = "몸무게 그래프의 각 점을 클릭하면\n자동으로 해당 정보가 강조 표시됩니다.",
                        style = BabbogiTypography.bodySmall,
                    )
                    LazyColumn(
                        state = lazyListState,
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.heightIn(max = 500.dp)
                    ) {
                        items(count = history.size, key = { history[it].id }) { index ->
                            val w = history[index]
                            FloatingContainer(
                                modifier = if (index == selectedIndex) Modifier.border(
                                    width = 2.dp,
                                    brush = Brush.verticalGradient(
                                        0.1f to Color.Gray,
                                        0.9f to BabbogiGreen,
                                    ),
                                    shape = CardDefaults.elevatedShape,
                                )
                                else Modifier,
                                onClick = { selectedIndex = index },
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column {
                                        Text(text = getDateString(w.date), fontSize = 12.sp)
                                        Text(text = "%.1fkg".format(w.weight))
                                    }
                                    Row {
                                        IconButton(
                                            onClick = {
                                                selectedIndex = index
                                                showModifyPopup = true
                                            }
                                        ) {
                                            Icon(
                                                painter = painterResource(id = R.drawable.baseline_edit_24),
                                                contentDescription = "기록 변경",
                                            )
                                        }
                                        IconButton(
                                            onClick = {
                                                selectedIndex = index
                                                showDeletePopup = true
                                            },
                                            enabled = history.size > 1,
                                        ) {
                                            Icon(
                                                painter = painterResource(id = R.drawable.baseline_delete_24),
                                                contentDescription = "기록 삭제",
                                            )
                                        }
                                    }
                                }
                            }
                        }
                        item { Spacer(modifier = Modifier) }
                    }
                }
                else {
                    Text(
                        text = "몸무게 기록이 생길 경우 이곳에 표시됩니다.\n설정에서 변화하는 몸무게 정보를 기록해보세요.",
                        style = BabbogiTypography.bodySmall,
                    )
                }
            }
        }

        // 새로고침 로딩 아이콘
        PullToRefreshContainer(
            state = refreshState,
            modifier = Modifier.align(Alignment.TopCenter)
        )
    }

    selectedIndex?.let { index ->
        if (history == null || index > history.lastIndex) return@let
        val w = history[index]

        if (showModifyPopup) {
            var text by remember { mutableStateOf("%.1f".format(w.weight)) }
            var error by remember { mutableStateOf(false) }
            CustomPopup(
                callbacks = listOf(
                    {
                        val weight = text.toFloatOrNull()
                        if (weight != null) {
                            onWeightHistoryChanged(w.id, weight)
                            showModifyPopup = false
                        }
                        else error = true
                    },
                    { showModifyPopup = false }
                ),
                labels = listOf("확인", "취소"),
                onDismiss = { showModifyPopup = false },
                icon = R.drawable.baseline_edit_24,
                title = "몸무게 수정",
                enableDismissOnCallbackEnded = false,
            ) {
                Text(text = getDateString(w.date), fontSize = 12.sp,)
                TextInputHolder(
                    content = "몸무게",
                    value = text,
                    onValueChange = { text = it },
                    labeling = "몸무게를 입력하세요",
                    keyboardType = KeyboardType.Number,
                    unit = "kg",
                    isError = error,
                )
            }
        }

        if (showDeletePopup) CustomPopup(
            callbacks = listOf({ onWeightHistoryDeleted(w.id) }, {}),
            labels = listOf("확인", "취소"),
            onDismiss = { showDeletePopup = false; selectedIndex = null },
            icon = R.drawable.baseline_delete_24,
            title = "기록 삭제",
        ) {
            Text(text = "다음 기록을 삭제하시겠습니까?")
            FloatingContainer {
                Text(text = getDateString(w.date), fontSize = 12.sp)
                Text(text = "%.1fkg".format(w.weight))
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
private fun getDateString(date: LocalDateTime): String = "%d년 %02d/%02d ${
    if (date.hour < 12) "오전" else "오후"
} %02d:%02d".format(
    date.year,
    date.month.value,
    date.dayOfMonth,
    (date.hour + 11) % 12 + 1,
    date.minute,
)

@RequiresApi(Build.VERSION_CODES.O)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PreviewWeightHistoryManagement() {
    ScreenPreviewer(screen = Screen.WeightHistoryManagement) {
        WeightHistoryManagement(
            history = testWeightHistoryList,
            height = 180f,
            onStarted = { it() },
            onWeightHistoryChanged = { _, _ -> },
            onWeightHistoryDeleted = {},
            onRefresh = {}
        )
    }
}
