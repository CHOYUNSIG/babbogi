package com.example.babbogi.ui

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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.babbogi.R
import com.example.babbogi.model.BabbogiViewModel
import com.example.babbogi.ui.theme.BabbogiTheme
import com.example.babbogi.ui.view.ColumnWithDefault
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue

@Composable
fun GuidePageScreen(viewModel: BabbogiViewModel, navController: NavController) {
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
            navController.popBackStack()
        }
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CustomSlider(
    modifier: Modifier = Modifier,
    sliderList: List<Int>, // Change to List<Int> for drawable resources
    backwardIcon: ImageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
    forwardIcon: ImageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
    dotsSize: Dp = 10.dp,
    imageCornerRadius: Dp = 16.dp,
    onComplete: () -> Unit
) {
    val pagerState = rememberPagerState(pageCount = { sliderList.size })
    val scope = rememberCoroutineScope()

    ColumnWithDefault {
        HorizontalPager(
            verticalAlignment = Alignment.Top,
            state = pagerState,
            modifier = modifier.weight(1f)
        ) { page ->
            val pageOffset = (pagerState.currentPage - page) + pagerState.currentPageOffsetFraction
            val scaleFactor = 0.75f + (1f - 0.75f) * (1f - pageOffset.absoluteValue)
            Box(
                modifier = modifier
                    .graphicsLayer {
                        scaleX = scaleFactor
                        scaleY = scaleFactor
                    }
                    .alpha(scaleFactor.coerceIn(0f, 1f))
                    .wrapContentHeight()
                    .clip(RoundedCornerShape(imageCornerRadius))
            ) {
                Image(
                    painter = painterResource(id = sliderList[page]),
                    contentDescription = "Image",
                    contentScale = ContentScale.Fit,
                )
            }
        }
        Row(
            modifier = modifier.height(50.dp).fillMaxWidth(),
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

    Box(
        contentAlignment = Alignment.TopEnd,
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp)
    ) {
        IconButton(
            onClick = onComplete,
            modifier = Modifier.size(75.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.baseline_skip_24),
                contentDescription = "Skip",
                modifier = Modifier.size(75.dp)
            )
        }
    }
}

@Composable
fun GuidePage(
    list: List<Int>,
    onComplete: () -> Unit,
) {
    CustomSlider(sliderList = list, onComplete = onComplete)
}

@Preview
@Composable
fun PreviewGuidePage() {
    BabbogiTheme {
        Scaffold {
            Box(modifier = Modifier.padding(it)) {
                GuidePage(
                    list = listOf(
                        R.drawable.guide_page1,
                        R.drawable.guide_page2,
                        R.drawable.guide_page3,
                        R.drawable.guide_page4,
                        R.drawable.guide_page5,
                        R.drawable.guide_page6,
                    ),
                    onComplete = {},
                )
            }
        }
    }
}