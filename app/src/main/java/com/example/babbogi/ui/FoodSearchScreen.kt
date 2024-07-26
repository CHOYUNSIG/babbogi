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
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
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
import com.example.babbogi.ui.view.PreviewCustomNavigationBar
import com.example.babbogi.ui.view.SearchBar
import com.example.babbogi.ui.view.TitleBar
import com.example.babbogi.util.SearchResult
import com.example.babbogi.util.getRandomTestSearchResult

@Composable
fun FoodSearchScreen(
    viewModel: BabbogiViewModel,
    navController: NavController,
    showSnackBar: (message: String, actionLabel: String, duration: SnackbarDuration) -> Unit
) {
    var searchResult by remember { mutableStateOf<List<SearchResult>>(emptyList()) }

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
                if (it != null) {
                    viewModel.addProduct(it)
                    showSnackBar(
                        "음식이 추가되었습니다.",
                        "확인",
                        SnackbarDuration.Short
                    )
                }
                onEnded(it != null)
            }
        },
    )
}

@Composable
private fun FoodSearch(
    searchResult: List<SearchResult>,
    onSearchWordSubmitted: (String, onEnded: () -> Unit) -> Unit,
    onWordSelected: (String, onEnded: (success: Boolean) -> Unit) -> Unit,
) {
    var word by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var selectedWord by remember { mutableStateOf("") }
    var showConfirmingPopup by remember { mutableStateOf(false) }
    var showCannotAddAlert by remember { mutableStateOf(false) }

    // 필터 상태 추가
    var selectedMainCategory by remember { mutableStateOf<String?>(null) }
    var selectedSubCategory by remember { mutableStateOf<String?>(null) }

    val mainCategories = searchResult.map { it.mainCategory }.distinct()
    val subCategories = searchResult.map { it.subCategory }.distinct()

    // 필터링된 검색 결과 리스트
    val filteredSearchResult = searchResult.filter { result ->
        val matchesMainCategory = selectedMainCategory?.let { result.mainCategory == it } ?: true
        val matchesSubCategory = selectedSubCategory?.let { result.subCategory == it } ?: true
        matchesMainCategory && matchesSubCategory
    }

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

                FoodFiltering(
                    mainCategories = mainCategories,
                    subCategories = subCategories,
                    selectedMainCategory = selectedMainCategory,
                    selectedSubCategory = selectedSubCategory,
                    onMainCategoryChange = { selectedMainCategory = it },
                    onSubCategoryChange = { selectedSubCategory = it }
                )

                LazyColumn(modifier = Modifier.wrapContentHeight()) {
                    items(count = filteredSearchResult.size) { index ->
                        Column(
                            modifier = Modifier.padding(8.dp).clickable {
                                selectedWord = filteredSearchResult[index].name
                                showConfirmingPopup = true
                            },
                            verticalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            Text(text = AnnotatedString(searchResult[index].name), fontSize = 16.sp)
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                DescriptionText(
                                    text = "${filteredSearchResult[index].mainCategory} > ${filteredSearchResult[index].subCategory}",
                                )
                                DescriptionText(text = filteredSearchResult[index].company ?: "")
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
    
    if (showConfirmingPopup) CustomPopup(
        callbacks = listOf(
            {
                showConfirmingPopup = false
                onWordSelected(selectedWord) {
                    if (!it) showCannotAddAlert = true
                    isLoading = false
                }
            },
            { showConfirmingPopup = false },
        ),
        labels = listOf("추가", "취소"),
        onDismiss = { showConfirmingPopup = false },
        title = "다음 상품을 추가하시겠습니까?",
    ) {
        Text(text = selectedWord)
    }

    if (showCannotAddAlert) CustomPopup(
        callbacks = listOf { showCannotAddAlert = false },
        labels = listOf("확인"),
        onDismiss = { showCannotAddAlert = false },
        title = "오류",
        icon = R.drawable.baseline_cancel_24
    ) {
        Text(text = "음식을 추가할 수 없습니다.")
    }

}

@Composable
fun FoodFiltering(
    mainCategories: List<String>,
    subCategories: List<String>,
    selectedMainCategory: String?,
    selectedSubCategory: String?,
    onMainCategoryChange: (String?) -> Unit,
    onSubCategoryChange: (String?) -> Unit
) {
    var isMainExpanded by remember { mutableStateOf(false) }
    var isSubExpanded by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = selectedMainCategory ?: "전체",
                modifier = Modifier
                    .clickable {
                        isMainExpanded = !isMainExpanded
                        if (isMainExpanded) isSubExpanded = false // Close subcategory menu if open
                    }
                    .padding(8.dp)
            )
            Text(" > ")
            Text(
                text = if (selectedMainCategory == null) "" else selectedSubCategory ?: "전체",
                modifier = Modifier
                    .clickable {
                        if (selectedMainCategory != null) {
                            isSubExpanded = !isSubExpanded
                            if (isSubExpanded) isMainExpanded = false // Close main category menu if open
                        }
                    }
                    .padding(8.dp)
            )
        }
        if (isMainExpanded) {
            Column(modifier = Modifier.background(Color.Gray.copy(alpha = 0.1f))) {
                (listOf("전체") + mainCategories).forEach { category ->
                    Text(
                        text = category,
                        modifier = Modifier
                            .clickable {
                                onMainCategoryChange(if (category == "전체") null else category)
                                onSubCategoryChange(null) // Reset subcategory when main category changes
                                isMainExpanded = false
                            }
                            .padding(8.dp)
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        if (isSubExpanded && selectedMainCategory != null) {
            Column(modifier = Modifier.background(Color.Gray.copy(alpha = 0.1f))) {
                (listOf("전체") + subCategories).forEach { category ->
                    Text(
                        text = category,
                        modifier = Modifier
                            .clickable {
                                onSubCategoryChange(if (category == "전체") null else category)
                                isSubExpanded = false
                            }
                            .padding(8.dp)
                    )
                }
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
                    searchResult = List(10) { getRandomTestSearchResult() },
                    onSearchWordSubmitted = { _, onEnded -> onEnded() },
                    onWordSelected = { _, onEnded -> onEnded(true) },
                )
            }
        }
    }
}