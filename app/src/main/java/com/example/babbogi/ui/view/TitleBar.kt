package com.example.babbogi.ui.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

//맨 위 앱 이름
@Composable
fun TitleBar(title: String) {
    Box (
        modifier = Modifier
            .fillMaxWidth()
            .background(color = Color.White)
            .padding(16.dp)
    ) {
        Text (
            text = title,
            color = Color.DarkGray,
            fontSize = 26.sp,
            fontWeight = FontWeight.Black,
        )
    }
}
