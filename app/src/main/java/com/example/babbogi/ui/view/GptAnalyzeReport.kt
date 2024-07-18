package com.example.babbogi.ui.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.babbogi.R
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition


@Composable
fun GptAnalyzeReport(
    title: String,
    report: String?,
    onNewReportRequested: (onLoadingEnded: () -> Unit) -> Unit,
) {
    var isLoading by remember { mutableStateOf(false) }

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
                    Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
                        if (isLoading) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(resId = R.raw.send))

                                Box() {
                                    LottieAnimation(
                                        composition = composition,
                                        iterations = LottieConstants.IterateForever,
                                        modifier = Modifier.size(150.dp)
                                    )
                                }
                                Text(
                                    text = "레포트를 생성하는 데 10초에서 30초 정도 걸릴 수 있습니다.",
                                    fontSize = 12.sp,
                                    color = Color.Gray,
                                    modifier = Modifier.padding(top = 8.dp)
                                )
                            }
                        } else if (report != null) {
                            Text(text = report)
                        } else {
                            FixedColorButton(
                                onClick = {
                                    isLoading = true
                                    onNewReportRequested { isLoading = false }
                                },
                                text = "레포트를 생성하려면 클릭하세요"
                            )
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
    val report = null

    GptAnalyzeReport(
        title = "레포트 제목",
        report = report,
        onNewReportRequested = {},
    )
}