package com.example.babbogi.ui.view

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.babbogi.R

@Composable
fun SearchBar(value: String, onValueChange: (String) -> Unit, onSubmit: (String) -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        TextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text("검색") },
            trailingIcon = {
                IconButton(onClick = { onSubmit(value) }) {
                    Icon(
                        painterResource(id = R.drawable.baseline_not_find_30),
                        contentDescription = "검색",
                    )
                }
            },
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