package com.example.babbogi.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.babbogi.ui.model.BabbogiViewModel
import com.example.babbogi.util.ProductInfo

@Composable
fun FoodListScreen(viewModel: BabbogiViewModel, navController: NavController) {
    Column {
        viewModel.productList.forEach { (productInfo, amount) -> FoodCard(productInfo, amount) }
    }
}

@Composable
fun FoodCard(productInfo: ProductInfo, amount: Int) {
    ElevatedCard(
        modifier = Modifier
            .padding(16.dp)
            .background(color = Color.White)
            .fillMaxWidth(),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 5.dp)
    ) {
        Row (
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = productInfo.name
                )
            }
            Column {
                Text(
                    text = productInfo.nutrition?.calorie.toString()
                )
            }
            Text(
                text = amount.toString()
            )
        }
    }
}

@Composable
fun FoodPopup() {

}

@Composable
fun SubmitButton(viewModel: BabbogiViewModel, navController: NavController) {

}