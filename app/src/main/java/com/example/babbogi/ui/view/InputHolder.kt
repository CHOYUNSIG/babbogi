package com.example.babbogi.ui.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun InputHolder(
    content: String,
    holder: @Composable () -> Unit
) {
    ElevatedCardWithDefault {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 5.dp)
                .fillMaxWidth()
                .height(80.dp)
        ) {
            Box(modifier = Modifier.width(100.dp)) {
                Text(
                    text = content,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
            Box { holder() }
        }
    }
}

@Composable
fun TextInputHolder(
    content: String,
    value: String,
    onValueChange: (String) -> Unit,
    labeling: String,
    keyboardType: KeyboardType,
    unit: String? = null,
    isError: Boolean = false,
) {
    InputHolder(content = content) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                label = { Text(text = labeling, overflow = TextOverflow.Ellipsis, maxLines = 1) },
                keyboardOptions = KeyboardOptions(
                    keyboardType = keyboardType,
                    imeAction = ImeAction.Done
                ),
                modifier = Modifier.weight(1f),
                enabled = true,
                textStyle = TextStyle(fontSize = 20.sp),
                suffix = { if (unit != null) Text(text = unit) },
                isError = isError
            )
        }
    }
}

@Preview
@Composable
fun PreviewTextInputHolder() {
    TextInputHolder(
        content = "입력항목",
        value = "입력",
        onValueChange = {},
        labeling = "입력하세요".repeat(10),
        keyboardType = KeyboardType.Text,
        unit = "cm",
    )
}