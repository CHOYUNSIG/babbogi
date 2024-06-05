package com.example.babbogi.ui.view

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
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
import androidx.compose.runtime.remember
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
import com.example.babbogi.util.IntakeState
import com.example.babbogi.util.Nutrition
import kotlin.math.min


@Preview
@Composable
fun NutritionCircularGraph(nutrition: Nutrition = Nutrition.Calorie, intake: IntakeState = IntakeState(2200f, 1900f)) {
    val animatedValue = remember { Animatable(0f) }

    LaunchedEffect(intake.getRatio()) {
        animatedValue.animateTo(
            targetValue = intake.getRatio() * 360,
            animationSpec = tween(durationMillis = 2000, easing = LinearEasing)
        )
    }

    Box(contentAlignment = Alignment.Center, modifier = Modifier.size(100.dp)) {
        Canvas(
            modifier = Modifier
                .size(100.dp)
                .padding(16.dp)
        ) {
            drawArc(
                color = Color.Gray,
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                size = Size(width = size.width, height = size.width),
                style = Stroke(width = 8.dp.toPx(), cap = StrokeCap.Round)
            )
            drawArc(
                brush = Brush.linearGradient(
                    colors =
                    if(intake.getRatio() > 1)
                        listOf(Color(0xFFFF0000), Color(0xFFFFA07A))
                    else if(intake.getRatio() > 0.5)
                        listOf(Color(0xff63C6C4), Color(0xff97CA49))
                    else
                        listOf(Color(0xFFFFFF00), Color(0xFFFFD700)),
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
        Text(text = "${"%.1f".format(intake.ingested)}${nutrition.unit}")
    }
}


@Preview
@Composable
fun NutritionBarGraph(nutrition: Nutrition = Nutrition.Calorie, intake: IntakeState = IntakeState(2200f, 1900f)) {
    val animatedValue = remember { Animatable(0f) }

    LaunchedEffect(intake.getRatio()) {
        animatedValue.animateTo(
            targetValue = intake.getRatio(),
            animationSpec = tween(durationMillis = 2000, easing = LinearEasing)
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
                color = Color.Gray,
                size = Size(size.width, 8.dp.toPx()),
                cornerRadius = CornerRadius(100f, 100f)
            )
            drawRoundRect(
                brush = Brush.linearGradient(
                    colors =
                    if(intake.getRatio() > 1)
                        listOf(Color(0xFFFF0000), Color(0xFFFFA07A))
                    else if(intake.getRatio() > 0.5)
                        listOf(Color(0xff63C6C4), Color(0xff97CA49))
                    else
                        listOf(Color(0xFFFFFF00), Color(0xFFFFD700)),
                    start = Offset.Zero,
                    end = Offset.Infinite,
                ),
                size = Size(min(animatedValue.value * size.width, size.width), 8.dp.toPx()),
                cornerRadius = CornerRadius(100f, 100f)
            )
        }
        Text(text = "${"%.1f".format(intake.ingested)}${nutrition.unit}")
    }
}