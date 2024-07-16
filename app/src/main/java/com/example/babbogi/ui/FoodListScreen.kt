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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
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
import com.example.babbogi.ui.view.CustomIconButton
import com.example.babbogi.ui.view.ElevatedCardWithDefault
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
        onAmountChanged = { index, amount ->
            viewModel.modifyProduct(
                index = index,
                amount = viewModel.productList[index].second + amount
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
                    popUpTo(navController.graph.startDestinationId) { inclusive = true }
                }
            }
        },
    )

    if (selectedIndex != null) selectedIndex = null
}

@Composable
private fun FoodModificationCard(
    product: Product,
    amount: Int,
    onClick: () -> Unit,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit,
    onDelete: () -> Unit
) {
    ElevatedCardWithDefault(onClick = onClick) {
        ColumnWithDefault {
            ProductAbstraction(product = product)
            Row (
                horizontalArrangement = Arrangement.SpaceAround,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(onClick = onClick) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_mode_24),
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
                IconButton(onClick = onDelete) {
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
    var prodNameText by remember { mutableStateOf(product.name) }
    var nutritionText by remember {
        mutableStateOf(Nutrition.entries.map { product.nutrition?.get(it)?.toString()?: "" } )
    }

    Dialog(onDismissRequest = onDismiss) {
        ElevatedCardWithDefault {
            Column(
                verticalArrangement = Arrangement.spacedBy(5.dp),
                modifier = Modifier
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                OutlinedTextField(
                    value = prodNameText,
                    onValueChange = { prodNameText = it },
                    label = { Text("상품명") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text, imeAction = ImeAction.Done),
                    modifier = Modifier.fillMaxWidth(),
                    enabled = true,
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
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(
                        onClick = {
                            val newProduct = Product(
                                prodNameText,
                                nutritionText.mapIndexed { index, string ->
                                    Nutrition.entries[index] to string.toFloat2(
                                        product.nutrition?.get(Nutrition.entries[index]) ?: 0f
                                    )
                                }.toMap()
                            )
                            onModifyClicked(newProduct)
                        }
                    ) {
                        Text(text = "Modify")
                    }
                }
            }
        }
    }
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
        R.drawable.ic_add_box_24,
        R.drawable.baseline_not_find_30,
        R.drawable.ic_add_box_24,
        R.drawable.baseline_list_24,
    )

    Dialog(onDismissRequest = onDismissRequest) {
        Box(
            contentAlignment = Alignment.BottomStart,
            modifier = Modifier.fillMaxSize().padding(bottom = 50.dp)
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
    onAmountChanged: (index: Int, Int) -> Unit,
    onModifyClicked: (index: Int, Product) -> Unit,
    onDeleteClicked: (index: Int) -> Unit,
    onAddByHandClicked: () -> Unit,
    onSearchClicked: () -> Unit,
    onCameraClicked: () -> Unit,
    onSubmitClicked: () -> Unit,
) {
    var selectedIndex by remember { mutableStateOf<Int?>(null) }
    var showAddOption by remember { mutableStateOf(false) }

    if (index != null) selectedIndex = index

    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
        TitleBar("섭취 리스트")
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
                    onClick = { selectedIndex = index },
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
            CustomIconButton(onClick = { showAddOption = true }, R.drawable.ic_add_box_24)
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
            )
        }
    }
}