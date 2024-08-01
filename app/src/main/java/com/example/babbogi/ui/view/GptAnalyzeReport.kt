package com.example.babbogi.ui.view

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.babbogi.R

@Composable
fun GptAnalyzeReport(
    title: String,
    isDateIncludesToday: Boolean,
    report: String?,
    onCopyReportToClipboard: (report: String) -> Unit,
    onNewReportRequested: (onLoadingEnded: () -> Unit) -> Unit,
) {
    var isLoading by remember { mutableStateOf(false) }
    val showReport = !isLoading && !isDateIncludesToday && report != null

    FloatingContainer {
        // 제목 행
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            // 제목
            Text(text = title, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            // ChatGPT 기반임을 표시
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
        // 내부 카드
        FloatingContainer {
            Box {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 300.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    // 당일 레포트 생성 불가 고지
                    if (isDateIncludesToday)
                        DescriptionText(text = "당일 레포트는 생성할 수 없어요!")
                    // 로딩중임을 고지
                    else if (isLoading) {
                        Box {
                            val composition by rememberLottieComposition(
                                LottieCompositionSpec.RawRes(
                                    resId = R.raw.send
                                )
                            )
                            LottieAnimation(
                                composition = composition,
                                iterations = LottieConstants.IterateForever,
                                modifier = Modifier.size(150.dp)
                            )
                        }
                        DescriptionText(text = "레포트를 생성하는 데\n10초에서 30초 정도\n걸릴 수 있습니다.")
                    }
                    // 생성된 레포트 표시
                    else if (report != null) {
                        Spacer(modifier = Modifier.heightIn(50.dp))
                        Text(text = report)
                    }
                    // 레포트 생성 버튼
                    else FixedColorButton(
                        onClick = {
                            isLoading = true
                            onNewReportRequested { isLoading = false }
                        },
                        text = "레포트를 생성하려면 클릭하세요"
                    )
                }
                // 클립보드 복사 버튼
                if (showReport) Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    FixedColorIconButton(
                        icon = R.drawable.baseline_content_copy_24,
                        contentDescription = "클립보드에 복사",
                        onClick = { onCopyReportToClipboard(report!!) },
                    )
                }
            }
        }
        // 레포트 재생성 버튼
        if (showReport) Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            FixedColorButton(
                onClick = {
                    isLoading = true
                    onNewReportRequested { isLoading = false }
                },
                text = "레포트 재생성"
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview
@Composable
fun PreviewGptAnalyzeReport() {
    val report = "레포트".repeat(100)

    GptAnalyzeReport(
        title = "레포트 제목",
        isDateIncludesToday = false,
        report = report,
        onNewReportRequested = {},
        onCopyReportToClipboard = {},
    )
}