package com.example.babbogi.ui.view

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun FixedColorButton(
    text: String,
    onClick: () -> Unit,
) {
    Button(
        onClick = onClick,
        contentPadding = PaddingValues(5.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Black
        )
    ) {
        Text(
            text = text,
            color = Color.White
        )
    }
}