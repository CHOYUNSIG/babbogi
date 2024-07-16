package com.example.babbogi.ui.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.babbogi.R
import com.example.babbogi.ui.theme.BabbogiGreen
import com.example.babbogi.ui.theme.BabbogiTheme

//맨 위 앱 이름
@Composable
fun TitleBar(title: String, buttonBar: @Composable () -> Unit = {}) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text(
                text = title,
                color = Color.Black,
                fontSize = 26.sp,
                fontWeight = FontWeight.Black,
            )
            buttonBar()
        }
        HorizontalDivider(
            modifier = Modifier.padding(top = 4.dp),
            thickness = 2.dp,
            color = BabbogiGreen
        )
    }
}


@Preview
@Composable
fun PreviewTitleBar(){
    TitleBar("밥보기") {
        Icon(
            painter = painterResource(id = R.drawable.baseline_list_24),
            contentDescription = null
        )
    }
}
