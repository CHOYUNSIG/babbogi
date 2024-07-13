package com.example.babbogi.ui

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.example.babbogi.model.BabbogiViewModel
import com.example.babbogi.ui.view.ColumnWithDefault
import com.example.babbogi.ui.view.ElevatedCardWithDefault
import com.example.babbogi.ui.view.NutritionCircularGraph
import com.example.babbogi.ui.view.TitleBar
import com.example.babbogi.util.IntakeState
import com.example.babbogi.util.Nutrition
import com.example.babbogi.util.NutritionState
import com.example.babbogi.util.testNutritionState
import com.example.babbogi.util.toFloat2
import kotlin.math.roundToInt

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NutritionOverviewScreen(viewModel: BabbogiViewModel, navController: NavController) {
    var showDialog by remember { mutableStateOf(false) }

    NutritionOverview(
        nutritionState = viewModel.nutritionState,
        showDialog = showDialog,
        onButtonClicked = { showDialog = true },
        onModify = { list ->
            val pre = viewModel.nutritionState
            viewModel.asyncChangeNutritionRecommendation(
                list.mapIndexed { index, recommend ->
                    Nutrition.entries[index] to recommend.toFloat2(pre[index].recommended)
                }.toMap()
            )
            showDialog = false
        },
        onDismiss = { showDialog = false },
    )
}

@Composable
fun CircularGraphCard(nutrition: Nutrition, intake: IntakeState) {
    val targetValue = remember { (intake.getRatio() * 100).roundToInt() }
    var animatedValue by remember { mutableIntStateOf(0) }

    // 애니메이션을 시작하는 LaunchedEffect
    LaunchedEffect(targetValue) {
        animate(
            initialValue = 0F,
            targetValue = targetValue.toFloat(),
            animationSpec = tween(durationMillis = 1000) // 1초 동안 애니메이션
        ) { value, _ ->
            animatedValue = value.toInt()
        }
    }

    ElevatedCardWithDefault {
        Box(modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
        ) {
            Row(modifier = Modifier.height(100.dp)) {
                Row(modifier = Modifier.width(180.dp)) {
                    NutritionCircularGraph(nutrition = nutrition, intake = intake)
                    Column(
                        verticalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(vertical = 16.dp)
                    ) {
                        Text(text = stringResource(id = nutrition.res))
                        Text(
                            text = "/${"%.1f".format(intake.recommended)}${nutrition.unit}",
                            fontSize = 12.sp,
                        )
                    }
                }
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Text(text = "이 날은 적정량의")
                    Text(text = "$animatedValue%", fontSize = 32.sp)
                    Text(text = "를 드셨습니다.")
                }
            }
        }
    }
}

@Composable
fun NutritionModifyPopup(
    nutritionState: NutritionState,
    onModifyClicked: (nutrition: List<String>) -> Unit,
    onDismiss: () -> Unit,
) {
    var nutritionText by remember { mutableStateOf(List(Nutrition.entries.size) { nutritionState[it].recommended.toString() } ) }

    Dialog(onDismissRequest = onDismiss) {
        ElevatedCardWithDefault(modifier = Modifier.verticalScroll(rememberScrollState())) {
            Box(modifier = Modifier.padding(16.dp)) {
                Column {
                    Text(
                        text = "권장 섭취량 설정",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(8.dp)
                    )
                    repeat(Nutrition.entries.size) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            OutlinedTextField(
                                value = nutritionText[it],
                                onValueChange = { changedText ->
                                    nutritionText = nutritionText.mapIndexed { i, p ->
                                        if (i == it) changedText else p
                                    }
                                },
                                label = { Text(stringResource(id = Nutrition.entries[it].res)) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
                                modifier = Modifier.fillMaxWidth(0.7f),
                                enabled = true,
                            )
                            Text(text = Nutrition.entries[it].unit, fontSize = 20.sp)
                        }
                    }
                    Row(
                        horizontalArrangement = Arrangement.End,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp)
                    ) {
                        Button(onClick = { onModifyClicked(nutritionText) }, modifier = Modifier.padding(start = 8.dp)) {
                            Text(text = "Modify")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NutritionOverview(
    nutritionState: NutritionState,
    showDialog: Boolean,
    onButtonClicked: () -> Unit,
    onModify: (nutrition: List<String>) -> Unit,
    onDismiss: () -> Unit,
) {
    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
        TitleBar("전체 영양 정보")
        ColumnWithDefault {
            repeat(Nutrition.entries.size) { index ->
                CircularGraphCard(
                    nutrition = Nutrition.entries[index],
                    intake = nutritionState[index]
                )
            }
        }
    }

    if (showDialog) NutritionModifyPopup(
        nutritionState = nutritionState,
        onModifyClicked = onModify,
        onDismiss = onDismiss,
    )
}

@Preview
@Composable
fun PreviewNutritionOverview() {
    Scaffold {
        Box(modifier = Modifier.padding(it)) {
            var showDialog by remember { mutableStateOf(false) }

            NutritionOverview(
                nutritionState = testNutritionState,
                showDialog = showDialog,
                onButtonClicked = { showDialog = true },
                onModify = { showDialog = false },
                onDismiss = { showDialog = false },
            )
        }
    }
}