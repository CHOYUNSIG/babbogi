package com.example.babbogi.ui.view

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.babbogi.ui.theme.BabbogiGreen
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottomAxis
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStartAxis
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineSpec
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.core.cartesian.data.AxisValueOverrider
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries
import com.patrykandpatrick.vico.core.common.data.ExtraStore
import com.patrykandpatrick.vico.core.common.shader.DynamicShader
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow

@Deprecated("Use LinearWeightHistoryGraph")
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun WeightHistoryGraph(history: Map<LocalDateTime, Float>) {
    val producer = remember { CartesianChartModelProducer.build() }
    val xToDateMapKey by remember { mutableStateOf(ExtraStore.Key<Map<Float, LocalDateTime>>()) }

    LaunchedEffect(history) {
        producer.tryRunTransaction {
            val xToDates = history.keys.associateBy { it.toEpochSecond(ZoneOffset.UTC).toFloat() }
            lineSeries { series(x = xToDates.keys, y = history.values) }
            updateExtras { it[xToDateMapKey] = xToDates }
        }
    }

    CartesianChartHost(
        chart = rememberCartesianChart(
            rememberLineCartesianLayer(
                lines = listOf(
                    rememberLineSpec(
                        shader = DynamicShader.verticalGradient(BabbogiGreen.toArgb(), BabbogiGreen.toArgb()),
                        backgroundShader = DynamicShader.verticalGradient(BabbogiGreen.copy(0.3f).toArgb(), 0)
                    )
                ),
                axisValueOverrider = AxisValueOverrider.adaptiveYValues(1.01f)
            ),
            startAxis = rememberStartAxis(
                valueFormatter = { y, _, _ ->
                    "%.1fkg".format(y)
                }
            ),
            bottomAxis = rememberBottomAxis(
                guideline = null,
                valueFormatter = { x, chartValues, _ ->
                    (chartValues.model.extraStore[xToDateMapKey][x] ?: LocalDateTime.ofEpochSecond(x.toLong(), 0, ZoneOffset.UTC))
                        .format(DateTimeFormatter.ofPattern("M/d"))
                },
                tickLength = 5.dp
            )
        ),
        modelProducer = producer,
        modifier = Modifier.fillMaxSize()
    )
}

