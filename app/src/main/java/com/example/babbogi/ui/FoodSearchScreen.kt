package com.example.babbogi.ui

import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.babbogi.R
import com.example.babbogi.model.BabbogiViewModel
import com.example.babbogi.ui.theme.BabbogiTheme
import com.example.babbogi.ui.view.ColumnWithDefault
import com.example.babbogi.ui.view.CustomPopup
import com.example.babbogi.ui.view.DescriptionText
import com.example.babbogi.ui.view.DropDown
import com.example.babbogi.ui.view.ElevatedCardWithDefault
import com.example.babbogi.ui.view.PreviewCustomNavigationBar
import com.example.babbogi.ui.view.SearchBar
import com.example.babbogi.ui.view.TitleBar
import com.example.babbogi.util.SearchResult
import com.example.babbogi.util.getRandomTestSearchResult

@Composable
fun FoodSearchScreen(
    viewModel: BabbogiViewModel,
    navController: NavController,
    showSnackBar: (message: String) -> Unit,
    showAlertPopup: (title: String, message: String, icon: Int) -> Unit,
) {
    FoodSearch(
        onSearchWordSubmitted = lambda@ { word, onEnded ->
            if (word.length < 2) {
                showAlertPopup(
                    "짧은 검색어",
                    "두 글자 이상 입력하세요.",
                    R.drawable.baseline_cancel_24,
                )
                onEnded(null)
            }
            else viewModel.searchWord(word) {
                if (it == null) showAlertPopup(
                    "검색 실패",
                    "음식을 검색하지 못했습니다.",
                    R.drawable.baseline_cancel_24,
                )
                onEnded(it)
            }
        },
        onFoodSelected = { id, onEnded ->
            viewModel.getProductByID(id) {
                if (it != null) {
                    viewModel.addProduct(it)
                    showSnackBar("음식이 추가되었습니다.")
                }
                else {
                    showAlertPopup(
                        "음식 추가 실패",
                        "음식을 추가하지 못했습니다.",
                        R.drawable.baseline_cancel_24,
                    )
                }
                onEnded()
            }
        },
    )
}

