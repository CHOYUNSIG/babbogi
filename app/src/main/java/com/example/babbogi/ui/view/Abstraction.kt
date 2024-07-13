package com.example.babbogi.ui.view

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.babbogi.util.HealthState
import com.example.babbogi.util.Nutrition
import com.example.babbogi.util.NutritionState
import com.example.babbogi.util.Product
import com.example.babbogi.util.testHealthState
import com.example.babbogi.util.testNutritionState
import com.example.babbogi.util.testProduct1
import com.example.babbogi.util.testProduct2
import com.example.babbogi.util.testProduct3

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NutritionAbstraction(
    nutritionState: NutritionState,
    onClick: () -> Unit
) {
    ElevatedCardWithDefault(onClick = onClick) {
        ColumnWithDefault {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = stringResource(id = Nutrition.Calorie.res))
                NutritionBarGraph(
                    nutrition = Nutrition.Calorie,
                    intake = nutritionState[Nutrition.Calorie]
                )
            }
            Row {
                listOf(Nutrition.Carbohydrate, Nutrition.Protein, Nutrition.Fat).forEach {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = stringResource(id = it.res))
                        NutritionCircularGraph(nutrition = it, intake = nutritionState[it])
                    }
                }
            }
        }
    }
}

@Composable
fun Abstraction(
    data: List<Pair<String, Pair<String, String>>>,
    onClick: () -> Unit = {},
    title: @Composable () -> Unit,
) {
    ElevatedCardWithDefault(onClick = onClick) {
        ColumnWithDefault {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                title()
            }
            Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                data.forEach { row ->
                    val (key, pair) = row
                    val (value, unit) = pair
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = key)
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(3.dp)
                        ) {
                            Text(text = value)
                            if (unit.isNotEmpty()) Text(text = unit)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HealthAbstraction(
    healthState: HealthState,
    onClick: () -> Unit = {}
) {
    Abstraction(
        data = listOf(
            "키" to Pair(healthState.height.toString(), "cm"),
            "몸무게" to Pair(healthState.weight.toString(), "kg"),
            "나이" to Pair(healthState.age.toString(), "세"),
            "성별" to Pair(healthState.gender.toString(), ""),
            "성인병" to Pair(healthState.adultDisease?.toString() ?: "없음", ""),
        ),
        onClick = onClick,
    ) {
        Text(
            text = "사용자 건강 정보",
            fontSize = 20.sp,
            fontWeight = FontWeight.W600,
        )
    }
}

@Composable
fun NutritionRecommendationAbstraction(
    nutritionState: NutritionState,
    onClick: () -> Unit = {}
) {
    Abstraction(
        data = Nutrition.entries.map { nutrition ->
            stringResource(id = nutrition.res) to Pair(
                nutritionState[nutrition].recommended.toString(),
                nutrition.unit
            )
        },
        onClick = onClick
    ) {
        Text(
            text = "권장 섭취량",
            fontSize = 20.sp,
            fontWeight = FontWeight.W600,
        )
    }
}

@Composable
fun ProductAbstraction(
    product: Product,
    amount: Int? = null,
    nullMessage: String = "",
    onClick: () -> Unit = {}
) {
    Abstraction(
        data = if (product.nutrition != null) Nutrition.entries.map { nutrition ->
            stringResource(id = nutrition.res) to Pair(
                product.nutrition[nutrition].toString(),
                nutrition.unit,
            )
        } else listOf(nullMessage to Pair("", "")),
        onClick = onClick
    ) {
        Text(
            text = product.name,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            overflow = TextOverflow.Ellipsis,
        )
        if (amount != null)
            Text(text = "x$amount", fontSize = 16.sp)
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview
@Composable
fun PreviewNutritionAbstraction() {
    NutritionAbstraction(testNutritionState) {}
}

@Preview
@Composable
fun PreviewHealthAbstraction() {
    HealthAbstraction(testHealthState) {}
}

@Preview
@Composable
fun PreviewNutritionRecommendationAbstraction() {
    NutritionRecommendationAbstraction(testNutritionState)
}

@Preview
@Composable
fun PreviewProductAbstraction() {
    ProductAbstraction(
        product = listOf(testProduct1, testProduct2, testProduct3).random(),
        amount = listOf(null, 1, 2, 3).random(),
    )
}