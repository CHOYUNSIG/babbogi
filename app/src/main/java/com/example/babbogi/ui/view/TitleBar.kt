package com.example.babbogi.ui.view

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.example.babbogi.R
import com.example.babbogi.ui.theme.BabbogiGreen
import com.example.babbogi.ui.theme.BabbogiTypography

//맨 위 앱 이름
@Composable
fun TitleBar(title: String, buttonBar: @Composable () -> Unit = {}) {
    Box(modifier = Modifier
        .zIndex(100f)
        .background(MaterialTheme.colorScheme.background)) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .height(50.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(2.dp, BabbogiGreen),
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_launcher_foreground),
                        contentDescription = "앱 아이콘",
                        contentScale = ContentScale.Inside,
                        modifier = Modifier.size(36.dp)
                    )
                }
                Text(text = title, style = BabbogiTypography.titleLarge)
            }
            buttonBar()
        }
        Box(contentAlignment = Alignment.BottomCenter, modifier = Modifier.matchParentSize()) {
            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 16.dp),
                thickness = 2.dp,
                color = BabbogiGreen
            )
        }
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