@Composable
private fun FoodSearch(
    onSearchWordSubmitted: (String, onEnded: (List<SearchResult>?) -> Unit) -> Unit,
    onFoodSelected: (id: String, onEnded: () -> Unit) -> Unit,
) {
    var word by remember { mutableStateOf("") }
    var searchResult by remember { mutableStateOf<List<SearchResult>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var selectedResult by remember { mutableStateOf<SearchResult?>(null) }

    // 필터링된 검색 결과 리스트
    var filteredSearchResult by remember { mutableStateOf(searchResult) }

    Column(modifier = Modifier.fillMaxHeight()) {
        TitleBar(title = "음식 검색")
        Box {
            ColumnWithDefault {
                SearchBar(
                    value = word,
                    onSubmit = {
                        isLoading = true
                        onSearchWordSubmitted(word) {
                            isLoading = false
                            if (it != null) searchResult = it
                        }
                    },
                    onValueChange = { word = it }
                )
                if (searchResult.isNotEmpty()) FoodFiltering(
                    searchResult = searchResult,
                    onFiltered = { filteredSearchResult = it }
                )
                LazyColumn(modifier = Modifier.wrapContentHeight()) {
                    items(count = filteredSearchResult.size) { index ->
                        val result = filteredSearchResult[index]
                        Column(
                            modifier = Modifier
                                .padding(8.dp)
                                .clickable { selectedResult = result },
                            verticalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            Text(text = result.name, fontSize = 16.sp)
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                DescriptionText(text = "${result.mainCategory} > ${result.subCategory}")
                                DescriptionText(text = result.company ?: "")
                            }
                        }
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
    
    selectedResult?.let { result ->
        CustomPopup(
            callbacks = listOf(
                {
                    onFoodSelected(result.id) { isLoading = false }
                    selectedResult = null
                },
                { selectedResult = null },
            ),
            labels = listOf("추가", "취소"),
            onDismiss = { selectedResult = null },
            title = "다음 상품을 추가하시겠습니까?",
        ) {
            Text(text = result.name)
        }
    }
}

@Composable
private fun FoodFiltering(
    searchResult: List<SearchResult>,
    onFiltered: (List<SearchResult>) -> Unit,
) {
    val mainCategories by remember(searchResult) {
        mutableStateOf(searchResult.map { it.mainCategory }.distinct())
    }
    var selectedMainCategory by remember(searchResult) { mutableStateOf<String?>(null) }
    val filteredResultByMain by remember(searchResult, selectedMainCategory) {
        mutableStateOf(
            searchResult.filter { selectedMainCategory == null || it.mainCategory == selectedMainCategory }
        )
    }
    // var isMainExpanded by remember(filteredResultByMain) { mutableStateOf(false) }

    val subCategories by remember(filteredResultByMain) {
        mutableStateOf(filteredResultByMain.map { it.subCategory }.distinct())
    }
    var selectedSubCategory by remember(filteredResultByMain) { mutableStateOf<String?>(null) }
    val filteredResultBySub by remember(filteredResultByMain, selectedSubCategory) {
        mutableStateOf(
            filteredResultByMain.filter { selectedSubCategory == null || it.subCategory == selectedSubCategory }
        )
    }
    // var isSubExpanded by remember(filteredResultBySub) { mutableStateOf(false) }

    LaunchedEffect(filteredResultBySub) {
        onFiltered(filteredResultBySub)
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                DropDown(
                    options = mainCategories,
                    nullOption = "대분류 전체",
                    index = remember(mainCategories) { null },
                    onChange = { selectedMainCategory = it?.let { mainCategories[it] } }
                )
                // Text(
                //     text = selectedMainCategory ?: "전체",
                //     modifier = Modifier
                //         .clickable {
                //             isMainExpanded = !isMainExpanded
                //             if (isMainExpanded) isSubExpanded = false
                //         }
                //         .padding(8.dp)
                // )
                // if (isMainExpanded) {
                //     Column(modifier = Modifier.background(Color.Gray.copy(alpha = 0.1f))) {
                //         (listOf("전체") + mainCategories).forEach { category ->
                //             Text(
                //                 text = category,
                //                 modifier = Modifier
                //                     .clickable {
                //                         selectedMainCategory =
                //                             if (category == "전체") null else category
                //                         selectedSubCategory = null
                //                         isMainExpanded = false
                //                     }
                //                     .padding(8.dp)
                //             )
                //         }
                //     }
                // }
            }
            Text(">", modifier = Modifier.padding(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                DropDown(
                    options = subCategories,
                    nullOption = "소분류 전체",
                    index = remember(subCategories) { null },
                    onChange = { selectedSubCategory = it?.let { subCategories[it] } }
                )
                // Text(
                //     text = if (selectedMainCategory == null) "" else selectedSubCategory ?: "전체",
                //     modifier = Modifier
                //         .clickable {
                //             selectedMainCategory?.let {
                //                 isSubExpanded = !isSubExpanded
                //                 if (isSubExpanded) isMainExpanded = false
                //             }
                //         }
                //         .padding(8.dp)
                // )
                // if (isSubExpanded && selectedMainCategory != null) {
                //     Column(modifier = Modifier.background(Color.Gray.copy(alpha = 0.1f))) {
                //         (listOf("전체") + subCategories).forEach { category ->
                //             Text(
                //                 text = category,
                //                 modifier = Modifier
                //                     .clickable {
                //                         selectedSubCategory =
                //                             if (category == "전체") null else category
                //                         isSubExpanded = false
                //                     }
                //                     .padding(8.dp)
                //             )
                //         }
                //     }
                // }
            }
        }
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
                    onSearchWordSubmitted = { _, onEnded ->
                        onEnded(List(100) { getRandomTestSearchResult() } )
                    },
                    onFoodSelected = { _, onEnded -> onEnded() },
                )
            }
        }
    }
}