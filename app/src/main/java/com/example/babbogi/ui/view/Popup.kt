package com.example.babbogi.ui.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

@Composable
fun Popup(
    onConfirm: () -> Unit,
    onCancel: (() -> Unit)? = null,
    onDismiss: () -> Unit,
    icon: Int? = null,
    mainText: String,
    subText: String? = null,
) {
    Dialog(onDismissRequest = onDismiss) {
        ElevatedCardWithDefault {
            ColumnWithDefault {
                if (icon != null) Icon(
                    painter = painterResource(id = icon),
                    contentDescription = null,
                    modifier = Modifier.size(50.dp)
                )
                Text(text = mainText)
                if (subText != null) Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxWidth().padding(16.dp)
                ) {
                    Text(text = subText)
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp, alignment = Alignment.End),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    FixedColorButton(text = "확인", onClick = onConfirm)
                    if (onCancel != null) FixedColorButton(text = "취소", onClick = onCancel)
                }
            }
        }
    }
}

@Preview
@Composable
fun PreviewConfirmingPopup() {
    Popup(
        onConfirm = {},
        onCancel = {},
        onDismiss = {},
        // icon = R.drawable.baseline_search_24,
        mainText = "다음 상품을 추가하시겠습니까?",
        subText = "깍두기",
    )
}