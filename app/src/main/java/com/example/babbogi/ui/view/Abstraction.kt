package com.example.babbogi.ui.view

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.LineBreak
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.babbogi.R
import com.example.babbogi.ui.theme.BabbogiGreen
import com.example.babbogi.ui.theme.BabbogiTypography
import com.example.babbogi.util.HealthState
import com.example.babbogi.util.Nutrition
import com.example.babbogi.util.NutritionIntake
import com.example.babbogi.util.NutritionRecommendation
import com.example.babbogi.util.Product
import com.example.babbogi.util.getRandomNutritionIntake
import com.example.babbogi.util.testHealthState
import com.example.babbogi.util.testNutritionRecommendation
import com.example.babbogi.util.testProduct1
import com.example.babbogi.util.testProduct2
import com.example.babbogi.util.testProduct3
import kotlin.math.abs
import kotlin.random.Random

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NutritionAbstraction(
    recommendation: NutritionRecommendation,
    intake: NutritionIntake,
    onClick: () -> Unit
) {
    val graphDrawer = @Composable { nutrition: Nutrition, graphComposable: @Composable (Float, Float) -> Unit ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val r = recommendation[nutrition]!!
            val i = intake[nutrition]!!
            Text(text = stringResource(id = nutrition.res))
            graphComposable(r, i)
            Text(
                text = "%.1f${nutrition.unit} ${if (r > i) "부족" else "초과"}".format(abs(r - i)),
                fontSize = 12.sp
            )
        }
    }

    FloatingContainer(onClick = onClick) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Text(text = "권장량 대비 섭취량", style = BabbogiTypography.titleMedium)
        }
        graphDrawer(Nutrition.Calorie) { r, i -> NutritionBarGraph(r, i) }
        Row(horizontalArrangement = Arrangement.spacedBy(32.dp)) {
            listOf(Nutrition.Carbohydrate, Nutrition.Protein, Nutrition.Fat).forEach {
                graphDrawer(it) { r, i -> NutritionCircularGraph(r, i)}
            }
        }
    }
}

@Composable
fun Abstraction(
    data: List<Pair<String, Pair<String, String>>>,
    onClick: () -> Unit = {},
    bottom: (@Composable () -> Unit)? = null,
    title: @Composable () -> Unit,
) {
    FloatingContainer(onClick = onClick) {
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
        if (bottom != null) Box(modifier = Modifier.fillMaxWidth()) { bottom() }
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
            Text(text = "사용자 건강 정보", style = BabbogiTypography.titleMedium)
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
            Text(text = "권장 섭취량", style = BabbogiTypography.titleMedium)
            icon()
        }
    }
}

@Composable
fun ProductAbstraction(
    product: Product,
    intakeRatio: Float? = null,
    nullMessage: String = "",
    onClick: () -> Unit = {},
    bottom: (@Composable () -> Unit)? = null,
    prefix: (@Composable () -> Unit)? = null,
    suffix: (@Composable () -> Unit)? = null,
) {
    Abstraction(
        data = if (product.nutrition != null) Nutrition.entries.map { nutrition ->
            stringResource(id = nutrition.res) to Pair(
                product.nutrition[nutrition].toString(),
                nutrition.unit,
            )
        } else listOf(nullMessage to Pair("", "")),
        onClick = onClick,
        bottom = bottom,
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (prefix != null) {
                    prefix()
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(5.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = product.name.ifEmpty { "(이름 없음)" },
                        style = BabbogiTypography.titleMedium.copy(
                            color = if (product.name.isEmpty()) Color.Gray else Color.Unspecified,
                            lineBreak = LineBreak.Heading,
                        ),
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    if (intakeRatio != null)
                        Text(text = "%.1fg".format(intakeRatio * product.servingSize), fontSize = 16.sp)
                }
                if (suffix != null) {
                    Spacer(modifier = Modifier.width(8.dp))
                    suffix()
                }
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
        intake = getRandomNutritionIntake(),
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
        intakeRatio = Random.nextFloat() * 2
    ) {
        Icon(painter = painterResource(id = R.drawable.baseline_edit_24), contentDescription = "")
    }
}