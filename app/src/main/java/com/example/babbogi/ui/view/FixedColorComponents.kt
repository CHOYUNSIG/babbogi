package com.example.babbogi.ui.view

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.babbogi.ui.theme.BabbogiGreen

@Composable
fun FixedColorButton(
    text: String,
    onClick: () -> Unit,
) {
    Button(
        onClick = onClick,
        contentPadding = PaddingValues(10.dp),
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

@Composable
fun FixedColorSwitch(checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Switch(
        checked = checked,
        onCheckedChange = onCheckedChange,
        colors = SwitchDefaults.colors(
            uncheckedThumbColor = Color.White,
            checkedThumbColor = Color.White,
            uncheckedTrackColor = Color.Black,
            checkedTrackColor = BabbogiGreen,
            uncheckedBorderColor = Color.Black,
            checkedBorderColor = BabbogiGreen,
        )
    )
}

@Preview
@Composable
fun PreviewFixedColorButton() {
    FixedColorButton(text = "text") {}
}

@Preview
@Composable
fun PreviewFixedColorSwitch() {
    var check by remember { mutableStateOf(false) }
    FixedColorSwitch(checked = check) { check = it }
}