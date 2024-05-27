package com.example.babbogi.ui.view

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.example.babbogi.util.IntakeState
import com.example.babbogi.util.Nutrition


@Composable
fun NutritionCircularGraph(nutrition: Nutrition, intake: IntakeState) {
    Box(contentAlignment = Alignment.Center, modifier = Modifier.size(100.dp)) {
        Canvas(
            modifier = Modifier
                .size(100.dp)
                .padding(16.dp)
        ) {
            drawArc(
                color = if (intake.getRatio() > 1) Color.Red else if (intake.getRatio() > 0.5) Color.Green else Color.Yellow,
                startAngle = -90f,
                sweepAngle = intake.getRatio() * 360,
                useCenter = false,
                size = Size(width = size.width, height = size.width),
                style = Stroke(width = 8.dp.toPx())
            )
        }
        Text(text = "${"%.1f".format(intake.ingested)}${nutrition.unit}")
    }
}
