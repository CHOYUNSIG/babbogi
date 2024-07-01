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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.example.babbogi.R
import com.example.babbogi.Screen
import com.example.babbogi.ui.model.BabbogiViewModel
import com.example.babbogi.ui.view.ButtonContainerBar
import com.example.babbogi.ui.view.CustomIconButton
import com.example.babbogi.ui.view.TitleBar
import com.example.babbogi.util.Nutrition
import com.example.babbogi.util.Product
import com.example.babbogi.util.ProductNutritionInfo
import com.example.babbogi.util.testProduct
import com.example.babbogi.util.testProductList
import com.example.babbogi.util.toProductNutritionInfo

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun FoodListScreen(viewModel: BabbogiViewModel, navController: NavController) {
    var index: Int? by remember { mutableStateOf(null) }

    FoodList(
        productList = viewModel.productList,
        index = index,
        onAmountChanged = { i, amount ->
            viewModel.modifyProduct(index = i, amount = viewModel.productList[i].second + amount)
        },
        onAddFoodClicked = {
            viewModel.addProduct()
            index = viewModel.productList.lastIndex
        },
        onModifyClicked = lambda@ { name, nutrition ->
            val i = index ?: return@lambda
            val productInfo = viewModel.productList[i].first
            viewModel.modifyProduct(
                index = i,
                product = Product(
                    name,
                    productInfo.barcode,
                    nutrition.toProductNutritionInfo(productInfo.nutrition?: ProductNutritionInfo())
                )
            )
            index = null
        },
        onDismiss = { index = null },
        onDeleteClicked = { viewModel.deleteProduct(it) },
        onSubmitClicked = {
            viewModel.asyncSendListToServer()
            navController.navigate(Screen.Loading.name)
        },
        onFoodCardClicked = { index = it }
    )
}

@Composable
fun FoodCard(
    product: Product,
    amount: Int,
    onClick: () -> Unit,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit,
    onDelete: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 5.dp),
        onClick = onClick
    ) {
        Column (
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Column (
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = product.name,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Icon(
                        modifier = Modifier.size(30.dp),
                        painter = painterResource(id = R.drawable.baseline_mode_24),
                        contentDescription = "정보 수정하기 아이콘"
                    )
                }
                Text(
                    text = product.nutrition?.toString(List(Nutrition.entries.size) { stringResource(id = Nutrition.entries[it].res) }) ?: "",
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
            Row (
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth(0.8f)
            ) {
                Row (verticalAlignment = Alignment.CenterVertically) {
                    Button(onClick = onDecrease) { Text(text = "-") }
                    Box(modifier = Modifier.padding(16.dp)) { Text(text = amount.toString(), fontSize = 16.sp) }
                    Button(onClick = onIncrease) { Text(text = "+") }
                }
                IconButton(onClick = onDelete) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_delete_24),
                        contentDescription = "Delete",
                    )
                }
            }
        }
    }
}

@Composable
fun FoodPopup(
    product: Product = testProduct,
    onModifyClicked: (name: String, nutrition: List<String>) -> Unit = { _, _ -> },
    onDismiss: () -> Unit = {},
) {
    var prodNameText by remember { mutableStateOf(product.name) }
    var nutritionText by remember { mutableStateOf(List(Nutrition.entries.size) { product.nutrition?.get(it)?.toString()?: "" } ) }

    Dialog(onDismissRequest = onDismiss) {
        ElevatedCard(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
        ) {
            Box(modifier = Modifier.padding(16.dp)) {
                Column {
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
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp)
                    ) {
                        Button(onClick = { onModifyClicked(prodNameText, nutritionText) }, modifier = Modifier.padding(start = 8.dp)) {
                            Text(text = "Modify")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FoodList(
    productList: List<Pair<Product, Int>>,
    index: Int?,
    onAmountChanged: (index: Int, amount: Int) -> Unit,
    onAddFoodClicked: () -> Unit,
    onModifyClicked: (name: String, nutrition: List<String>) -> Unit,
    onDismiss: () -> Unit,
    onDeleteClicked: (index: Int) -> Unit,
    onSubmitClicked: () -> Unit,
    onFoodCardClicked: (index: Int) -> Unit,
) {
    Column {
        TitleBar("섭취 리스트")
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth(0.8f)) {
                ButtonContainerBar(
                    text = "수동 입력",
                    icon = R.drawable.ic_add_box_24,
                    onClick = onAddFoodClicked
                )
                if (productList.isEmpty())
                    Text(
                        text = "카메라로 식품의 바코드를 찍으세요!\n이곳에 표시됩니다.",
                        modifier = Modifier.padding(16.dp),
                        color = Color.Gray
                    )
                else
                    productList.forEachIndexed { index, (productInfo, amount) ->
                        FoodCard(
                            product = productInfo,
                            amount = amount,
                            onIncrease = { onAmountChanged(index, 1) },
                            onDecrease = { onAmountChanged(index, -1) },
                            onClick = { onFoodCardClicked(index) },
                            onDelete = { onDeleteClicked(index) }
                        )
                    }
            }
        }
    }
    if (index != null) {
        Box(modifier = Modifier.padding(50.dp)) {
            FoodPopup(
                product = productList[index].first,
                onModifyClicked = onModifyClicked,
                onDismiss = onDismiss
            )
        }
    }
    Box(
        contentAlignment = Alignment.BottomEnd,
        modifier = Modifier
            .fillMaxSize()
            .padding(50.dp)
    ) {
        CustomIconButton(onSubmitClicked, R.drawable.baseline_send_24)
    }
}

@Preview
@Composable
fun PreviewFoodList() {
    FoodList(
        productList = testProductList,
        index = 3,
        onAmountChanged = { _, _ -> },
        onAddFoodClicked = {},
        onModifyClicked = { _, _ -> },
        onDismiss = {},
        onDeleteClicked = {},
        onSubmitClicked = {},
        onFoodCardClicked = {},
    )
}