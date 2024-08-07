package com.example.babbogi.ui.view

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.example.babbogi.R

@Composable
fun SearchBar(value: String, onValueChange: (String) -> Unit, onSubmit: (String) -> Unit) {
    val keyboardController = LocalSoftwareKeyboardController.current

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        TextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text("검색") },
            trailingIcon = {
                IconButton(
                    onClick = {
                        keyboardController?.hide()
                        onSubmit(value)
                    }
                ) {
                    Icon(
                        painterResource(id = R.drawable.baseline_search_24),
                        contentDescription = "검색",
                    )
                }
            },
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = Color.Gray.copy(alpha = 0.2f),
                focusedContainerColor = Color.Gray.copy(alpha = 0.1f),
            ),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = {
                keyboardController?.hide()
                onSubmit(value)
            }),
            textStyle = MaterialTheme.typography.bodyLarge.copy(textAlign = TextAlign.Unspecified),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Preview
@Composable
fun PreviewSearchBar() {
    SearchBar(
        value = "",
        onValueChange = {},
        onSubmit = {},
    )
}