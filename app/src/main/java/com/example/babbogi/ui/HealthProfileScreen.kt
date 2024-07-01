package com.example.babbogi.ui

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.babbogi.R
import com.example.babbogi.Screen
import com.example.babbogi.ui.model.BabbogiViewModel
import com.example.babbogi.ui.view.CustomIconButton
import com.example.babbogi.ui.view.TitleBar
import com.example.babbogi.util.AdultDisease
import com.example.babbogi.util.Gender
import com.example.babbogi.util.HealthState
import com.example.babbogi.util.testHealthState
import com.example.babbogi.util.toFloat2

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HealthProfileScreen(viewModel: BabbogiViewModel, navController: NavController) {
    HealthProfile(
        healthState = viewModel.healthState,
        onModifyClicked = {
            viewModel.asyncChangeHealthStateWithServer(it)
            navController.navigate(Screen.Loading.name)
        },
    )
}

@Composable
fun InputHolder(
    content: String,
    holder: @Composable () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
            .height(80.dp)
    ) {
        Box(modifier = Modifier.width(100.dp)) {
            Text(
                text = content,
                fontSize = 20.sp,
                color = Color.Black,
                fontWeight = FontWeight.Bold,
            )
        }
        Box { holder() }
    }
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
                Canvas(modifier = Modifier.matchParentSize()) {
                    drawRoundRect(
                        color = if (selected == gender) Color(0xFF21A642) else Color(0x20000000),
                        topLeft = Offset.Zero,
                        size = Size(size.width, size.height),
                        cornerRadius = CornerRadius(0f, 0f),
                        style = Stroke(width = 5f),
                    )
                }
                Text(
                    text = gender,
                    fontSize = 20.sp,
                    color = if (selected == gender) Color(0xFF21A642) else Color(0x20000000),
                    fontWeight = FontWeight.Normal
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropDown(
    options: List<String>,
    index: Int?,
    onChange: (index: Int?) -> Unit
) {
    val realOptions = listOf("해당 없음") + options
    var selectedText: String? by remember { mutableStateOf(if (index != null) options[index] else null) }
    var isExpended by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = isExpended,
        onExpandedChange = { isExpended = !isExpended }
    ) {
        TextField(
            modifier = Modifier.menuAnchor(),
            value = selectedText?: realOptions.first(),
            onValueChange = {},
            readOnly = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpended) }
        )
        ExposedDropdownMenu(
            expanded = isExpended,
            onDismissRequest = {isExpended = false},
        ) {
            realOptions.forEachIndexed { index, text ->
                DropdownMenuItem(
                    text = { Text(text = text) },
                    onClick = {
                        selectedText = text
                        isExpended = false
                        if (index > 0)
                            onChange(index - 1)
                        else
                            onChange(null)
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
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

    Column {
        TitleBar("건강 정보")
        InputHolder("키") {
            OutlinedTextField(
                value = heightText,
                onValueChange = { heightText = it },
                label = { Text("본인의 키를 입력하시오") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
                modifier = Modifier.fillMaxWidth(),
                enabled = true,
                textStyle = TextStyle(fontSize = 20.sp, color = Color.Black)
            )
        }
        InputHolder("몸무게") {
            OutlinedTextField(
                value = weightText,
                onValueChange = { weightText = it },
                label = { Text("본인의 몸무게를 입력하시오") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
                modifier = Modifier.fillMaxWidth(),
                enabled = true,
                textStyle = TextStyle(fontSize = 20.sp, color = Color.Black)
            )
        }
        InputHolder("나이") {
            OutlinedTextField(
                value = ageText,
                onValueChange = { ageText = it },
                label = { Text("본인의 나이를 입력하시오") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
                modifier = Modifier.fillMaxWidth(),
                enabled = true,
                textStyle = TextStyle(fontSize = 20.sp, color = Color.Black)
            )
        }
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
                index = adultDisease?.ordinal,
                onChange = {
                    if (it == null) adultDisease = null else adultDisease = AdultDisease.entries[it]
                }
            )
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
                val h = heightText.toFloat2(healthState?.height ?: 0f)
                val w = weightText.toFloat2(healthState?.weight ?: 0f)
                val a = try { ageText.toInt() } catch (e: NumberFormatException) {0}
                val g = gender
                if (g == null || h < 10f || w < 10f || a <= 0) return@lambda
                onModifyClicked(
                    HealthState(
                        height = h,
                        weight = w,
                        gender = g,
                        age = a,
                        adultDisease = adultDisease
                    )
                )
            },
            icon = R.drawable.baseline_send_24
        )
    }
}

@Preview
@Composable
fun PreviewHealthProfile() {
    HealthProfile(
        healthState = testHealthState,
        onModifyClicked = {},
    )
}