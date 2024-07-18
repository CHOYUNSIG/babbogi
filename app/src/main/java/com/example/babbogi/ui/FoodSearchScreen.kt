package com.example.babbogi.ui

import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import com.example.babbogi.ui.theme.BabbogiTheme
import com.example.babbogi.ui.view.ColumnWithDefault
import com.example.babbogi.ui.view.PreviewCustomNavigationBar
import com.example.babbogi.ui.view.SearchBar
import com.example.babbogi.ui.view.TitleBar

@Composable
fun FoodSearchScreen(viewModel: BabbogiViewModel, navController: NavController) {
    var searchResult by remember { mutableStateOf<List<String>>(emptyList()) }

    FoodSearch(
        searchResult = searchResult,
        onSearchWordSubmitted = { word, onEnded ->
            viewModel.searchWord(word) {
                if (it != null) searchResult = it
                onEnded()
            }
        },
        onWordSelected = { word ->
            viewModel.getProductByNameSearch(word) {
                if (it != null) viewModel.addProduct(it)
            }
        },
    )
}

@Composable
fun FoodSearch(
    searchResult: List<String>,
    onSearchWordSubmitted: (String, onEnded: () -> Unit) -> Unit,
    onWordSelected: (String) -> Unit,
) {
    var word by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

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
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    searchResult.forEach {
                        ClickableText(
                            text = AnnotatedString(it),
                            onClick = { _ -> onWordSelected(it) },
                            modifier = Modifier.padding(16.dp),
                            style = TextStyle(color = MaterialTheme.colorScheme.onPrimary)
                        )
                        HorizontalDivider()
                    }
                }
                //serch Tip 코드
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    SearchTip()
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
}

@Composable
fun SearchTip() {
    Box(modifier = Modifier
        .padding(16.dp)
        .fillMaxWidth()
    ) {
        Text(
            text = "Tip  " +
                    "\n 두 글자 이상 검색하시오." +
                    "\n (예시)음식 이름을 입력하여 검색해보세요." +
                    "\n예: '김치', '된장찌개'",
            style = TextStyle(color = MaterialTheme.colorScheme.primary)
        )
    }
}

@Preview(uiMode = UI_MODE_NIGHT_NO)
@Preview(uiMode = UI_MODE_NIGHT_YES)
@Composable
fun PreviewFoodSearch() {
    BabbogiTheme {
        Scaffold(bottomBar = { PreviewCustomNavigationBar() }) {
            Box(modifier = Modifier.padding(it)) {
                FoodSearch(
                    searchResult = listOf(
                        "김치",
                        "김치찌개",
                        "김치전",
                        "김치볶음밥",
                        "갓김치",
                        "열무김치",
                    ),
                    onSearchWordSubmitted = { _, _ -> },
                    onWordSelected = {},
                )
            }
        }
    }
}