package com.example.babbogi.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.example.babbogi.BabbogiScreen
import com.example.babbogi.R
import com.example.babbogi.ui.model.BabbogiViewModel
import com.example.babbogi.ui.view.CustomIconButton
import com.example.babbogi.ui.view.TitleBar
import com.example.babbogi.util.ProductInfo
import com.example.babbogi.util.nutritionName
import com.example.babbogi.util.nutritionNameKorean
import com.example.babbogi.util.nutritionUnit
import com.example.babbogi.util.testProduct
import com.example.babbogi.util.testProductList

@Composable
fun FoodListScreen(viewModel: BabbogiViewModel, navController: NavController) {
    var index: Int? = remember { null }
    var productInfo: ProductInfo? = remember { null }

    FoodList(
        productList = viewModel.productList,
        index = index,
        onModifyClicked = {},
        onDeleteClicked = { val i = index; if (i != null) viewModel.deleteProduct(i); index = null },
        onSubmitClicked = { navController.navigate(BabbogiScreen.Home.name) },
        onFoodCardClicked = { index = it }
    )
}

@Composable
fun FoodCard(productInfo: ProductInfo, amount: Int, onClick: () -> Unit) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 5.dp),
        onClick = onClick
    ) {
        Row (
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(modifier = Modifier.padding(16.dp)) {
                Text(text = productInfo.name, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
            Box(modifier = Modifier.padding(16.dp)) {
                Text(text = amount.toString(), fontSize = 16.sp)
            }
        }
    }
}

@Composable
fun FoodPopup(productInfo: ProductInfo = testProduct, onModifyClicked: () -> Unit, onDeleteClicked: () -> Unit) {
    Dialog(onDismissRequest = {}) {
        ElevatedCard(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Box(
                modifier = Modifier.padding(16.dp)
            ) {
                Column {
                    Text(
                        text = productInfo.name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        modifier = Modifier.padding(8.dp)
                    )
                    repeat(9) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            OutlinedTextField(
                                value = "",
                                onValueChange = { /*값 업데이트*/ },
                                label = { Text(nutritionNameKorean[nutritionName[it]]!!) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.fillMaxWidth(0.7f),
                                enabled = true,
                            )
                            Text(text = nutritionUnit[nutritionName[it]]!!, fontSize = 20.sp)
                        }
                    }
                    Row(
                        horizontalArrangement = Arrangement.End,
                        modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
                    ) {
                        Button(onClick = onModifyClicked, modifier = Modifier.padding(start = 8.dp)) {
                            Text(text = "Modify")
                        }
                        Button(onClick = onDeleteClicked, modifier = Modifier.padding(start = 8.dp)) {
                            Text(text = "Delete")
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
    productList: List<Pair<ProductInfo, Int>> = testProductList,
    index: Int? = 1,
    onModifyClicked: (index: Int) -> Unit = {},
    onDeleteClicked: (index: Int) -> Unit = {},
    onSubmitClicked: () -> Unit = {},
    onFoodCardClicked: (index: Int) -> Unit = {}
) {
    Column {
        TitleBar("섭취 리스트")
        Column(
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .align(Alignment.CenterHorizontally)
        ) {
            productList.forEachIndexed { index, (productInfo, amount) ->
                FoodCard(productInfo, amount) { onFoodCardClicked(index) }
            }
        }
    }
    if (index != null) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(50.dp)
        ) {
            FoodPopup(productList[index].first, { onModifyClicked(index) }, { onDeleteClicked(index) })
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