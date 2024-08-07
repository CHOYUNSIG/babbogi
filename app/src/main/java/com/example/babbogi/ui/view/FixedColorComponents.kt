package com.example.babbogi.ui.view

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.AbsoluteRoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.babbogi.R
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

@Composable
fun FixedColorFloatingIconButton(
    icon: Int,
    onClick: () -> Unit,
) {
    FloatingActionButton(
        onClick = onClick,
        shape = CircleShape,
        modifier = Modifier.size(80.dp),
        containerColor = BabbogiGreen,
        contentColor = Color.White,
        elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 5.dp)
    ) {
        Icon(
            painter = painterResource(icon),
            modifier = Modifier.fillMaxSize(0.5f),
            contentDescription = "Send"
        )
    }
}

@Composable
fun FixedColorCheckBox(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Checkbox(
        checked = checked,
        onCheckedChange = onCheckedChange,
        colors = CheckboxDefaults.colors(
            checkedColor = BabbogiGreen,
            checkmarkColor = Color.White,
            uncheckedColor = MaterialTheme.colorScheme.onPrimaryContainer,
        )
    )
}

@Composable
fun FixedColorIconButton(
    icon: Int,
    contentDescription: String? = null,
    size: Dp = 40.dp,
    onClick: () -> Unit,
) {
    ElevatedButton(
        onClick = onClick,
        shape = AbsoluteRoundedCornerShape(8.dp),
        colors = ButtonDefaults.elevatedButtonColors(
            containerColor = Color.Black,
            contentColor = Color.White,
        ),
        elevation = ButtonDefaults.elevatedButtonElevation(defaultElevation = 5.dp),
        contentPadding = PaddingValues(5.dp),
        modifier = Modifier.size(size)
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = contentDescription,
            modifier = Modifier.fillMaxSize()
        )
    }
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

@Preview
@Composable
fun PreviewFixedColorFloatingIconButton() {
    FixedColorFloatingIconButton(icon = R.drawable.baseline_send_24) {}
}

@Preview
@Composable
fun PreviewFixedColorCheckBox() {
    FixedColorCheckBox(checked = true) {}
}

@Preview
@Composable
fun PreviewFixedColorIconButton() {
    FixedColorIconButton(icon = R.drawable.baseline_send_24) {}
}