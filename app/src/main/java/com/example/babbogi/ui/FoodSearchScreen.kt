package com.example.babbogi.ui

import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.babbogi.model.BabbogiViewModel
import com.example.babbogi.network.response.ServerSearchResultFormat
import com.example.babbogi.ui.theme.BabbogiTheme
import com.example.babbogi.ui.view.ColumnWithDefault
import com.example.babbogi.ui.view.CustomPopup
import com.example.babbogi.ui.view.DescriptionText
import com.example.babbogi.ui.view.PreviewCustomNavigationBar
import com.example.babbogi.ui.view.SearchBar
import com.example.babbogi.ui.view.TitleBar

@Composable
fun FoodSearchScreen(
    viewModel: BabbogiViewModel,
    navController: NavController,
    showSnackBar: (message: String, actionLabel: String, duration: SnackbarDuration) -> Unit
) {
    var searchResult by remember { mutableStateOf<List<ServerSearchResultFormat>>(emptyList()) }

    FoodSearch(
        searchResult = searchResult,
        onSearchWordSubmitted = { word, onEnded ->
            viewModel.searchWord(word) {
                if (it != null) searchResult = it
                else showSnackBar(
                    "오류: 음식을 검색하지 못했습니다.",
                    "확인",
                    SnackbarDuration.Short
                )
                onEnded()
            }
        },
        onWordSelected = lambda@ { word, onEnded ->
            if (word.length < 2) {
                showSnackBar("오류: 두 글자 이상 입력하세요.", "확인", SnackbarDuration.Short)
                return@lambda
            }
            viewModel.getProductByNameSearch(word) {
                if (it != null) viewModel.addProduct(it)
                onEnded()
                showSnackBar(
                    if (it != null) "음식이 추가되었습니다." else "오류: 음식을 추가할 수 없습니다.",
                    "확인",
                    SnackbarDuration.Short
                )
            }
        },
    )
}

@Composable
fun FoodSearch(
    searchResult: List<ServerSearchResultFormat>,
    onSearchWordSubmitted: (String, onEnded: () -> Unit) -> Unit,
    onWordSelected: (String, onEnded: () -> Unit) -> Unit,
) {
    var word by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var selectedWord by remember { mutableStateOf("") }
    var showConfirmingPopup by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxHeight()) {
        TitleBar(title = "음식 검색")
        Box {
            ColumnWithDefault {
                SearchBar(
                    value = word,
                    onSubmit = {
                        isLoading = true
                        onSearchWordSubmitted(word) { isLoading = false }
                    },
                    onValueChange = { word = it }
                )
                LazyColumn(modifier = Modifier.wrapContentHeight()) {
                    items(count = searchResult.size) { index ->
                        ClickableText(
                            text = AnnotatedString(searchResult[index].name),
                            onClick = { _ -> selectedWord = searchResult[index].name; showConfirmingPopup = true },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            style = TextStyle(color = MaterialTheme.colorScheme.onPrimary)
                        )
                        HorizontalDivider()
                    }
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    DescriptionText(
                        text = "Tip!\n" +
                                "두 글자 이상 검색하세요.\n" +
                                "음식 이름을 입력하여 검색해보세요.\n" +
                                "예) '김치', '된장찌개'",
                    )
                }
            }

            if (isLoading) Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                CircularProgressIndicator(modifier = Modifier.size(50.dp))
            }
        }
    }
    
    if (showConfirmingPopup) CustomPopup(
        callbacks = listOf(
            {
                onWordSelected(selectedWord) { isLoading = false }
                showConfirmingPopup = false
            },
            { showConfirmingPopup = false },
        ),
        labels = listOf("추가", "취소"),
        onDismiss = { showConfirmingPopup = false },
        title = "다음 상품을 추가하시겠습니까?",
    ) {
        Text(text = selectedWord)
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(uiMode = UI_MODE_NIGHT_NO)
@Preview(uiMode = UI_MODE_NIGHT_YES)
@Composable
fun PreviewFoodSearch() {
    BabbogiTheme {
        Scaffold(bottomBar = { PreviewCustomNavigationBar() }) {
            Box(modifier = Modifier.padding(it)) {
                FoodSearch(
                    searchResult = listOf(),
                    onSearchWordSubmitted = { _, _ -> },
                    onWordSelected = { _, _ -> },
                )
            }
        }
    }
}