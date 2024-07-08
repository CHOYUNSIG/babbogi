package com.example.babbogi.ui.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.babbogi.R

@Composable
fun ButtonContainerBar(
    text: String,
    descriptor: String,
    icon: Int,
    onClick: () -> Unit,
) {
    ElevatedCardWithDefault(modifier = Modifier.fillMaxWidth()) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Text(text = text)
            IconButton(onClick = onClick) {
                Icon(
                    modifier = Modifier.size(30.dp),
                    contentDescription = descriptor,
                    painter = painterResource(id = icon)
                )
            }
        }
    }
}

@Preview
@Composable
fun PreviewButtonContainerBar() {
    ButtonContainerBar(
        text = "버튼 이름",
        descriptor = "버튼 이름",
        icon = R.drawable.baseline_mode_24,
        onClick = {},
    )
}