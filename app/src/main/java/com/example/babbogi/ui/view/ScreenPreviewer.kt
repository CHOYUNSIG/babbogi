package com.example.babbogi.ui.view

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.babbogi.Screen
import com.example.babbogi.ui.theme.BabbogiTheme

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ScreenPreviewer(
    screen: Screen,
    showNavBar: Boolean = true,
    showTitleBar: Boolean = true,
    content: @Composable () -> Unit,
) {
    BabbogiTheme {
        Scaffold(
            topBar = { if (showTitleBar) screen.title?.let { TitleBar(title = it) } },
            bottomBar = { if (showNavBar) PreviewCustomNavigationBar() }
        ) {
            Box(modifier = Modifier.padding(it)) {
                content()
            }
        }
    }
}