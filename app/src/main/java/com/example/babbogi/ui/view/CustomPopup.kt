package com.example.babbogi.ui.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.babbogi.util.Nutrition
import com.example.babbogi.util.getRandomTestProduct

@Composable
fun CustomPopup(
    callbacks: List<() -> Unit>,
    labels: List<String>,
    onDismiss: () -> Unit,
    icon: Int? = null,
    title: String? = null,
    content: (@Composable () -> Unit)? = null,
) {
    Dialog(onDismissRequest = onDismiss) {
        ElevatedCardWithDefault(
            modifier = Modifier
                .wrapContentHeight()
                .verticalScroll(rememberScrollState())
        ) {
            ColumnWithDefault {
                if (icon != null) Icon(
                    painter = painterResource(id = icon),
                    contentDescription = null,
                    modifier = Modifier.size(50.dp)
                )
                if (title != null) Text(text = title, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                if (content != null) content()
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp, alignment = Alignment.End),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    repeat(callbacks.size) { index ->
                        FixedColorButton(text = labels[index], onClick = callbacks[index])
                    }
                }
            }
        }
    }
}

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

    CustomPopup(
        callbacks = listOf({ onModifyClicked(texts) }, onDismiss),
        labels = listOf("확인", "취소"),
        onDismiss = onDismiss
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
            repeat(texts.size) { index ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = texts[index],
                        onValueChange = { changedText ->
                            texts =
                                texts.mapIndexed { i, p -> if (i == index) changedText else p }
                        },
                        label = { labels[index]?.let { Text(text = it) } },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = types[index],
                            imeAction = ImeAction.Done
                        ),
                        modifier = Modifier.weight(1f),
                        enabled = true,
                    )
                    units[index]?.let { Text(text = it) }
                }
            }
        }
    }
}

@Preview
@Composable
fun PreviewCustomPopup() {
    CustomPopup(
        callbacks = listOf({}, {}),
        labels = listOf("예", "아니오"),
        onDismiss = {},
        // icon = R.drawable.baseline_search_24,
        title = "다음 상품을 추가하시겠습니까?"
    ) {
        Text("깍두기")
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