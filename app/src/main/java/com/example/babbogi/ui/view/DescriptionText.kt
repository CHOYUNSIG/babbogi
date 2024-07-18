package com.example.babbogi.ui.view

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle

@Composable
fun DescriptionText(text: String) {
    Text(
        text = text,
        style = TextStyle(color = Color.Gray)
    )
}