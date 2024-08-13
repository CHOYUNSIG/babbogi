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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActionScope
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.example.babbogi.R
import com.example.babbogi.Screen
import com.example.babbogi.model.BabbogiViewModel
import com.example.babbogi.ui.theme.BabbogiGreen
import com.example.babbogi.ui.theme.BabbogiTypography
import com.example.babbogi.ui.view.CustomPopup
import com.example.babbogi.ui.view.DateSelector
import com.example.babbogi.ui.view.FixedColorCheckBox
import com.example.babbogi.ui.view.FixedColorFloatingIconButton
import com.example.babbogi.ui.view.FloatingContainer
import com.example.babbogi.ui.view.LazyColumnScreen
import com.example.babbogi.ui.view.ListModificationPopup
import com.example.babbogi.ui.view.ProductAbstraction
import com.example.babbogi.ui.view.ScreenPreviewer
import com.example.babbogi.util.Nutrition
import com.example.babbogi.util.Product
import com.example.babbogi.util.testProductTripleList
import com.example.babbogi.util.toFloat2
import java.time.LocalDate

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun FoodListScreen(
    viewModel: BabbogiViewModel,
    navController: NavController,
    showSnackBar: (message: String) -> Unit,
    showAlertPopup: (title: String, message: String, icon: Int) -> Unit,
) {
    FoodList(
        productList = viewModel.productList,
        onIntakeRatioChanged =  { index, ratio ->
            viewModel.modifyProduct(index, intakeRatio = ratio)
        },
        onProductInfoChanged = { index, product, checked ->
            viewModel.modifyProduct(index = index, product = product, checked = checked)
        },
        onAddByHandConfirmed = {
            viewModel.addProduct(it)
            showSnackBar("음식이 추가되었습니다.")
        },
        onSearchClicked = { navController.navigate(Screen.FoodSearch.name) },
        onCameraClicked = { navController.navigate(Screen.Camera.name) },
        onDeleteClicked = {
            viewModel.deleteProduct()
            showSnackBar("음식이 제거되었습니다.")
        },
        onSubmitClicked = {
            navController.navigate(Screen.Loading.name)
            viewModel.sendList(it) { success ->
                if (success) {
                    navController.navigate(Screen.NutritionDailyAnalyze.name) {
                        popUpTo(navController.graph.startDestinationId) { inclusive = false }
                    }
                    showSnackBar("음식이 전송되었습니다.")
                }
                else {
                    navController.popBackStack()
                    showAlertPopup(
                        "전송 실패",
                        "음식 리스트를 전송하는 데 실패했습니다.",
                        R.drawable.baseline_cancel_24,
                    )
                }
            }
        }
    )
}

@Composable
private fun FoodModificationCard(
    product: Product,
    intakeRatio: Float,
    checked: Boolean,
    onCardClicked: () -> Unit,
    onCheckedChanged: (Boolean) -> Unit,
    onServingSizeChanged: (Float) -> Unit,
    onIntakeRatioChanged: (Float) -> Unit,
) {
    var detailsMode by remember { mutableStateOf(false) }
    val onDetailsModeToggled: (Boolean) -> Unit = remember { { mode -> detailsMode = mode } }
    var servingSizeText by remember(product) { mutableStateOf("%.1f".format(product.servingSize)) }
    var intakeRatioText by remember(intakeRatio) { mutableStateOf("%.1f".format(intakeRatio * 100)) }
    val keyboardController = LocalSoftwareKeyboardController.current
    val onKeyboardDone: KeyboardActionScope.() -> Unit = remember {
        {
            servingSizeText.toFloatOrNull()?.let { onServingSizeChanged(it) }
            intakeRatioText.toFloatOrNull()?.let { onIntakeRatioChanged(it / 100) }
            keyboardController?.hide()
        }
    }

    if (detailsMode) ProductAbstraction(
        product = product,
        onClick = onCardClicked,
        prefix = {
            IconButton(onClick = { onDetailsModeToggled(false) }) {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_arrow_drop_up_24),
                    contentDescription = "접기",
                )
            }
        },
        suffix = {
            Icon(
                painter = painterResource(id = R.drawable.baseline_edit_24),
                contentDescription = null,
            )
            FixedColorCheckBox(checked = checked, onCheckedChange = onCheckedChanged)
        },
        bottom = {
            Row (
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = servingSizeText,
                    onValueChange = { servingSizeText = it },
                    label = { Text(text = "1회 제공량") },
                    suffix = { Text(text = "g") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = onKeyboardDone),
                )
                Text(text = " × ")
                OutlinedTextField(
                    value = intakeRatioText,
                    onValueChange = { intakeRatioText = it },
                    label = { Text(text = "섭취량") },
                    suffix = { Text(text = "%") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = onKeyboardDone),
                )
            }
        }
    )
    else FloatingContainer {
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(5.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                IconButton(onClick = { onDetailsModeToggled(true) }) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_arrow_drop_down_24),
                        contentDescription = "펼쳐보기",
                    )
                }
                Text(
                    text = product.name.ifEmpty { "(이름 없음)" },
                    style = BabbogiTypography.titleMedium.copy(
                        color = if (product.name.isEmpty()) Color.Gray else Color.Unspecified,
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false)
                )
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(5.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                VerticalDivider(
                    color = BabbogiGreen,
                    thickness = 2.dp,
                    modifier = Modifier.height(30.dp)
                )
                FixedColorCheckBox(checked = checked, onCheckedChange = onCheckedChanged)
            }
        }
    }
}

