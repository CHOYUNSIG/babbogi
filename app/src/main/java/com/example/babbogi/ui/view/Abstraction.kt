package com.example.babbogi.ui.view

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.babbogi.R
import com.example.babbogi.util.HealthState
import com.example.babbogi.util.Nutrition
import com.example.babbogi.util.NutritionIntake
import com.example.babbogi.util.NutritionRecommendation
import com.example.babbogi.util.Product
import com.example.babbogi.util.testHealthState
import com.example.babbogi.util.testNutritionIntake
import com.example.babbogi.util.testNutritionRecommendation
import com.example.babbogi.util.testProduct1
import com.example.babbogi.util.testProduct2
import com.example.babbogi.util.testProduct3
import com.example.babbogi.ui.theme.BabbogiGreen

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NutritionAbstraction(
    recommendation: NutritionRecommendation,
    intake: NutritionIntake,
    onClick: () -> Unit
) {
    ElevatedCardWithDefault(onClick = onClick) {
        ColumnWithDefault {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = stringResource(id = Nutrition.Calorie.res))
                NutritionBarGraph(
                    nutrition = Nutrition.Calorie,
                    recommendation = recommendation[Nutrition.Calorie]!!,
                    intake = intake[Nutrition.Calorie]!!,
                )
            }
            Row {
                listOf(Nutrition.Carbohydrate, Nutrition.Protein, Nutrition.Fat).forEach {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = stringResource(id = it.res))
                        NutritionCircularGraph(
                            nutrition = it,
                            recommendation = recommendation[it]!!,
                            intake = intake[it]!!,
                        )
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
            Box(modifier = Modifier.fillMaxWidth()) { title() }
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
    onClick: () -> Unit = {},
    icon: @Composable () -> Unit = {},
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
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "사용자 건강 정보",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
            )
            icon()
        }
    }
}

@Composable
fun NutritionRecommendationAbstraction(
    recommendation: NutritionRecommendation,
    onClick: () -> Unit = {},
    icon: @Composable () -> Unit = {},
) {
    Abstraction(
        data = Nutrition.entries.map { nutrition ->
            stringResource(id = nutrition.res) to Pair(
                recommendation[nutrition]!!.toString(),
                nutrition.unit
            )
        },
        onClick = onClick
    ) {
        Row (
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "권장 섭취량",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
            )
            icon()
        }
    }
}

@Composable
fun ProductAbstraction(
    product: Product,
    amount: Int? = null,
    nullMessage: String = "",
    onClick: () -> Unit = {},
    icon: @Composable () -> Unit = {},
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
        Column {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(5.dp),
                    verticalAlignment = Alignment.Bottom,
                ) {
                    Text(
                        text = product.name.ifEmpty { "(이름 없음)" },
                        color = if (product.name.isEmpty()) Color.Gray else Color.Unspecified,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        overflow = TextOverflow.Ellipsis,
                    )
                    if (amount != null)
                        Text(text = "x$amount", fontSize = 16.sp)
                }
                icon()
            }
            HorizontalDivider(thickness = 2.dp, color = BabbogiGreen)
        }
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@Preview
@Composable
fun PreviewNutritionAbstraction() {
    NutritionAbstraction(
        recommendation = testNutritionRecommendation,
        intake = testNutritionIntake,
    ) {}
}

@Preview
@Composable
fun PreviewHealthAbstraction() {
    HealthAbstraction(testHealthState) {
        Icon(painter = painterResource(id = R.drawable.baseline_edit_24), contentDescription = "")
    }
}

@Preview
@Composable
fun PreviewNutritionRecommendationAbstraction() {
    NutritionRecommendationAbstraction(testNutritionRecommendation) {
        Icon(painter = painterResource(id = R.drawable.baseline_edit_24), contentDescription = "")
    }
}

@Preview
@Composable
fun PreviewProductAbstraction() {
    ProductAbstraction(
        product = listOf(testProduct1, testProduct2, testProduct3).random(),
        amount = listOf(1, 2, 3).random()
    ) {
        Icon(painter = painterResource(id = R.drawable.baseline_edit_24), contentDescription = "")
    }
}