package com.example.babbogi.ui

import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.babbogi.R
import com.example.babbogi.Screen
import com.example.babbogi.model.BabbogiViewModel
import com.example.babbogi.ui.view.ColumnScreen
import com.example.babbogi.ui.view.FixedColorIconButton
import com.example.babbogi.ui.view.ScreenPreviewer
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun GuidePageScreen(
    viewModel: BabbogiViewModel,
    navController: NavController,
    showSnackBar: (message: String) -> Unit,
    showAlertPopup: (title: String, message: String, icon: Int) -> Unit,
) {
    GuidePage(
        list = listOf(
            R.drawable.guide_page1,
            R.drawable.guide_page2,
            R.drawable.guide_page3,
            R.drawable.guide_page4,
            R.drawable.guide_page5,
            R.drawable.guide_page6,
            R.drawable.guide_page7,
            R.drawable.guide_page8,
            R.drawable.guide_page9,
            R.drawable.guide_page10,
            R.drawable.guide_page11
        ),
        onComplete = {
            viewModel.isTutorialDone = true
            if (viewModel.healthState == null)
                navController.navigate(Screen.HealthProfile.name)
            else
                navController.popBackStack()
        }
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun CustomSlider(
    sliderList: List<Int>,
    dotsSize: Dp = 10.dp,
    imageCornerRadius: Dp = 16.dp
) {
    val pagerState = rememberPagerState(pageCount = { sliderList.size })
    val scope = rememberCoroutineScope()
    var width by remember { mutableStateOf<Dp?>(null) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f, fill = false).run { width?.let { this.width(it) } ?: this },
        ) { page ->
            val pageOffset = (pagerState.currentPage - page) + pagerState.currentPageOffsetFraction
            val scaleFactor = 0.75f + (1f - 0.75f) * (1f - pageOffset.absoluteValue)
            Card(
                modifier = Modifier
                    .graphicsLayer {
                        scaleX = scaleFactor
                        scaleY = scaleFactor
                    },
                shape = RoundedCornerShape(imageCornerRadius),
            ) {
                val density = LocalDensity.current
                Image(
                    painter = painterResource(id = sliderList[page]),
                    contentDescription = "Image",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .weight(1f, fill = false)
                        .onGloballyPositioned { width = density.run { it.size.width.toDp() } },
                )
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(sliderList.size) {
                Box(
                    modifier = Modifier
                        .padding(2.dp)
                        .clip(CircleShape)
                        .size(dotsSize)
                        .background(if (pagerState.currentPage == it) Color.LightGray else Color.DarkGray)
                        .clickable { scope.launch { pagerState.animateScrollToPage(it) } }
                )
            }
        }
    }
}

@Composable
private fun GuidePage(
    list: List<Int>,
    onComplete: () -> Unit,
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        // 이미지 슬라이더
        CustomSlider(sliderList = list)

        // 스킵 버튼을 이미지 위에 배치, 투명 배경 처리
        Box(
            contentAlignment = Alignment.TopEnd,
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp, 32.dp)
        ) {
            // FixedColorIconButton의 배경을 제거하고, 버튼만 표시되도록 함
            Image(
                painter = painterResource(id = R.drawable.baseline_skip_24),
                contentDescription = "건너뛰기",
                modifier = Modifier
                    .size(100.dp)
                    .clickable { onComplete() }
            )
        }
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@Preview(uiMode = UI_MODE_NIGHT_NO)
@Preview(uiMode = UI_MODE_NIGHT_YES)
@Composable
fun PreviewGuidePage() {
    val list = remember {
        listOf(
            R.drawable.guide_page1,
            R.drawable.guide_page2,
            R.drawable.guide_page3,
            R.drawable.guide_page4,
            R.drawable.guide_page5,
            R.drawable.guide_page6,
            R.drawable.guide_page7,
            R.drawable.guide_page8,
            R.drawable.guide_page9,
            R.drawable.guide_page10,
            R.drawable.guide_page11
        )
    }

    ScreenPreviewer(screen = Screen.Tutorial, showTitleBar = false, showNavBar = false) {
        GuidePage(
            list = list,
            onComplete = {},
        )
    }
}