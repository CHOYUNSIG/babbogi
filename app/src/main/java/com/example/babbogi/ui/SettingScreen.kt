package com.example.babbogi.ui

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.example.babbogi.R
import com.example.babbogi.Screen
import com.example.babbogi.model.BabbogiViewModel
import com.example.babbogi.ui.view.ColumnWithDefault
import com.example.babbogi.ui.view.ElevatedCardWithDefault
import com.example.babbogi.ui.view.HealthAbstraction
import com.example.babbogi.ui.view.InputHolder
import com.example.babbogi.ui.view.NutritionRecommendationAbstraction
import com.example.babbogi.ui.view.TitleBar
import com.example.babbogi.util.HealthState
import com.example.babbogi.util.Nutrition
import com.example.babbogi.util.NutritionRecommendation
import com.example.babbogi.util.testHealthState
import com.example.babbogi.util.testNutritionRecommendation
import com.example.babbogi.util.toFloat2

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SettingScreen(viewModel: BabbogiViewModel, navController: NavController) {
    Setting(
        healthState = viewModel.healthState!!,
        recommendation = viewModel.nutritionRecommendation,
        notificationState = true,
        onNotificationStateChanged = {
            /* TODO: 뷰모델에 알림 처리 추가 */
        },
        onHealthCardClicked = {
            navController.navigate(Screen.HealthProfile.name)
        },
        onRecommendationChanged = {
            navController.navigate(Screen.Loading.name)
            viewModel.changeNutritionRecommendation(it) {
                navController.navigate(Screen.Setting.name) {
                    popUpTo(navController.graph.startDestinationId) { inclusive = true }
                }
            }
        },
        onTutorialRestartClicked = {
            navController.navigate(Screen.Tutorial.name)
        }
    )
}

@Composable
fun Setting(
    healthState: HealthState,
    recommendation: NutritionRecommendation,
    notificationState: Boolean,
    onNotificationStateChanged: (Boolean) -> Unit,
    onHealthCardClicked: () -> Unit,
    onRecommendationChanged: (NutritionRecommendation) -> Unit,
    onTutorialRestartClicked: () -> Unit,
) {
    var showRecommendationDialog by remember { mutableStateOf(false) }

    Column {
        TitleBar(title = "설정")
        ColumnWithDefault {
            HealthAbstraction(
                healthState = healthState,
                onClick = onHealthCardClicked,
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_edit_24),
                    contentDescription = "건강 정보 수정",
                )
            }
            NutritionRecommendationAbstraction(
                recommendation = recommendation,
                onClick = { showRecommendationDialog = true }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_edit_24),
                    contentDescription = "권장 섭취량 조정",
                )
            }
            InputHolder(content = "알림") {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "알림을 받으려면 알림 권한을 활성화해야 해요.")
                    }
                    Spacer(modifier = Modifier.requiredWidth(20.dp))
                    Switch(
                        checked = notificationState,
                        onCheckedChange = onNotificationStateChanged
                    )
                }
            }
            ElevatedCardWithDefault(onClick = onTutorialRestartClicked) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                ) {
                    Text(text = "메뉴얼 다시 보기", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_check_24),
                        contentDescription = "메뉴얼 다시 보기",
                    )
                }
            }
        }
    }

    if (showRecommendationDialog) NutritionRecommendationPopup(
        recommendation = recommendation,
        onModifyClicked = onRecommendationChanged,
        onDismiss = { showRecommendationDialog = false },
    )
}

@Composable
private fun NutritionRecommendationPopup(
    recommendation: NutritionRecommendation,
    onModifyClicked: (NutritionRecommendation) -> Unit,
    onDismiss: () -> Unit,
) {
    var nutritionText by remember {
        mutableStateOf(Nutrition.entries.map { recommendation[it]!!.toString() } )
    }

    Dialog(onDismissRequest = onDismiss) {
        ElevatedCardWithDefault {
            Column(
                verticalArrangement = Arrangement.spacedBy(5.dp),
                modifier = Modifier
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
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
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(
                        onClick = {
                            onModifyClicked(
                                Nutrition.entries.mapIndexed { index, nutrition ->
                                    nutrition to nutritionText[index].toFloat2(recommendation[nutrition]!!)
                                }.toMap()
                            )
                        }
                    ) {
                        Text(text = "Modify")
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun PreviewSetting() {
    Scaffold {
        Box(modifier = Modifier.padding(it)) {
            Setting(
                healthState = testHealthState,
                recommendation = testNutritionRecommendation,
                notificationState = true,
                onHealthCardClicked = {},
                onRecommendationChanged = {},
                onNotificationStateChanged = {},
                onTutorialRestartClicked = {},
            )
        }
    }
}