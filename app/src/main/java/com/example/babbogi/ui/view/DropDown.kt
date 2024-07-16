package com.example.babbogi.ui.view

import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropDown(
    options: List<String>,
    nullOption: String,
    index: Int?,
    onChange: (index: Int?) -> Unit
) {
    val realOptions = listOf(nullOption) + options
    var selectedText: String? by remember { mutableStateOf(if (index != null) options[index] else null) }
    var isExpended by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = isExpended,
        onExpandedChange = { isExpended = !isExpended },
    ) {
        TextField(
            modifier = Modifier.menuAnchor(),
            value = selectedText?: realOptions.first(),
            onValueChange = {},
            readOnly = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpended) }
        )
        ExposedDropdownMenu(
            expanded = isExpended,
            onDismissRequest = {isExpended = false},
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
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                )
            }
        }
    }
}

@Preview
@Composable
fun PreviewDropDown() {
    DropDown(
        options = listOf("항목 1", "항목 2", "항목 3"),
        nullOption = "해당 없음",
        index = 1,
        onChange = {}
    )
}