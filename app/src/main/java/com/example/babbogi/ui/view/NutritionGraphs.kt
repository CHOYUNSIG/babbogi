package com.example.babbogi.ui.view

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseOutCubic
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.babbogi.util.Nutrition
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottomAxis
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStartAxis
import com.patrykandpatrick.vico.compose.cartesian.decoration.rememberHorizontalLine
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberColumnCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.common.component.rememberLineComponent
import com.patrykandpatrick.vico.compose.common.component.rememberTextComponent
import com.patrykandpatrick.vico.compose.common.shader.verticalGradient
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.CartesianValueFormatter
import com.patrykandpatrick.vico.core.cartesian.data.columnSeries
import com.patrykandpatrick.vico.core.cartesian.layer.ColumnCartesianLayer
import com.patrykandpatrick.vico.core.common.data.ExtraStore
import com.patrykandpatrick.vico.core.common.shader.DynamicShader
import com.patrykandpatrick.vico.core.common.shape.DashedShape
import com.patrykandpatrick.vico.core.common.shape.Shape
import java.text.DecimalFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.math.min

private fun getColorListByRatio(ratio: Float): List<Color> {
    return if (ratio > 1)
        listOf(Color(0xFFFF0000), Color(0xFFFFA07A))
    else if (ratio > 0.5)
        listOf(Color(0xff63C6C4), Color(0xff97CA49))
    else
        listOf(Color(0xFFFFFF00), Color(0xFFFFD700))
}

@Composable
fun NutritionCircularGraph(nutrition: Nutrition, recommendation: Float, intake: Float) {
    val animatedValue = remember { Animatable(0f) }
    var preIntake by remember { mutableFloatStateOf(intake) }

    if (preIntake != intake)
        preIntake = intake

    LaunchedEffect(preIntake) {
        animatedValue.animateTo(
            targetValue = min(intake / recommendation * 360, 360f),
            animationSpec = tween(durationMillis = 2000, easing = EaseOutCubic)
        )
    }

    Box(contentAlignment = Alignment.Center, modifier = Modifier.size(100.dp)) {
        Canvas(
            modifier = Modifier
                .size(100.dp)
                .padding(16.dp)
        ) {
            drawArc(
                color = Color(0x504DED5D),
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                size = Size(width = size.width, height = size.width),
                style = Stroke(width = 8.dp.toPx(), cap = StrokeCap.Round)
            )
            drawArc(
                brush = Brush.linearGradient(
                    colors = getColorListByRatio(intake / recommendation),
                    start = Offset.Zero,
                    end = Offset.Infinite,
                ),
                startAngle = -90f,
                sweepAngle = animatedValue.value,
                useCenter = false,
                size = Size(width = size.width, height = size.width),
                style = Stroke(width = 8.dp.toPx(), cap = StrokeCap.Round)
            )
        }
        Text(text = "${"%.1f".format(intake)}${nutrition.unit}")
    }
}

@Composable
fun NutritionBarGraph(nutrition: Nutrition, recommendation: Float, intake: Float) {
    val animatedValue = remember { Animatable(0f) }
    var preIntake by remember { mutableFloatStateOf(intake) }

    if (preIntake != intake)
        preIntake = intake

    LaunchedEffect(preIntake) {
        animatedValue.animateTo(
            targetValue = min(intake / recommendation, 1f),
            animationSpec = tween(durationMillis = 2000, easing = EaseOutCubic)
        )
    }

    Box(
        contentAlignment = Alignment.BottomCenter,
        modifier = Modifier.padding(16.dp)
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(30.dp)
        ) {
            drawRoundRect(
                color =  Color(0x504DED5D),
                size = Size(size.width, 8.dp.toPx()),
                cornerRadius = CornerRadius(100f, 100f)
            )
            drawRoundRect(
                brush = Brush.linearGradient(
                    colors = getColorListByRatio(intake / recommendation),
                    start = Offset.Zero,
                    end = Offset.Infinite,
                ),
                size = Size(min(animatedValue.value * size.width, size.width), 8.dp.toPx()),
                cornerRadius = CornerRadius(100f, 100f)
            )
        }
        Text(text = "${"%.1f".format(intake)}${nutrition.unit}")
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NutritionPeriodBarGraph(
    nutrition: Nutrition,
    recommend: Float,
    intakes: Map<LocalDate, Float>,
) {
    val producer = remember { CartesianChartModelProducer.build() }
    val xToDateMapKey by remember { mutableStateOf(ExtraStore.Key<Map<Long, LocalDate>>()) }

    LaunchedEffect(intakes) {
        producer.tryRunTransaction {
            val xToDates = intakes.keys.associateBy { it.toEpochDay() }
            columnSeries { series(x = xToDates.keys, y = intakes.values) }
            updateExtras { it[xToDateMapKey] = xToDates }
        }
    }

    CartesianChartHost(
        chart = rememberCartesianChart(
            rememberColumnCartesianLayer(
                columnProvider = ColumnCartesianLayer.ColumnProvider.series(
                    listOf(rememberLineComponent(
                        thickness = 20.dp,
                        shape = Shape.Pill,
                        dynamicShader = DynamicShader.verticalGradient(
                            getColorListByRatio(
                                (intakes.values.average() / recommend).toFloat()
                            ).toTypedArray()
                        ),
                    ))
                )
            ),
            startAxis = rememberStartAxis(
                valueFormatter = CartesianValueFormatter.decimal(
                    DecimalFormat("#" + nutrition.unit)
                ),
            ),
            bottomAxis = rememberBottomAxis(
                guideline = null,
                valueFormatter = { x, chartValues, _ ->
                    (chartValues.model.extraStore[xToDateMapKey][x.toLong()] ?: LocalDate.ofEpochDay(x.toLong()))
                        .format(DateTimeFormatter.ofPattern("M/d"))
                }
            ),
            decorations = listOf(
                rememberHorizontalLine(
                    y = { recommend },
                    line = rememberLineComponent(
                        thickness = 1.dp,
                        color = Color.Gray,
                        shape = DashedShape(
                            shape = Shape.Pill,
                            dashLengthDp = 2.dp.value,
                            gapLengthDp = 3.dp.value,
                            fitStrategy = DashedShape.FitStrategy.Fixed,
                        )
                    ),
                    labelComponent = rememberTextComponent(),
                )
            )
        ),
        modelProducer = producer
    )
}

@Preview
@Composable
fun PreviewNutritionCircularGraph() {
    NutritionCircularGraph(
        nutrition = Nutrition.Calorie,
        recommendation = 2200f,
        intake = 2000f,
    )
}

@Preview
@Composable
fun PreviewNutritionBarGraph() {
    NutritionBarGraph(
        nutrition = Nutrition.Calorie,
        recommendation = 2200f,
        intake = 2000f,
    )
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview
@Composable
fun PreviewPeriodBarGraph() {
    val data = mapOf(
        LocalDate.now().minusDays(6) to 1600f,
        LocalDate.now().minusDays(5) to 2400f,
        LocalDate.now().minusDays(4) to 2200f,
        LocalDate.now().minusDays(3) to 2300f,
        LocalDate.now().minusDays(2) to 2400f,
        LocalDate.now().minusDays(1) to 3000f,
        LocalDate.now() to 1200f,
    )
    NutritionPeriodBarGraph(
        nutrition = Nutrition.Calorie,
        recommend = Nutrition.Calorie.defaultRecommend,
        intakes = data,
    )
}