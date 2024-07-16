package com.example.babbogi.ui.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.babbogi.util.Nutrition
import com.example.babbogi.util.getRandomTestProduct

@Composable
fun ListModificationPopup(
    defaultTexts: List<String>,
    types: List<KeyboardType>,
    labels: List<String?>,
    units: List<String?>,
    onDismiss: () -> Unit,
    onModifyClicked: (List<String>) -> Unit,
) {
    var texts by remember { mutableStateOf(defaultTexts) }

    Dialog(onDismissRequest = onDismiss) {
        ElevatedCardWithDefault(
            modifier = Modifier
                .wrapContentHeight()
                .verticalScroll(rememberScrollState())
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(5.dp),
                modifier = Modifier.padding(16.dp)
            ) {
                repeat(texts.size) { index ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = texts[index],
                            onValueChange = { changedText ->
                                texts = texts.mapIndexed { i, p -> if (i == index) changedText else p }
                            },
                            label = { labels[index]?.let { Text(text = it) } },
                            keyboardOptions = KeyboardOptions(keyboardType = types[index], imeAction = ImeAction.Done),
                            modifier = Modifier.weight(1f),
                            enabled = true,
                        )
                        units[index]?.let { Text(text = it) }
                    }
                }
                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(onClick = { onModifyClicked(texts) }) {
                        Text(text = "Modify")
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun PreviewListModificationPopup() {
    val product = getRandomTestProduct(false)
    ListModificationPopup(
        defaultTexts = listOf(product.name) + Nutrition.entries.map { product.nutrition!![it].toString() },
        types = listOf(KeyboardType.Text) + List(Nutrition.entries.size) { KeyboardType.Number },
        labels = listOf("상품명") + Nutrition.entries.map { stringResource(id = it.res) },
        units = listOf(null) + Nutrition.entries.map { it.unit },
        onDismiss = {},
        onModifyClicked = {}
    )
}