@Composable
private fun FoodPopup(
    product: Product,
    onModifyClicked: (Product) -> Unit,
    onDismiss: () -> Unit,
) {
    ListModificationPopup(
        defaultTexts = listOf(product.name) + Nutrition.entries.map { nutrition ->
            product.nutrition?.get(nutrition)?.toString()?.let { if (it == "0.0") null else it } ?: ""
        },
        types = listOf(KeyboardType.Text) + List(Nutrition.entries.size) { KeyboardType.Number },
        labels = listOf("식품명") + Nutrition.entries.map { stringResource(id = it.res) },
        units = listOf(null) + Nutrition.entries.map { it.unit },
        onDismiss = onDismiss,
        onModifyClicked = { texts ->
            val productName = texts.first()
            val nutrition = texts.drop(1).mapIndexed { index, text ->
                Nutrition.entries[index] to text.toFloat2(
                    product.nutrition?.get(Nutrition.entries[index]) ?: 0f
                )
            }.toMap()
            onModifyClicked(
                product.copy(
                    name = productName,
                    nutrition = nutrition,
                )
            )
        },
        countsOfEachRow = listOf(1, 1, 2, 2, 2, 2)
    )
}

@Composable
private fun OptionDialog(
    alignment: Alignment,
    callbacks: List<() -> Unit>,
    texts: List<String>,
    icons: List<Int>,
    onDismissRequest: () -> Unit,
) {
    val realCallbacks by remember(onDismissRequest, callbacks) {
        mutableStateOf(callbacks + listOf(onDismissRequest))
    }
    val realTexts by remember(texts) {
        mutableStateOf(texts + listOf("취소"))
    }
    val realIcons by remember(icons) {
        mutableStateOf(icons + listOf(R.drawable.baseline_arrow_back_24))
    }

    Dialog(onDismissRequest = onDismissRequest) {
        Box(
            contentAlignment = alignment,
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 50.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                repeat(realCallbacks.size) { index ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        FixedColorFloatingIconButton(
                            onClick = {
                                realCallbacks[index]()
                                onDismissRequest()
                            },
                            icon = realIcons[index]
                        )
                        Text(text = realTexts[index], color = Color.White)
                    }
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun FoodList(
    productList: List<Triple<Product, Float, Boolean>>,
    onProductInfoChanged: (index: Int, product: Product, checked: Boolean) -> Unit,
    onIntakeRatioChanged: (index: Int, ratio: Float) -> Unit,
    onAddByHandConfirmed: (Product) -> Unit,
    onSearchClicked: () -> Unit,
    onCameraClicked: () -> Unit,
    onDeleteClicked: () -> Unit,
    onSubmitClicked: (LocalDate) -> Unit,
) {
    var selectedIndex by remember { mutableStateOf<Int?>(null) }
    var showAddOption by remember { mutableStateOf(false) }
    var showCheckHandleOption by remember { mutableStateOf(false) }
    var showSubmissionConfirmPopup by remember { mutableStateOf(false) }
    var showDeletionConfirmPopup by remember { mutableStateOf(false) }
    var isAddingByHand by remember { mutableStateOf(false) }

    LazyColumnScreen {
        if (productList.isEmpty()) item {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
            ) {
                Text(
                    text = "음식을 검색하거나,\n카메라로 바코드를 찍으세요.\n이곳에 표시됩니다.",
                    style = BabbogiTypography.bodySmall,
                )
            }
        }
        else items(productList.size) { index ->
            val (product, ratio, checked) = productList[index]
            FoodModificationCard(
                product = product,
                intakeRatio = ratio,
                checked = checked,
                onCheckedChanged = { onProductInfoChanged(index, product, it) },
                onServingSizeChanged = {
                    onProductInfoChanged(
                        index,
                        product.copy(servingSize = it),
                        checked
                    )
                },
                onIntakeRatioChanged = { onIntakeRatioChanged(index, it) },
                onCardClicked = { selectedIndex = index },
            )
        }
        item { Spacer(modifier = Modifier.height(150.dp)) }
    }

    Box(
        contentAlignment = Alignment.BottomCenter,
        modifier = Modifier
            .fillMaxSize()
            .padding(50.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            FixedColorFloatingIconButton(
                onClick = { showAddOption = true },
                icon = R.drawable.baseline_add_24
            )
            if (productList.any { it.third }) FixedColorFloatingIconButton(
                onClick = { showCheckHandleOption = true },
                icon = R.drawable.baseline_check_24
            )
        }
    }

    selectedIndex?.let { index ->
        val (product, _, checked) = productList[index]
        FoodPopup(
            product = product,
            onModifyClicked = {
                onProductInfoChanged(index, it, checked)
                selectedIndex = null
            },
            onDismiss = { selectedIndex = null },
        )
    }

    if (isAddingByHand) FoodPopup(
        product = Product("", null, 100f),
        onModifyClicked = {
            onAddByHandConfirmed(it)
            isAddingByHand = false
        },
        onDismiss = { isAddingByHand = false },
    )

    if (showAddOption) OptionDialog(
        alignment = Alignment.BottomStart,
        callbacks = listOf({ isAddingByHand = true }, onSearchClicked, onCameraClicked),
        texts = listOf("수동 입력", "음식 검색", "바코드 스캔"),
        icons = listOf(R.drawable.baseline_edit_24, R.drawable.baseline_search_24, R.drawable.baseline_photo_camera_24),
        onDismissRequest = { showAddOption = false },
    )

    if (showCheckHandleOption) OptionDialog(
        alignment = Alignment.BottomEnd,
        callbacks = listOf({ showDeletionConfirmPopup = true }, { showSubmissionConfirmPopup = true }),
        texts = listOf("삭제", "전송"),
        icons = listOf(R.drawable.baseline_delete_24, R.drawable.baseline_send_24),
        onDismissRequest = { showCheckHandleOption = false },
    )

    if (showDeletionConfirmPopup) {
        val indexes by remember {
            mutableStateOf(productList.mapIndexedNotNull { index, b -> if (b.third) index else null })
        }
        CustomPopup(
            callbacks = listOf(
                { onDeleteClicked() },
                {},
            ),
            labels = listOf("확인", "취소"),
            onDismiss = { showDeletionConfirmPopup = false },
            title = "선택한 음식을 삭제하시겠습니까?",
            icon = R.drawable.baseline_delete_24,
        ) {
            Column {
                indexes.forEach { index ->
                    Text(text = productList[index].first.name)
                }
            }
        }
    }

    if (showSubmissionConfirmPopup) {
        val indexes by remember {
            mutableStateOf(productList.mapIndexedNotNull { index, b -> if (b.third) index else null })
        }
        var dateOption by remember { mutableStateOf<Boolean?>(null) }
        var date by remember { mutableStateOf<LocalDate>(LocalDate.now()) }
        val callbacks by remember(dateOption) {
            mutableStateOf(
                if (dateOption == null) listOf {}
                else listOf({ onSubmitClicked(date) }, {})
            )
        }
        val labels by remember(dateOption) {
            mutableStateOf(
                if (dateOption == null) listOf("취소")
                else listOf("전송", "취소")
            )
        }
        CustomPopup(
            callbacks = callbacks,
            labels = labels,
            onDismiss = { showSubmissionConfirmPopup = false },
            title = "음식 리스트를 제출하시겠습니까?",
            icon = R.drawable.baseline_send_24,
        ) {
            Column {
                indexes.forEach { index ->
                    val (product, ratio) = productList[index]
                    Text(
                        text = "${
                            product.name.ifEmpty { "(이름 없음)" }
                        } %.1fg".format(ratio * product.servingSize)
                    )
                }
            }
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    FixedColorCheckBox(checked = dateOption == false) { dateOption = false }
                    Text(text = "오늘 날짜로 추가")
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    FixedColorCheckBox(checked = dateOption == true) { dateOption = true }
                    Text(text = "이전 일자로 추가")
                }
            }
            if (dateOption == true) DateSelector { date = it }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(uiMode = UI_MODE_NIGHT_NO)
@Preview(uiMode = UI_MODE_NIGHT_YES)
@Composable
fun PreviewFoodList() {
    ScreenPreviewer(screen = Screen.FoodList) {
        FoodList(
            productList = testProductTripleList,
            onIntakeRatioChanged = { _, _ -> },
            onProductInfoChanged = { _, _, _ -> },
            onAddByHandConfirmed = {},
            onSearchClicked = {},
            onCameraClicked = {},
            onDeleteClicked = {},
            onSubmitClicked = {},
        )
    }
}