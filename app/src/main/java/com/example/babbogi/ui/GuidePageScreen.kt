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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.babbogi.R
import com.example.babbogi.Screen
import com.example.babbogi.model.BabbogiViewModel
import com.example.babbogi.ui.view.ColumnScreen
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
    modifier: Modifier = Modifier,
    sliderList: List<Int>,
    dotsSize: Dp = 10.dp,
    imageCornerRadius: Dp = 16.dp
) {
    val pagerState = rememberPagerState(pageCount = { sliderList.size })
    val scope = rememberCoroutineScope()

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            val pageOffset = (pagerState.currentPage - page) + pagerState.currentPageOffsetFraction
            val scaleFactor = 0.75f + (1f - 0.75f) * (1f - pageOffset.absoluteValue)
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .graphicsLayer {
                        scaleX = scaleFactor
                        scaleY = scaleFactor
                    }
                    .alpha(scaleFactor.coerceIn(0f, 1f))
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .clip(RoundedCornerShape(imageCornerRadius))
            ) {
                Image(
                    painter = painterResource(id = sliderList[page]),
                    contentDescription = "Image",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.fillMaxWidth().wrapContentHeight()
                )
            }
        }
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp) // 하단 여백 설정
                .height(50.dp)
                .fillMaxWidth(),
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
    ColumnScreen(prohibitScroll = true) {
        CustomSlider(modifier = Modifier.fillMaxWidth(), sliderList = list)
    }

    Box(
        contentAlignment = Alignment.TopEnd,
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp)
    ) {
        IconButton(
            onClick = onComplete,
            modifier = Modifier.size(75.dp),
            colors = IconButtonDefaults.iconButtonColors(contentColor = Color.Black)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.baseline_skip_24),
                contentDescription = "Skip",
                modifier = Modifier.size(75.dp)
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
        )
    }

    ScreenPreviewer(screen = Screen.Tutorial, showTitleBar = false, showNavBar = false) {
        GuidePage(
            list = list,
            onComplete = {},
        )
    }
}