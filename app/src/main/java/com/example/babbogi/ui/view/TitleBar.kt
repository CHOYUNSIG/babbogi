package com.example.babbogi.ui.view


import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

//맨 위 앱 이름
@Composable
fun TitleBar(title: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = title,
            color = Color.Black,
            fontSize = 26.sp,
            fontWeight = FontWeight.Black,
        )
        Divider(
            color = Color(0xFF4DED5D),
            thickness = 2.dp,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}


@Preview
@Composable
fun PreviewTitleBar(){
    TitleBar("밥보기")
}
