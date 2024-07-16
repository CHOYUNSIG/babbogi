package com.example.babbogi.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.babbogi.model.BabbogiViewModel
import com.example.babbogi.ui.theme.BabbogiTheme
import com.example.babbogi.ui.view.ColumnWithDefault
import com.example.babbogi.ui.view.SearchBar
import com.example.babbogi.ui.view.TitleBar

@Composable
fun FoodSearchScreen(viewModel: BabbogiViewModel, navController: NavController) {
    var searchResult by remember { mutableStateOf<List<String>>(emptyList()) }

    FoodSearch(
        searchResult = searchResult,
        onSearchWordSubmitted = { /* TODO */ },
        onWordSelected = { /* TODO */ },
    )
}

@Composable
fun FoodSearch(
    searchResult: List<String>,
    onSearchWordSubmitted: (String) -> Unit,
    onWordSelected: (String) -> Unit,
) {
    var word by remember { mutableStateOf("") }

    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
        TitleBar(title = "음식 검색")
        ColumnWithDefault {
            SearchBar(
                value = word,
                onSubmit = onSearchWordSubmitted,
                onValueChange = { word = it }
            )
            Column {
                searchResult.forEach {
                    ClickableText(
                        text = AnnotatedString(it),
                        onClick = { _ -> onWordSelected(it) },
                        modifier = Modifier.padding(16.dp)
                    )
                    HorizontalDivider()
                }
            }
        }
    }
}

@Preview
@Composable
fun PreviewFoodSearch() {
    BabbogiTheme {
        Scaffold {
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
                    onSearchWordSubmitted = {},
                    onWordSelected = {},
                )
            }
        }
    }
}