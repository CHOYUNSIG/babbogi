package com.example.babbogi.ui

import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import com.example.babbogi.R
import com.example.babbogi.Screen
import com.example.babbogi.model.BabbogiViewModel
import com.example.babbogi.ui.theme.BabbogiGreen
import com.example.babbogi.ui.view.ColumnScreen
import com.example.babbogi.ui.view.DropDown
import com.example.babbogi.ui.view.FixedColorFloatingIconButton
import com.example.babbogi.ui.view.InputHolder
import com.example.babbogi.ui.view.ScreenPreviewer
import com.example.babbogi.ui.view.TextInputHolder
import com.example.babbogi.util.AdultDisease
import com.example.babbogi.util.Gender
import com.example.babbogi.util.HealthState
import com.example.babbogi.util.testHealthState

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HealthProfileScreen(
    viewModel: BabbogiViewModel,
    navController: NavController,
    showSnackBar: (message: String) -> Unit,
    showAlertPopup: (title: String, message: String, icon: Int) -> Unit,
) {
    HealthProfile(
        healthState = viewModel.healthState,
        onModifyClicked = {
            navController.navigate(Screen.Loading.name)
            viewModel.changeHealthState(it) { success ->
                if (success) {
                    showSnackBar("건강 정보가 수정되었습니다.")
                    navController.navigate(Screen.NutritionDailyAnalyze.name) {
                        popUpTo(navController.graph.startDestinationId) { inclusive = false }
                    }
                }
                else {
                    navController.popBackStack()
                    showAlertPopup(
                        "오류",
                        "건강 정보를 서버로 전송하지 못했습니다.",
                        R.drawable.baseline_cancel_24,
                    )
                }
            }
        },
    )
}

@Composable
private fun Selector(
    options: List<String>,
    initIndex: Int?,
    onChange: (index: Int?) -> Unit,
    isError: Boolean = false,
) {
    var selectedIndex by remember(initIndex) { mutableStateOf(initIndex) }

    repeat(2) { turn ->
        Box(modifier = Modifier.zIndex(turn.toFloat())) {
            Row(modifier = Modifier.fillMaxWidth()) {
                options.forEachIndexed { index, option ->
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .clickable {
                                selectedIndex = if (index == selectedIndex) null else index
                                onChange(selectedIndex)
                            }
                            .height(45.dp)
                            .weight(1f)
                    ) option@ {
                        if ((turn == 0 && selectedIndex == index) || (turn == 1 && selectedIndex != index))
                            return@option
                        val color =
                            if (selectedIndex == index) BabbogiGreen
                            else if (isError) Color.Red
                            else MaterialTheme.colorScheme.onPrimary
                        Canvas(modifier = Modifier.matchParentSize()) {
                            drawRect(
                                color = color,
                                topLeft = Offset.Zero,
                                size = Size(size.width, size.height),
                                style = Stroke(width = 5f),
                            )
                        }
                        Text(text = option, fontSize = 20.sp, color = color)
                    }
                }
            }
        }
    }
}

@Composable
private fun HealthProfile(
    healthState: HealthState?,
    onModifyClicked: (HealthState) -> Unit,
) {
    var heightText by remember { mutableStateOf(healthState?.height?.toString() ?: "") }
    var weightText by remember { mutableStateOf(healthState?.weight?.toString() ?: "") }
    var ageText by remember { mutableStateOf(healthState?.age?.toString() ?: "") }
    var gender by remember { mutableStateOf(healthState?.gender) }
    var adultDisease by remember { mutableStateOf(healthState?.adultDisease) }

    var heightError by remember { mutableStateOf(false) }
    var weightError by remember { mutableStateOf(false) }
    var ageError by remember { mutableStateOf(false) }
    var genderError by remember { mutableStateOf(false) }

    ColumnScreen {
        TextInputHolder(
            content = "키",
            value = heightText,
            onValueChange = { heightText = it },
            labeling = "키를 입력하세요",
            keyboardType = KeyboardType.Number,
            unit = "cm",
            isError = heightError,
        )
        TextInputHolder(
            content = "몸무게",
            value = weightText,
            onValueChange = { weightText = it },
            labeling = "몸무게를 입력하세요",
            keyboardType = KeyboardType.Number,
            unit = "kg",
            isError = weightError,
        )
        TextInputHolder(
            content = "나이",
            value = ageText,
            onValueChange = { ageText = it },
            labeling = "나이를 입력하세요",
            keyboardType = KeyboardType.Number,
            unit = "세",
            isError = ageError,
        )
        InputHolder("성별") {
            Selector(
                options = remember { Gender.entries.map { it.toString() }.toList() },
                initIndex = gender?.ordinal,
                onChange = { gender = it?.let { Gender.entries[it] }; genderError = false },
                isError = genderError,
            )
        }
        InputHolder("성인병") {
            DropDown(
                options = remember { AdultDisease.entries.map { it.toString() }.toList() },
                nullOption = "없음",
                index = adultDisease?.ordinal,
                onChange = { adultDisease = if (it == null) null else AdultDisease.entries[it] }
            )
        }
    }

    Box(
        contentAlignment = Alignment.BottomEnd,
        modifier = Modifier
            .fillMaxSize()
            .padding(50.dp)
    ) {
        FixedColorFloatingIconButton(
            onClick = {
                val height = heightText.toFloatOrNull()
                val weight = weightText.toFloatOrNull()
                val age = ageText.toIntOrNull()
                val g = gender

                heightError = height == null
                weightError = weight == null
                ageError = age == null
                genderError = g == null

                if (height != null && weight != null && age != null && g != null) onModifyClicked(
                    HealthState(
                        height = height,
                        weight = weight,
                        gender = g,
                        age = age,
                        adultDisease = adultDisease
                    )
                )
            },
            icon = R.drawable.baseline_send_24
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(uiMode = UI_MODE_NIGHT_NO)
@Preview(uiMode = UI_MODE_NIGHT_YES)
@Composable
fun PreviewHealthProfile() {
    ScreenPreviewer(screen = Screen.HealthProfile) {
        HealthProfile(
            healthState = testHealthState,
            onModifyClicked = {},
        )
    }
}
