package com.example.babbogi.ui.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.babbogi.R

@Composable
fun GptAnalyzeReport(title: String, report: String?) {
    ElevatedCardWithDefault {
        ColumnWithDefault {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = title, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(3.dp, Alignment.End),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "Powered by", fontSize = 12.sp)
                    Icon(
                        modifier = Modifier.size(20.dp),
                        painter = painterResource(id = R.drawable.chatgpt_logo),
                        contentDescription = "ChatGPT 로고"
                    )
                    Text(text = "ChatGPT")
                }
            }
            ElevatedCardWithDefault {
                ColumnWithDefault(
                    modifier = Modifier
                        .heightIn(max = 300.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    if (report != null) Box(modifier = Modifier.fillMaxWidth()) {
                        Text(text = report)
                    }
                    else Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
                        Button(onClick = { /*TODO*/ }) {
                            Text(text = "레포트 생성하려면 클릭하세요")
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun PreviewGptAnalyzeReport() {
    val report = "이것은 챗지피티가 제작한 일일 영양소 레포트입니다. ".repeat(100)

    GptAnalyzeReport(
        title = "레포트 제목",
        report = report,
    )
}