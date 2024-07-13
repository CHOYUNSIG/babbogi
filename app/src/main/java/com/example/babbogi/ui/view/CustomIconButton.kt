package com.example.babbogi.ui.view

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.babbogi.R

@Composable
fun CustomIconButton(
    onClick: () -> Unit,
    icon: Int
) {
    FloatingActionButton(
        onClick = onClick,
        shape = CircleShape,
        modifier = Modifier.size(80.dp),
        elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 6.dp)
    ) {
        Icon(
            painter = painterResource(icon),
            modifier = Modifier.fillMaxSize(0.5f),
            contentDescription = "Send"
        )
    }
}

@Preview
@Composable
fun PreviewCustomIconButton() {
    CustomIconButton(onClick = {}, icon = R.drawable.baseline_send_24)
}