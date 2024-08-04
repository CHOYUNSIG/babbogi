package com.example.babbogi.ui.view

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.babbogi.ui.theme.BabbogiGreen
import com.example.babbogi.ui.theme.BabbogiTypography
import com.example.babbogi.util.WeightHistory
import com.example.babbogi.util.testWeightHistoryList
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
import kotlinx.coroutines.launch
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

private const val bottomAxisHeight = 100
private const val sidePadding = 50
private const val topPadding = 50

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun LinearWeightHistoryGraph(
    history: List<WeightHistory>,
    selectedHistoryID: Long? = null,
    bottomLimit: Float,
    topLimit: Float,
    onWeightAnkerClicked: (WeightHistory) -> Unit,
) {
    val data by remember(history) {
        mutableStateOf(
            history.sortedBy { it.date }.let {
                it.plus(it.last().copy(id = it.last().id + 1, date = LocalDateTime.now()))
            }
        )
    }
    val startSecond = data.first().date.toEpochSecond(ZoneOffset.UTC)
    val endSecond = data.last().date.toEpochSecond(ZoneOffset.UTC)
    val minWeight by remember(data) { mutableFloatStateOf(min(data.minOf { it.weight }, bottomLimit)) }
    val maxWeight by remember(data) { mutableFloatStateOf(max(data.maxOf { it.weight }, topLimit)) }

    val scrollState = rememberScrollState(Int.MAX_VALUE)
    val maxWidth = (endSecond - startSecond).let { (it / (86400 * 30) * scrollState.viewportSize).toInt() }
    var minWidth by remember { mutableIntStateOf(0) }
    var width by remember(minWidth) { mutableIntStateOf(minWidth) }

    val getX: DrawScope.(LocalDateTime) -> Float = remember(startSecond, endSecond) {
        { date: LocalDateTime ->
            val s = startSecond.toFloat()
            val e = endSecond.toFloat()
            sidePadding + (date.toEpochSecond(ZoneOffset.UTC) - s) / (e - s) * (size.width - 2 * sidePadding)
        }
    }
    val getY: DrawScope.(Float) -> Float = remember(minWeight, maxWeight) {
        { weight: Float ->
            (1 - (weight - minWeight) / (maxWeight - minWeight)) * (size.height - bottomAxisHeight - topPadding) + topPadding
        }
    }

    val anker = remember { mutableStateMapOf<Offset, () -> Unit>() }

    val textColor = MaterialTheme.colorScheme.onPrimaryContainer
    val textMeasurer = rememberTextMeasurer()

    FloatingContainer(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .onGloballyPositioned { minWidth = it.size.width }
                .weight(1f)
                .horizontalScroll(scrollState)
        ) {
            // 정보가 부족할 경우
            if (data.size < 2)
                Text(text = "몸무게 정보가 부족하여\n그래프를 그릴 수 없습니다.", style = BabbogiTypography.bodySmall)
            // 그래프
            else if (width > 0) Canvas(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(LocalDensity.current.run { width.toDp() })
                    .pointerInput(Unit) {
                        detectTapGestures {
                            anker.minBy { (offset, _) ->
                                offset.minus(it).getDistance()
                            }.value()
                        }
                    }
            ) {
                // 정상 몸무게 범위 표시
                drawRect(
                    color = Color.Gray,
                    alpha = 0.1f,
                    topLeft = Offset(x = 0f, y = getY(topLimit)),
                    size = Size(
                        width = size.width,
                        height = getY(bottomLimit) - getY(topLimit)
                    )
                )
                // 날짜 축 표시
                also {
                    val minInterval = textMeasurer.measure("00/00").size.width * 2
                    val guideLineDivision =
                        ((getX(data.last().date) - getX(data.first().date)) / minInterval).toInt()
                    if (guideLineDivision == 0) return@also

                    repeat(guideLineDivision + 1) { index ->
                        val date = LocalDateTime.ofEpochSecond(
                            startSecond + index * (endSecond - startSecond) / guideLineDivision,
                            0,
                            ZoneOffset.UTC
                        )
                        val x = getX(date)
                        drawLine(
                            start = Offset(x = x, y = 0f),
                            end = Offset(x = x, y = size.height),
                            color = textColor.copy(0.2f),
                            strokeWidth = Stroke.DefaultMiter,
                            cap = Stroke.DefaultCap,
                        )
                        val text =
                            if (index < guideLineDivision)
                                date.format(DateTimeFormatter.ofPattern("yyyy\nM/d"))
                            else ""
                        val style = TextStyle(color = textColor, fontSize = 10.sp)
                        drawText(
                            textMeasurer = textMeasurer,
                            text = text,
                            topLeft = Offset(
                                x = x + 5,
                                y = size.height - textMeasurer.measure(
                                    text = text,
                                    style = style,
                                ).size.height
                            ),
                            style = style
                        )
                    }
                }
                // 몸무게 상한과 하한선 표시
                listOf(topLimit, bottomLimit).forEachIndexed { index, value ->
                    val y = getY(value)
                    drawLine(
                        start = Offset(x = 0f, y = y),
                        end = Offset(x = size.width, y = y),
                        color = Color.Gray,
                        strokeWidth = Stroke.DefaultMiter,
                        cap = Stroke.DefaultCap,
                        pathEffect = PathEffect.dashPathEffect(
                            intervals = floatArrayOf(10f, 10f),
                            phase = 0f
                        )
                    )
                    val text = "%.1fkg".format(value)
                    val style = TextStyle(color = Color.Gray, fontSize = 12.sp)
                    drawText(
                        textMeasurer = textMeasurer,
                        text = text,
                        topLeft = Offset(
                            x = scrollState.value.toFloat(),
                            y = y + 5 - index * (textMeasurer.measure(
                                text,
                                style = style
                            ).size.height + 10)
                        ),
                        style = style
                    )
                }
                // 몸무게 그래프 표시
                repeat(data.lastIndex) { index ->
                    drawLine(
                        start = Offset(
                            x = getX(data[index].date),
                            y = getY(data[index].weight),
                        ),
                        end = Offset(
                            x = getX(data[index + 1].date),
                            y = getY(data[index + 1].weight),
                        ),
                        color = BabbogiGreen.copy(alpha = 0.5f),
                        strokeWidth = Stroke.DefaultMiter,
                        cap = Stroke.DefaultCap,
                    )
                }
                // 몸무게 앵커
                anker.clear()
                repeat(data.lastIndex) { index ->
                    val weightHistory = data[index]
                    val (id, weight, date) = weightHistory
                    drawCircle(
                        color = if (id == selectedHistoryID) Color.Red else BabbogiGreen,
                        radius = 10f,
                        center = Offset(
                            x = getX(date),
                            y = getY(weight),
                        ),
                    )
                    anker[Offset(getX(date), getY(weight))] = {
                        onWeightAnkerClicked(weightHistory)
                    }
                }
            }
            // 그래프 초기 로딩 시
            else CircularProgressIndicator(modifier = Modifier.size(50.dp))
        }
        // 그래프 확대 및 축소 버튼
        var sliderPosition by remember { mutableFloatStateOf(0f) }
        val scope = rememberCoroutineScope()
        val setWidth = remember(minWidth, maxWidth) {
            { newWidth: Int ->
                val half = scrollState.viewportSize / 2
                val realNewWidth = max(minWidth, min(maxWidth, newWidth))
                val newPosition = ((scrollState.value + half) * realNewWidth / width.toFloat() - half).toInt()
                scope.launch { scrollState.scrollTo(newPosition) }
                width = realNewWidth
            }
        }

        Column {
            Slider(
                value = sliderPosition,
                onValueChange = {
                    sliderPosition = it
                    setWidth(((maxWidth - minWidth) * it + minWidth).toInt())
                }
            )
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "축소", style = BabbogiTypography.bodySmall)
                Text(text = "확대", style = BabbogiTypography.bodySmall)
            }
        }
    }
}