private val bottomAxisHeight = 70
private val sidePadding = 50
private val guideLineDivision = 4

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun LinearWeightHistoryGraph(
    modifier: Modifier = Modifier,
    history: Map<LocalDateTime, Float>,
    bottomLimit: Float,
    topLimit: Float,
) {
    val data by remember(history) { mutableStateOf(history.toList().sortedBy { it.first }) }
    val startDateLong = data.first().first.toEpochSecond(ZoneOffset.UTC)
    val endDateLong = data.last().first.toEpochSecond(ZoneOffset.UTC)
    val minWeight by rememberUpdatedState(min(data.minOf { it.second }, bottomLimit))
    val maxWeight by rememberUpdatedState(max(data.maxOf { it.second }, topLimit))
    val getX: (date: LocalDateTime, width: Float) -> Float = remember(data) {
        { date: LocalDateTime, width: Float ->
            val minDate = startDateLong.toFloat()
            val maxDate = endDateLong.toFloat()
            sidePadding + ((date.toEpochSecond(ZoneOffset.UTC) - minDate) / (maxDate - minDate)) * (width - 2 * sidePadding)
        }
    }
    val getY: (weight: Float, height: Float) -> Float = remember(minWeight, maxWeight) {
        { weight: Float, height: Float ->
            (1 - (weight - minWeight) / (maxWeight - minWeight)) * (height - bottomAxisHeight)
        }
    }

    val textMeasurer = rememberTextMeasurer()
    val textColor = MaterialTheme.colorScheme.onPrimaryContainer
    Canvas(modifier = modifier) {
        drawRect(
            color = Color.Gray,
            alpha = 0.1f,
            topLeft = Offset(x = 0f, y = getY(topLimit, size.height)),
            size = Size(width = size.width, height = getY(bottomLimit, size.height) - getY(topLimit, size.height))
        )
        repeat(guideLineDivision + 1) { index ->
            val date = LocalDateTime.ofEpochSecond(startDateLong + index * (endDateLong - startDateLong) / guideLineDivision, 0, ZoneOffset.UTC)
            val x = getX(date, size.width)
            drawLine(
                start = Offset(x = x, y = 0f),
                end = Offset(x = x, y = size.height),
                color = textColor.copy(0.2f),
                strokeWidth = Stroke.DefaultMiter,
                cap = Stroke.DefaultCap,
            )
            val text = if (index < guideLineDivision) date.format(DateTimeFormatter.ofPattern("M/d")) else ""
            val style = TextStyle(color = textColor, fontSize = 12.sp)
            drawText(
                textMeasurer = textMeasurer,
                text = text,
                topLeft = Offset(x = x + 5, y = size.height - textMeasurer.measure(text, style = style).size.height),
                style = style
            )
        }
        listOf(topLimit, bottomLimit).forEachIndexed { index, value ->
            val y = getY(value, size.height)
            drawLine(
                start = Offset(x = 0f, y = y),
                end = Offset(x = size.width, y = y),
                color = Color.Gray,
                strokeWidth = Stroke.DefaultMiter,
                cap = Stroke.DefaultCap,
                pathEffect = PathEffect.dashPathEffect(intervals = floatArrayOf(10f, 10f), phase = 0f)
            )
            val text = "%.1fkg".format(value)
            val style = TextStyle(color = Color.Gray, fontSize = 12.sp)
            drawText(
                textMeasurer = textMeasurer,
                text = text,
                topLeft = Offset(x = 0f, y = y + 5 - index * (textMeasurer.measure(text, style = style).size.height + 10)),
                style = style
            )
        }
        repeat(data.lastIndex) { index ->
            drawLine(
                start = Offset(
                    x = getX(data[index].first, size.width),
                    y = getY(data[index].second, size.height),
                ),
                end = Offset(
                    x = getX(data[index + 1].first, size.width),
                    y = getY(data[index + 1].second, size.height),
                ),
                color = BabbogiGreen,
                strokeWidth = Stroke.DefaultMiter,
                cap = Stroke.DefaultCap,
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun WeightHistoryPopup(
    onStarted: (onLoaded: (Map<LocalDateTime, Float>?, Float?) -> Unit) -> Unit,
    onDismissRequest: () -> Unit,
    onConfirmClicked: () -> Unit,
    onChangeWeightClicked: () -> Unit,
) {
    var isLoading by remember { mutableStateOf(true) }
    var history by remember { mutableStateOf<Map<LocalDateTime, Float>?>(null) }
    var height by remember { mutableStateOf<Float?>(null) }

    LaunchedEffect(true) {
        onStarted { w, h ->
            isLoading = false
            history = w
            height = h
        }
    }

    CustomPopup(
        callbacks = listOf(onChangeWeightClicked, onConfirmClicked),
        labels = listOf("몸무게 수정", "확인"),
        title = "몸무게",
        onDismiss = onDismissRequest,
    ) {
        val w = history
        val h = height
        if (isLoading) Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
        ) {
            CircularProgressIndicator(modifier = Modifier.size(50.dp))
        }
        else if (w != null && h != null) {
            val unit = (h / 100).pow(2)
            val topLimit = unit * 23.0f
            val bottomLimit = unit * 18.5f
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            ) {
                if (w.size >= 2) LinearWeightHistoryGraph(
                    history = w,
                    modifier = Modifier.fillMaxSize(),
                    bottomLimit = bottomLimit,
                    topLimit = topLimit,
                )
                else DescriptionText(text = "정보가 부족해 그래프를 그릴 수 없습니다.")
            }
            Column(
                verticalArrangement = Arrangement.spacedBy(3.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("신체질량지수(BMI) 기준,")
                Text("키 %.1fcm의 적정 몸무게는".format(height))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(3.dp),
                ) {
                    Text(
                        text = "%.1fkg".format(bottomLimit),
                        fontWeight = FontWeight.Bold
                    )
                    Text(text = "이상")
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = "%.1fkg".format(topLimit),
                        fontWeight = FontWeight.Bold
                    )
                    Text(text = "이하입니다.")
                }
            }
        }
        else Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
        ) { DescriptionText(text = "정보를 불러오는 데 실패했습니다.") }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview
@Composable
fun PreviewWeightHistoryPopup() {
    val success: Boolean? = listOf(true, false, null).random()

    WeightHistoryPopup(
        onStarted = onStarted@ { onLoaded ->
            when (true) {
                true -> onLoaded(
                    mapOf(
                        LocalDateTime.parse("2023-01-01T00:00:00") to 80f,
                        LocalDateTime.parse("2023-04-01T00:00:00") to 70f,
                        LocalDateTime.parse("2023-05-01T00:00:00") to 75f,
                    ),
                    180f,
                )
                false -> onLoaded(null, null)
                else -> return@onStarted
            }
        },
        onDismissRequest = {},
        onConfirmClicked = {},
        onChangeWeightClicked = {},
    )
}
