package com.example.babbogi.ui

import android.Manifest
import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.babbogi.R
import com.example.babbogi.Screen
import com.example.babbogi.model.BabbogiViewModel
import com.example.babbogi.ui.theme.BabbogiTypography
import com.example.babbogi.ui.view.ColumnScreen
import com.example.babbogi.ui.view.FixedColorSwitch
import com.example.babbogi.ui.view.FloatingContainer
import com.example.babbogi.ui.view.HealthAbstraction
import com.example.babbogi.ui.view.InputHolder
import com.example.babbogi.ui.view.ListModificationPopup
import com.example.babbogi.ui.view.NutritionRecommendationAbstraction
import com.example.babbogi.ui.view.ScreenPreviewer
import com.example.babbogi.util.HealthState
import com.example.babbogi.util.Nutrition
import com.example.babbogi.util.NutritionRecommendation
import com.example.babbogi.util.testHealthState
import com.example.babbogi.util.testNutritionRecommendation
import com.example.babbogi.util.toFloat2
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState

@OptIn(ExperimentalPermissionsApi::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SettingScreen(
    viewModel: BabbogiViewModel,
    navController: NavController,
    showSnackBar: (message: String) -> Unit,
    showAlertPopup: (title: String, message: String, icon: Int) -> Unit,
) {
    val hasPermission = Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU || run {
        rememberPermissionState(Manifest.permission.POST_NOTIFICATIONS).hasPermission
    }

    Setting(
        healthState = viewModel.healthState,
        recommendation = viewModel.nutritionRecommendation,
        notificationState = viewModel.notificationActivation,
        hasNotificationPermission = hasPermission,
        serverRecommendationUsage = viewModel.useServerRecommendation,
        onStarted = { viewModel.getHealthStateFromServer {} },
        onNotificationStateChanged = { viewModel.notificationActivation = it },
        onServerRecommendationUsageChanged = { viewModel.useServerRecommendation = it },
        onHealthCardClicked = { navController.navigate(Screen.HealthProfile.name) },
        onRecommendationChanged = {
            navController.navigate(Screen.Loading.name)
            viewModel.changeNutritionRecommendation(it) { success ->
                if (success) showSnackBar("권장 섭취량이 변경되었습니다.")
                else showAlertPopup(
                    "변경 실패",
                    "변경된 섭취량을 전송하는 데 실패했습니다.",
                    R.drawable.baseline_cancel_24,
                )
                navController.navigate(Screen.Setting.name) {
                    popUpTo(Screen.Setting.name) { inclusive = true }
                }
            }
        },
        onTutorialRestartClicked = { navController.navigate(Screen.Tutorial.name) }
    )
}

@Composable
private fun Setting(
    healthState: HealthState?,
    recommendation: NutritionRecommendation,
    notificationState: Boolean,
    hasNotificationPermission: Boolean,
    serverRecommendationUsage: Boolean,
    onStarted: () -> Unit,
    onNotificationStateChanged: (Boolean) -> Unit,
    onServerRecommendationUsageChanged: (Boolean) -> Unit,
    onHealthCardClicked: () -> Unit,
    onRecommendationChanged: (NutritionRecommendation) -> Unit,
    onTutorialRestartClicked: () -> Unit,
) {
    var showRecommendationDialog by remember { mutableStateOf(false) }
    LaunchedEffect(true) { if (healthState == null) onStarted() }

    ColumnScreen {
        // 사용자 건강 정보 카드
        healthState?.let { healthState ->
            HealthAbstraction(
                healthState = healthState,
                onClick = onHealthCardClicked,
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_edit_24),
                    contentDescription = "건강 정보 수정",
                )
            }
        }
        // 권장 섭취량 카드
        NutritionRecommendationAbstraction(
            recommendation = recommendation,
            onClick = { showRecommendationDialog = true }
        ) {
            Icon(
                painter = painterResource(id = R.drawable.baseline_edit_24),
                contentDescription = "권장 섭취량 조정",
            )
        }
        // 추천 권장 섭취량 사용 옵션
        InputHolder(content = "섭취량 추천") {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    text = "밥보기가 사용자의 건강 정보를 기반으로 계산한 권장 섭취량을 사용합니다.",
                    style = BabbogiTypography.bodySmall,
                    modifier = Modifier.weight(1f),
                )
                FixedColorSwitch(
                    checked = serverRecommendationUsage,
                    onCheckedChange = onServerRecommendationUsageChanged
                )
            }
        }
        // 알림 활성화 옵션
        InputHolder(content = "알림") {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                if (!hasNotificationPermission) Text(
                    text = "경고: 앱에 알림 권한이 부여되지 않았습니다. 알림을 받으려면 알림 권한을 활성화해야 합니다.",
                    style = BabbogiTypography.bodySmall,
                )
                else {
                    Text(
                        text = "밥보기에서 푸시 알림을 받습니다.",
                        style = BabbogiTypography.bodySmall,
                        modifier = Modifier.weight(1f),
                    )
                    FixedColorSwitch(
                        checked = notificationState,
                        onCheckedChange = onNotificationStateChanged
                    )
                }
            }
        }
        // 가이드라인 다시보기
        FloatingContainer(onClick = onTutorialRestartClicked) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "메뉴얼 다시 보기", style = BabbogiTypography.titleMedium)
                Icon(
                    painter = painterResource(id = R.drawable.baseline_menu_book_24),
                    contentDescription = "메뉴얼 다시 보기",
                )
            }
        }
    }

    // 추천 영양소 수정 팝업 표시
    if (showRecommendationDialog) NutritionRecommendationPopup(
        recommendation = recommendation,
        onModifyClicked = {
            showRecommendationDialog = false
            onRecommendationChanged(it)
        },
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
        },
        countsOfEachRow = listOf(1, 2, 2, 2, 2)
    )
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(uiMode = UI_MODE_NIGHT_NO)
@Preview(uiMode = UI_MODE_NIGHT_YES)
@Composable
fun PreviewSetting() {
    ScreenPreviewer(screen = Screen.Setting) {
        Setting(
            healthState = testHealthState,
            recommendation = testNutritionRecommendation,
            notificationState = true,
            hasNotificationPermission = false,
            serverRecommendationUsage = true,
            onStarted = {},
            onHealthCardClicked = {},
            onRecommendationChanged = {},
            onServerRecommendationUsageChanged = {},
            onNotificationStateChanged = {},
            onTutorialRestartClicked = {},
        )
    }
}