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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
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
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.babbogi.R
import com.example.babbogi.ui.theme.BabbogiTypography

@Composable
fun GptAnalyzeReport(
    title: String,
    report: String?,
    prohibitMessage: String? = null,
    onCopyReportToClipboard: (report: String) -> Unit,
    onNewReportRequested: (onLoadingEnded: () -> Unit) -> Unit,
) {
    var isLoading by remember { mutableStateOf(false) }
    val showReport = !isLoading && prohibitMessage == null && report != null

    FloatingContainer {
        // 제목 행
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = title, style = BabbogiTypography.titleMedium)
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = remember {
                    buildAnnotatedString {
                        withStyle(SpanStyle(fontSize = 12.sp)) { append("Powered by ") }
                        appendInlineContent("chatgpt_logo")
                        append(" ChatGPT")
                    }
                },
                inlineContent = remember {
                    mapOf(
                        "chatgpt_logo" to InlineTextContent(Placeholder(20.sp, 20.sp, PlaceholderVerticalAlign.TextCenter)) {
                            Icon(
                                painter = painterResource(id = R.drawable.chatgpt_logo),
                                contentDescription = "ChatGPT 로고"
                            )
                        }
                    )
                },
            )
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
                    if (prohibitMessage != null)
                        Text(text = prohibitMessage, style = BabbogiTypography.bodySmall)
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
                        Text(text = "레포트를 생성하는 데\n10초에서 30초 정도\n걸릴 수 있습니다.", style = BabbogiTypography.bodySmall)
                    }
                    // 생성된 레포트 표시
                    else if (report != null) {
                        Spacer(modifier = Modifier.heightIn(50.dp))  // 클립보드 복사 버튼만큼의 패딩
                        Text(
                            text = report,
                            fontSize = 15.sp,
                            lineHeight = 20.sp,
                            textAlign = TextAlign.Start,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                    // 레포트 생성 버튼
                    else FixedColorButton(
                        onClick = {
                            isLoading = true
                            onNewReportRequested { isLoading = false }
                        },
                        text = "레포트 생성"
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
    val report = ("레포트".repeat(2) + "\n").repeat(10)

    GptAnalyzeReport(
        title = "레포트 제목",
        report = report,
        prohibitMessage = null, // "레포트 생성에 실패했습니다.",
        onNewReportRequested = {},
        onCopyReportToClipboard = {},
    )
}