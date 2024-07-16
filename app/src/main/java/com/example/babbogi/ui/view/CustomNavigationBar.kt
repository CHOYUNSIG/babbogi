package com.example.babbogi.ui.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.rememberNavController
import com.example.babbogi.R
import com.example.babbogi.Screen

@Composable
fun CustomNavigationBar(
    navController: NavController,
    screens: List<Screen>,
    labels: List<String>,
    icons: List<Int>,
) {
    ElevatedCardWithDefault {
        Row(
            horizontalArrangement = Arrangement.SpaceAround,
            modifier = Modifier.fillMaxWidth().padding(8.dp)
        ) {
           screens.forEachIndexed { index, screen ->
               CustomNavigationBarItem(
                   icon = icons[index],
                   description = labels[index],
                   onClick = {
                       navController.navigate(screen.name) {
                           popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                           launchSingleTop = true
                           restoreState = true
                       }
                   },
               )
           }
        }
    }
}

@Composable
fun CustomNavigationBarItem(
    icon: Int,
    description: String,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            contentColor = Color.Black,
        )
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Icon(painter = painterResource(id = icon), contentDescription = description)
            Text(text = description)
        }
    }
}

@Preview
@Composable
fun PreviewCustomNavigationBar() {
    CustomNavigationBar(
        navController = rememberNavController(),
        listOf(Screen.NutritionDailyAnalyze, Screen.FoodList, Screen.NutritionPeriodAnalyze),
        listOf("일일 분석", "추가", "기간 분석"),
        listOf(R.drawable.baseline_list_24, R.drawable.ic_add_box_24, R.drawable.baseline_list_24)
    )
}