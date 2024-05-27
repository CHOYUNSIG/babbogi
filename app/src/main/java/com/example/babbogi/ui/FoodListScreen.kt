package com.example.babbogi.ui

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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
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
import com.example.babbogi.ui.view.CustomIconButton
import com.example.babbogi.ui.view.TitleBar
import com.example.babbogi.util.Nutrition
import com.example.babbogi.util.Product
import com.example.babbogi.util.ProductNutritionInfo
import com.example.babbogi.util.testProduct
import com.example.babbogi.util.testProductList
import com.example.babbogi.util.toProductNutritionInfo

@Composable
fun FoodListScreen(viewModel: BabbogiViewModel, navController: NavController) {
    var index: Int? by remember { mutableStateOf(null) }

    FoodList(
        productList = viewModel.productList,
        index = index,
        onAmountChanged = { i, amount ->
            viewModel.modifyProduct(index = i, amount = viewModel.productList[i].second + amount)
        },
        onModifyClicked = lambda@ { list ->
            val i = index ?: return@lambda
            val productInfo = viewModel.productList[i].first
            viewModel.modifyProduct(
                index = i,
                product = Product(
                    productInfo.name,
                    productInfo.barcode,
                    list.toProductNutritionInfo(productInfo.nutrition?: ProductNutritionInfo())
                )
            )
            index = null
        },
        onDeleteClicked = { viewModel.deleteProduct(it) },
        onSubmitClicked = {
            viewModel.sendList()
            navController.navigate(Screen.Home.name)
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
                Text(
                    text = product.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    overflow = TextOverflow.Ellipsis,
                )
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

@Preview
@Composable
fun FoodPopup(
    product: Product = testProduct,
    onModifyClicked: (List<String>) -> Unit = {},
) {
    var inputText by remember { mutableStateOf(List(Nutrition.entries.size) { product.nutrition?.get(it)?.toString()?: "" } ) }

    Dialog(onDismissRequest = {}) {
        ElevatedCard(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
        ) {
            Box(modifier = Modifier.padding(16.dp)) {
                Column {
                    Text(
                        text = product.name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        modifier = Modifier.padding(8.dp)
                    )
                    repeat(Nutrition.entries.size) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            OutlinedTextField(
                                value = inputText[it],
                                onValueChange = { changedText ->
                                    inputText = inputText.mapIndexed { i, p ->
                                        if (i == it) changedText else p
                                    }
                                },
                                label = { Text(stringResource(id = Nutrition.entries[it].res)) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
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
                        Button(onClick = { onModifyClicked(inputText) }, modifier = Modifier.padding(start = 8.dp)) {
                            Text(text = "Modify")
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun FoodList(
    productList: List<Pair<Product, Int>> = testProductList,
    index: Int? = null,
    onAmountChanged: (index: Int, amount: Int) -> Unit = { _, _ -> },
    onModifyClicked: (List<String>) -> Unit = {},
    onDeleteClicked: (index: Int) -> Unit = {},
    onSubmitClicked: () -> Unit = {},
    onFoodCardClicked: (index: Int) -> Unit = {}
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
                onModifyClicked = { onModifyClicked(it) },
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