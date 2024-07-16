package com.example.babbogi.ui

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.example.babbogi.R
import com.example.babbogi.Screen
import com.example.babbogi.model.BabbogiViewModel
import com.example.babbogi.ui.theme.BabbogiTheme
import com.example.babbogi.ui.view.ColumnWithDefault
import com.example.babbogi.ui.view.CustomIconButton
import com.example.babbogi.ui.view.ElevatedCardWithDefault
import com.example.babbogi.ui.view.ListModificationPopup
import com.example.babbogi.ui.view.ProductAbstraction
import com.example.babbogi.ui.view.TitleBar
import com.example.babbogi.util.Nutrition
import com.example.babbogi.util.Product
import com.example.babbogi.util.testProductList
import com.example.babbogi.util.toFloat2

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun FoodListScreen(viewModel: BabbogiViewModel, navController: NavController) {
    var selectedIndex by remember { mutableStateOf<Int?>(null) }

    FoodList(
        productList = viewModel.productList,
        index = selectedIndex,
        onAmountChanged = { index, change ->
            val newAmount = viewModel.productList[index].second + change
            if (newAmount > 0) viewModel.modifyProduct(
                index = index,
                amount = newAmount
            )
        },
        onAddByHandClicked = {
            viewModel.addProduct()
            selectedIndex = viewModel.productList.lastIndex
        },
        onSearchClicked = { navController.navigate(Screen.FoodSearch.name) },
        onCameraClicked = { navController.navigate(Screen.Camera.name) },
        onModifyClicked = { index, product ->
            viewModel.modifyProduct(index = index, product = product)
        },
        onDeleteClicked = { viewModel.deleteProduct(it) },
        onSubmitClicked = {
            navController.navigate(Screen.Loading.name)
            viewModel.sendList {
                navController.navigate(Screen.NutritionDailyAnalyze.name) {
                    popUpTo(navController.graph.startDestinationId) { inclusive = false }
                }
            }
        },
        onSettingClicked = { navController.navigate(Screen.Setting.name) }
    )

    if (selectedIndex != null) selectedIndex = null
}

@Composable
private fun FoodModificationCard(
    product: Product,
    amount: Int,
    onModify: () -> Unit,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit,
    onDelete: () -> Unit
) {
    ElevatedCardWithDefault {
        ColumnWithDefault {
            ProductAbstraction(
                product = product,
                onClick = onModify,
            )
            Row (
                horizontalArrangement = Arrangement.SpaceAround,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(onClick = onModify, modifier = Modifier.weight(1f)) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_edit_24),
                        contentDescription = "수정",
                    )
                }
                Row (
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Button(onClick = onDecrease) { Text(text = "-") }
                    Text(text = amount.toString(), fontSize = 16.sp)
                    Button(onClick = onIncrease) { Text(text = "+") }
                }
                IconButton(onClick = onDelete, modifier = Modifier.weight(1f)) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_delete_24),
                        contentDescription = "삭제",
                    )
                }
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
        defaultTexts = listOf(product.name) + Nutrition.entries.map {
            product.nutrition?.get(it)?.toString()?.let { if (it == "0.0") null else it } ?: ""
        },
        types = listOf(KeyboardType.Text) + List(Nutrition.entries.size) { KeyboardType.Number },
        labels = listOf("상품명") + Nutrition.entries.map { stringResource(id = it.res) },
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
                Product(
                    name = productName,
                    nutrition = nutrition,
                )
            )
        }
    )
}

@Composable
private fun OptionDialog(
    onDismissRequest: () -> Unit,
    onAddByHandClicked: () -> Unit,
    onSearchClicked: () -> Unit,
    onCameraClicked: () -> Unit,
) {
    val callbacks = listOf(onAddByHandClicked, onSearchClicked, onCameraClicked, onDismissRequest)
    val texts = listOf("수동 입력", "음식 검색", "바코드 스캔", "취소")
    val icons = listOf(
        R.drawable.baseline_edit_24,
        R.drawable.baseline_search_24,
        R.drawable.baseline_photo_camera_24,
        R.drawable.baseline_arrow_back_24,
    )

    Dialog(onDismissRequest = onDismissRequest) {
        Box(
            contentAlignment = Alignment.BottomStart,
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 50.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {

                repeat(4) { index ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        CustomIconButton(
                            onClick = {
                                callbacks[index]()
                                onDismissRequest()
                            },
                            icon = icons[index]
                        )
                        Text(text = texts[index], color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
private fun FoodList(
    productList: List<Pair<Product, Int>>,
    index: Int?,
    onAmountChanged: (index: Int, change: Int) -> Unit,
    onModifyClicked: (index: Int, Product) -> Unit,
    onDeleteClicked: (index: Int) -> Unit,
    onAddByHandClicked: () -> Unit,
    onSearchClicked: () -> Unit,
    onCameraClicked: () -> Unit,
    onSubmitClicked: () -> Unit,
    onSettingClicked: () -> Unit,
) {
    var selectedIndex by remember { mutableStateOf<Int?>(null) }
    var showAddOption by remember { mutableStateOf(false) }

    if (index != null) selectedIndex = index

    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
        TitleBar("섭취 리스트") {
            IconButton(onClick = onSettingClicked) {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_settings_24),
                    contentDescription = "설정",
                )
            }
        }
        ColumnWithDefault {
            if (productList.isEmpty())
                Text(
                    text = "카메라로 식품의 바코드를 찍으세요!\n이곳에 표시됩니다.",
                    modifier = Modifier.padding(16.dp),
                    color = Color.Gray
                )
            else productList.forEachIndexed { index, (productInfo, amount) ->
                FoodModificationCard(
                    product = productInfo,
                    amount = amount,
                    onIncrease = { onAmountChanged(index, 1) },
                    onDecrease = { onAmountChanged(index, -1) },
                    onModify = { selectedIndex = index },
                    onDelete = { onDeleteClicked(index) }
                )
            }
        }
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
            CustomIconButton(onClick = { showAddOption = true }, R.drawable.baseline_add_24)
            CustomIconButton(onSubmitClicked, R.drawable.baseline_send_24)
        }
    }

    selectedIndex?.let {
        FoodPopup(
            product = productList[it].first,
            onModifyClicked = { product ->
                onModifyClicked(it, product)
                selectedIndex = null
            },
            onDismiss = { selectedIndex = null },
        )
    }

    if (showAddOption) {
        OptionDialog(
            onDismissRequest = { showAddOption = false },
            onAddByHandClicked = onAddByHandClicked,
            onSearchClicked = onSearchClicked,
            onCameraClicked = onCameraClicked,
        )
    }
}

@Preview
@Composable
fun PreviewFoodList() {
    BabbogiTheme {
        Scaffold {
            Box(modifier = Modifier.padding(it)) {
                FoodList(
                    productList = testProductList,
                    index = null,
                    onAmountChanged = { _, _ -> },
                    onAddByHandClicked = {},
                    onCameraClicked = {},
                    onSearchClicked = {},
                    onModifyClicked = { _, _ -> },
                    onDeleteClicked = {},
                    onSubmitClicked = {},
                    onSettingClicked = {},
                )
            }
        }
    }
}