@Deprecated("This will not work properly.")
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun WeightHistoryPopup(
    onStarted: (onLoaded: (List<WeightHistory>?, Float?) -> Unit) -> Unit,
    onDismissRequest: () -> Unit,
    onConfirmClicked: () -> Unit,
    onChangeWeightClicked: () -> Unit,
) {
    var isLoading by remember { mutableStateOf(true) }
    var history by remember { mutableStateOf<List<WeightHistory>?>(null) }
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
                    .height(300.dp)
            ) {
                if (w.size >= 2) LinearWeightHistoryGraph(
                    history = w,
                    bottomLimit = bottomLimit,
                    topLimit = topLimit,
                    onWeightAnkerClicked = {

                    },
                )
                else Text(
                    text = "정보가 부족해 그래프를 그릴 수 없습니다.",
                    style = BabbogiTypography.bodySmall,
                )
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
        ) {
            Text(text = "정보를 불러오는 데 실패했습니다.", style = BabbogiTypography.bodySmall)
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview
@Composable
fun PreviewLinearWeightHistoryGraph() {
    Box(modifier = Modifier.size(500.dp, 300.dp)) {
        LinearWeightHistoryGraph(
            history = testWeightHistoryList,
            topLimit = 1.8f * 1.8f * 23.0f,
            bottomLimit = 1.8f * 1.8f * 18.5f,
            onWeightAnkerClicked = {}
        )
    }
}
