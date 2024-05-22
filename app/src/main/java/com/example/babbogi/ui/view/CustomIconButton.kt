package com.example.babbogi.ui.view

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

@Composable
fun CustomIconButton(
    onClick: () -> Unit,
    icon: Int
) {
    FloatingActionButton(
        onClick = onClick,
        modifier = Modifier
            .size(80.dp, 80.dp)
            .clip(RoundedCornerShape(40.dp))
    ) {
        Icon(
            painter = painterResource(icon),
            modifier = Modifier.fillMaxSize(0.5f),
            contentDescription = "Send"
        )
    }
}