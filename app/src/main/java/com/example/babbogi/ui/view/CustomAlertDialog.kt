package com.example.babbogi.ui.view

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.babbogi.R

@Composable
fun CustomAlertDialog(
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
    dialogTitle: String,
    dialogText: String,
    iconResId: Int,
) {
    AlertDialog(
        icon = {
            Icon(
                painter = painterResource(id = iconResId),
                contentDescription = "찾을 수 없음 아이콘"
            )
        },
        title = { Text(text = dialogTitle) },
        text = { Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxWidth()
        ) { Text(text = dialogText) } },
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(onClick = onConfirmation) {
                Text("확인")
            }
        }
    )
}

@Preview
@Composable
fun PreviewCustomAlertDialog(){
    CustomAlertDialog(
        onDismissRequest={},
        onConfirmation={},
        dialogTitle="찾을 수 없음",
        dialogText="해당 상품은 찾을 수 없는 상품입니다.",
        iconResId = R.drawable.baseline_not_find_30
    )
}