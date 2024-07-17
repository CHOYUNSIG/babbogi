package com.example.babbogi.ui

import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.babbogi.R
import com.example.babbogi.Screen
import com.example.babbogi.model.BabbogiViewModel
import com.example.babbogi.ui.theme.BabbogiTheme
import com.example.babbogi.ui.view.ColumnWithDefault
import com.example.babbogi.ui.view.ElevatedCardWithDefault
import com.example.babbogi.ui.view.HealthAbstraction
import com.example.babbogi.ui.view.InputHolder
import com.example.babbogi.ui.view.ListModificationPopup
import com.example.babbogi.ui.view.NutritionRecommendationAbstraction
import com.example.babbogi.ui.view.PreviewCustomNavigationBar
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
        healthState = viewModel.healthState,
        recommendation = viewModel.nutritionRecommendation,
        notificationState = viewModel.notificationActivation,
        onNotificationStateChanged = {
            /* TODO: 뷰모델에 알림 처리 추가 */
            viewModel.notificationActivation = it
        },
        onHealthCardClicked = { navController.navigate(Screen.HealthProfile.name) },
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
    healthState: HealthState?,
    recommendation: NutritionRecommendation,
    notificationState: Boolean,
    onNotificationStateChanged: (Boolean) -> Unit,
    onHealthCardClicked: () -> Unit,
    onRecommendationChanged: (NutritionRecommendation) -> Unit,
    onTutorialRestartClicked: () -> Unit,
) {
    var showRecommendationDialog by remember { mutableStateOf(false) }

    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
        TitleBar(title = "설정")
        ColumnWithDefault {
            if (healthState != null) HealthAbstraction(
                healthState = healthState,
                onClick = onHealthCardClicked,
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_edit_24),
                    contentDescription = "건강 정보 수정",
                )
            }
            else ElevatedCardWithDefault(onClick = onHealthCardClicked) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                ) {
                    Text(text = "건강 정보 입력", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_edit_24),
                        contentDescription = "건강 정보 입력",
                    )
                }
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
                        painter = painterResource(id = R.drawable.baseline_menu_book_24),
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
    ListModificationPopup(
        defaultTexts = Nutrition.entries.map { recommendation[it]!!.toString() },
        types = List(Nutrition.entries.size) { KeyboardType.Number },
        labels = Nutrition.entries.map { stringResource(id = it.res) },
        units = Nutrition.entries.map { it.unit },
        onDismiss = onDismiss,
        onModifyClicked = { texts ->
            onModifyClicked(
                texts.mapIndexed { index, text ->
                    Nutrition.entries[index] to text.toFloat2(
                        recommendation[Nutrition.entries[index]]!!
                    )
                }.toMap()
            )
        }
    )
}

@Preview(uiMode = UI_MODE_NIGHT_NO)
@Preview(uiMode = UI_MODE_NIGHT_YES)
@Composable
fun PreviewSetting() {
    BabbogiTheme {
        Scaffold(bottomBar = { PreviewCustomNavigationBar() }) {
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
}