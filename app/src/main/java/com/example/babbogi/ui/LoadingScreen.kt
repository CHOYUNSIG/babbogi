package com.example.babbogi.ui

import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.babbogi.R
import com.example.babbogi.model.BabbogiViewModel
import com.example.babbogi.ui.theme.BabbogiTheme

@Composable
fun LoadingScreen(
    viewModel: BabbogiViewModel,
    navController: NavController,
    showSnackBar: (message: String, actionLabel: String, duration: SnackbarDuration) -> Unit
) {
    Loading()
}

@Composable
fun Loading() {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(resId = R.raw.loading))

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        LottieAnimation(
            composition = composition,
            iterations = LottieConstants.IterateForever,
            modifier = Modifier.size(150.dp)
        )
    }
}

@Preview(uiMode = UI_MODE_NIGHT_NO)
@Preview(uiMode = UI_MODE_NIGHT_YES)
@Composable
fun PreviewLoading() {
    BabbogiTheme {
        Scaffold {
            Box(modifier = Modifier.padding(it)) {
                Loading()
            }
        }
    }
}