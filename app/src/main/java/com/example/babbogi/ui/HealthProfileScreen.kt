package com.example.babbogi.ui

import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.babbogi.R
import com.example.babbogi.Screen
import com.example.babbogi.model.BabbogiViewModel
import com.example.babbogi.ui.theme.BabbogiGreen
import com.example.babbogi.ui.theme.BabbogiTheme
import com.example.babbogi.ui.view.ColumnWithDefault
import com.example.babbogi.ui.view.CustomIconButton
import com.example.babbogi.ui.view.DropDown
import com.example.babbogi.ui.view.InputHolder
import com.example.babbogi.ui.view.PreviewCustomNavigationBar
import com.example.babbogi.ui.view.TextInputHolder
import com.example.babbogi.ui.view.TitleBar
import com.example.babbogi.util.AdultDisease
import com.example.babbogi.util.Gender
import com.example.babbogi.util.HealthState
import com.example.babbogi.util.testHealthState
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HealthProfileScreen(
    viewModel: BabbogiViewModel,
    navController: NavController,
    showSnackBar: (message: String, actionLabel: String, duration: SnackbarDuration) -> Unit
) {
    HealthProfile(
        healthState = viewModel.healthState,
        onModifyClicked = {
            navController.navigate(Screen.Loading.name)
            viewModel.changeHealthState(it) { success ->
                if (success) navController.navigate(Screen.NutritionDailyAnalyze.name) {
                    popUpTo(navController.graph.startDestinationId) { inclusive = false }
                }
                else navController.popBackStack()
                showSnackBar(
                    if (success) "건강 정보가 수정되었습니다." else "오류: 건강 정보를 서버로 전송하지 못했습니다.",
                    "확인",
                    SnackbarDuration.Short
                )
            }
        },
    )
}

@Composable
fun Selector(
    options: List<String>,
    index: Int?,
    onChange: (index: Int) -> Unit
) {
    var selected: String? by remember { mutableStateOf(if (index != null) options[index] else null) }

    Row(modifier = Modifier.fillMaxWidth()) {
        options.forEachIndexed { index, gender ->
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .clickable {
                        selected = gender
                        onChange(index)
                    }
                    .height(45.dp)
                    .weight(1f)
            ) {
                val color = MaterialTheme.colorScheme.onPrimary
                Canvas(modifier = Modifier.matchParentSize()) {
                    drawRoundRect(
                        color = if (selected == gender) BabbogiGreen else color,
                        topLeft = Offset.Zero,
                        size = Size(size.width, size.height),
                        cornerRadius = CornerRadius(0f, 0f),
                        style = Stroke(width = 5f),
                    )
                }
                Text(
                    text = gender,
                    fontSize = 20.sp,
                    color = if (selected == gender) BabbogiGreen else color,
                    fontWeight = FontWeight.Normal
                )
            }
        }
    }
}

@Composable
fun HealthProfile(
    healthState: HealthState?,
    onModifyClicked: (HealthState) -> Unit,
) {
    var heightText by remember { mutableStateOf(healthState?.height?.toString() ?: "") }
    var weightText by remember { mutableStateOf(healthState?.weight?.toString() ?: "") }
    var ageText by remember { mutableStateOf(healthState?.age?.toString() ?: "") }
    var gender by remember { mutableStateOf(healthState?.gender) }
    var adultDisease by remember { mutableStateOf(healthState?.adultDisease) }

    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
        TitleBar("건강 정보")
        ColumnWithDefault {
            TextInputHolder(
                content = "키",
                value = heightText,
                onValueChange = { heightText = it },
                labeling = "본인의 키를 입력하세요",
                keyboardType = KeyboardType.Number,
            )
            TextInputHolder(
                content = "몸무게",
                value = weightText,
                onValueChange = { weightText = it },
                labeling = "본인의 몸무게를 입력하세요",
                keyboardType = KeyboardType.Number,
            )
            TextInputHolder(
                content = "나이",
                value = ageText,
                onValueChange = { ageText = it },
                labeling = "본인의 나이를 입력하세요",
                keyboardType = KeyboardType.Number,
            )
            InputHolder("성별") {
                Selector(
                    options = Gender.entries.map { it.toString() }.toList(),
                    index = gender?.ordinal,
                    onChange = { gender = Gender.entries[it] }
                )
            }
            InputHolder("성인병") {
                DropDown(
                    options = AdultDisease.entries.map { it.toString() }.toList(),
                    nullOption = "없음",
                    index = adultDisease?.ordinal,
                    onChange = { adultDisease = if (it == null) null else AdultDisease.entries[it] }
                )
            }
        }
    }

    Box(
        contentAlignment = Alignment.BottomEnd,
        modifier = Modifier
            .fillMaxSize()
            .padding(50.dp)
    ) {
        CustomIconButton(
            onClick = lambda@ {
                try {
                    onModifyClicked(
                        HealthState(
                            height = heightText.toFloat(),
                            weight = weightText.toFloat(),
                            gender = gender!!,
                            age = ageText.toInt(),
                            adultDisease = adultDisease
                        )
                    )
                }
                catch (_: Exception) {}
            },
            icon = R.drawable.baseline_send_24
        )
    }
}

@Preview(uiMode = UI_MODE_NIGHT_NO)
@Preview(uiMode = UI_MODE_NIGHT_YES)
@Composable
fun PreviewHealthProfile() {
    BabbogiTheme {
        Scaffold(bottomBar = { PreviewCustomNavigationBar() }) {
            Box(modifier = Modifier.padding(it)) {
                HealthProfile(
                    healthState = testHealthState,
                    onModifyClicked = {},
                )
            }
        }
    }
}
