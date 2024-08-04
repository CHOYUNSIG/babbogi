package com.example.babbogi.ui.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropDown(
    options: List<String>,
    nullOption: String,
    index: Int?,
    onChange: (index: Int?) -> Unit
) {
    val realOptions by remember(options) { mutableStateOf(listOf(nullOption) + options) }
    var selectedText: String? by remember(options, index) { mutableStateOf(index?.let { options[index] }) }
    var isExpended by remember(options) { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = isExpended,
        onExpandedChange = { isExpended = it },
    ) {
        TextField(
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth(),
            value = selectedText ?: realOptions.first(),
            onValueChange = {},
            readOnly = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpended) },
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = Color.Gray.copy(alpha = 0.2f),
                focusedContainerColor = Color.Gray.copy(alpha = 0.1f),
            ),
            singleLine = true,
            textStyle = TextStyle(textAlign = TextAlign.Start),
        )
        ExposedDropdownMenu(
            expanded = isExpended,
            onDismissRequest = {isExpended = false},
            modifier = Modifier.background(color = Color.White)
        ) {
            realOptions.forEachIndexed { index, text ->
                DropdownMenuItem(
                    text = { Text(text = text) },
                    onClick = {
                        selectedText = text
                        isExpended = false
                        if (index > 0)
                            onChange(index - 1)
                        else
                            onChange(null)
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                    colors = MenuDefaults.itemColors(
                        textColor = Color.Black
                    )
                )
            }
        }
    }
}

@Preview
@Composable
fun PreviewDropDown() {
    DropDown(
        options = remember { listOf("항목 1", "항목 2", "항목 3") },
        nullOption = "해당 없음",
        index = 1,
        onChange = {}
    )
}