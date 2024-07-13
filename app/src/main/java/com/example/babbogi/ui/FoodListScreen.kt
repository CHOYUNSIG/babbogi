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
import com.example.babbogi.util.testProduct1
import com.example.babbogi.util.testProductList
import com.example.babbogi.util.toFloat2

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
                    nutrition.mapIndexed { index, string ->
                        Nutrition.entries[index] to string.toFloat2(
                            productInfo.nutrition?.get(Nutrition.entries[index])?: 0f
                        )
                    }.toMap()
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
        onSearchClicked = {},
        onFoodCardClicked = { index = it }
    )
}

@Composable
fun FoodModificationCard(
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
                IconButton(onClick = onDelete) {
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
fun FoodPopup(
    product: Product = testProduct1,
    onModifyClicked: (name: String, nutrition: List<String>) -> Unit = { _, _ -> },
    onDismiss: () -> Unit = {},
) {
    var prodNameText by remember { mutableStateOf(product.name) }
    var nutritionText by remember {
        mutableStateOf(Nutrition.entries.map { product.nutrition?.get(it)?.toString()?: "" } )
    }

    Dialog(onDismissRequest = onDismiss) {
        ElevatedCardWithDefault(modifier = Modifier.verticalScroll(rememberScrollState())) {
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
    onSearchClicked: () -> Unit,
    onAddFoodClicked: () -> Unit,
    onModifyClicked: (name: String, nutrition: List<String>) -> Unit,
    onDismiss: () -> Unit,
    onDeleteClicked: (index: Int) -> Unit,
    onSubmitClicked: () -> Unit,
    onFoodCardClicked: (index: Int) -> Unit,
) {
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
                    onClick = { onFoodCardClicked(index) },
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
            CustomIconButton(onSearchClicked, R.drawable.ic_add_box_24)
            CustomIconButton(onSubmitClicked, R.drawable.baseline_send_24)
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
}

@Preview
@Composable
fun PreviewFoodList() {
    Scaffold {
        Box(modifier = Modifier.padding(it)) {
            var index by remember { mutableStateOf<Int?>(null) }

            FoodList(
                productList = testProductList,
                index = index,
                onAmountChanged = { _, _ -> },
                onAddFoodClicked = {},
                onSearchClicked = {},
                onModifyClicked = { _, _ -> index = null },
                onDismiss = { index = null },
                onDeleteClicked = {},
                onSubmitClicked = {},
                onFoodCardClicked = { index = it },
            )
        }
    }
}