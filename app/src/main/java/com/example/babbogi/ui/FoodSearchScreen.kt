package com.example.babbogi.ui

import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.os.Build
import androidx.annotation.RequiresApi
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.babbogi.R
import com.example.babbogi.Screen
import com.example.babbogi.model.BabbogiViewModel
import com.example.babbogi.ui.theme.BabbogiTypography
import com.example.babbogi.ui.view.ColumnScreen
import com.example.babbogi.ui.view.CustomPopup
import com.example.babbogi.ui.view.DropDown
import com.example.babbogi.ui.view.ProductAbstraction
import com.example.babbogi.ui.view.ScreenPreviewer
import com.example.babbogi.ui.view.SearchBar
import com.example.babbogi.ui.view.TextInputHolder
import com.example.babbogi.util.Product
import com.example.babbogi.util.SearchResult
import com.example.babbogi.util.getRandomTestProduct
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
            viewModel.getProductByID(id) { onEnded(it) }
        },
        onFoodAdded = { product, ratio ->
            viewModel.addProduct(product, intakeRatio = ratio)
            showSnackBar("음식이 추가되었습니다.")
        }
    )
}

@Composable
private fun FoodSearch(
    onSearchWordSubmitted: (String, onEnded: (List<SearchResult>?) -> Unit) -> Unit,
    onFoodSelected: (id: String, onEnded: (Product?) -> Unit) -> Unit,
    onFoodAdded: (Product, intakeRatio: Float) -> Unit,
) {
    var word by remember { mutableStateOf("") }
    var searchResult by remember { mutableStateOf<List<SearchResult>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var selectedResult by remember { mutableStateOf<SearchResult?>(null) }
    var isFoodPreviewLoading by remember { mutableStateOf(false) }
    var previewFood by remember { mutableStateOf<Product?>(null) }

    // 필터링된 검색 결과 리스트
    var filteredSearchResult by remember { mutableStateOf(searchResult) }

    ColumnScreen(prohibitScroll = true) {
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
        if (searchResult.isNotEmpty()) FoodFilter(
            searchResult = searchResult,
            onFiltered = { filteredSearchResult = it }
        )
        Box {
            LazyColumn {
                items(count = filteredSearchResult.size) { index ->
                    val result = filteredSearchResult[index]
                    Column(
                        modifier = Modifier
                            .padding(8.dp)
                            .clickable {
                                isFoodPreviewLoading = true
                                onFoodSelected(result.id) {
                                    previewFood = it
                                    isFoodPreviewLoading = false
                                }
                                selectedResult = result
                            },
                        verticalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Text(text = result.name, fontSize = 16.sp)
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(text = "${result.mainCategory} > ${result.subCategory}", style = BabbogiTypography.bodySmall)
                            Text(text = result.company ?: "", style = BabbogiTypography.bodySmall)
                        }
                    }
                    HorizontalDivider()
                }
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp),
                        contentAlignment = Alignment.BottomCenter
                    ) {
                        Text(
                            text = "Tip!\n" +
                                    "두 글자 이상 검색하세요.\n" +
                                    "음식 이름을 입력하여 검색해보세요.\n" +
                                    "예) '김치', '된장찌개'",
                            style = BabbogiTypography.bodySmall,
                        )
                    }
                }
            }
            if (isLoading) Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(modifier = Modifier.size(100.dp))
            }
        }
    }

    selectedResult?.let { result ->
        FoodPreviewPopup(
            result = result,
            onStarted = { onEnded -> onFoodSelected(result.id) { onEnded(it) } },
            onDismiss = { selectedResult = null },
            onAddClicked = onFoodAdded
        )
    }
}

@Composable
private fun FoodFilter(
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

    val subCategories by remember(filteredResultByMain) {
        mutableStateOf(filteredResultByMain.map { it.subCategory }.distinct())
    }
    var selectedSubCategory by remember(filteredResultByMain) { mutableStateOf<String?>(null) }
    val filteredResultBySub by remember(filteredResultByMain, selectedSubCategory) {
        mutableStateOf(
            filteredResultByMain.filter { selectedSubCategory == null || it.subCategory == selectedSubCategory }
        )
    }

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
            }
            Text(">", modifier = Modifier.padding(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                DropDown(
                    options = subCategories,
                    nullOption = "소분류 전체",
                    index = remember(subCategories) { null },
                    onChange = { selectedSubCategory = it?.let { subCategories[it] } }
                )
            }
        }
    }
}

@Composable
fun FoodPreviewPopup(
    result: SearchResult,
    onStarted: (onEnded: (Product?) -> Unit) -> Unit,
    onDismiss: () -> Unit,
    onAddClicked: (Product, intakeRatio: Float) -> Unit,
) {
    var isLoading by remember { mutableStateOf(true) }
    var loadedFood by remember { mutableStateOf<Product?>(null) }
    var callbacks by remember { mutableStateOf(listOf {}) }
    var labels by remember { mutableStateOf(listOf("취소")) }
    var title by remember { mutableStateOf("정보를 불러오는 중..") }
    var ratioText by remember { mutableStateOf("100") }
    var isError by remember { mutableStateOf(false) }

    LaunchedEffect(result) {
        onStarted {
            loadedFood = it
            isLoading = false
            if (it != null) {
                callbacks = listOf({
                    val ratio = ratioText.toFloatOrNull()
                    if (ratio != null) onAddClicked(it, ratio / 100)
                    else isError = true
                }, {})
                labels = listOf("확인", "취소")
                title = "다음 식품을 추가하시겠습니까?"
            }
            else {
                title = "오류"
            }
        }
    }

    CustomPopup(
        callbacks = callbacks,
        labels = labels,
        onDismiss = onDismiss,
        title = title,
    ) {
        if (isLoading)
            CircularProgressIndicator(modifier = Modifier.size(50.dp))
        else loadedFood?.let { food ->
            ProductAbstraction(product = food) {
                Text(text = "%.1fg 기준".format(food.servingSize), style = BabbogiTypography.bodySmall)
            }
            TextInputHolder(
                content = "섭취량",
                value = ratioText,
                onValueChange = { ratioText = it },
                labeling = "섭취량을 입력하세요.",
                keyboardType = KeyboardType.Number,
                unit = "%",
                isError = isError,
            )
        } ?: Text(text = "정보를 불러오지 못했습니다.")
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(uiMode = UI_MODE_NIGHT_NO)
@Preview(uiMode = UI_MODE_NIGHT_YES)
@Composable
fun PreviewFoodSearch() {
    ScreenPreviewer(screen = Screen.FoodSearch) {
        FoodSearch(
            onSearchWordSubmitted = { _, onEnded ->
                onEnded(List(100) { getRandomTestSearchResult() } )
            },
            onFoodSelected = { _, onEnded -> onEnded(getRandomTestProduct()) },
            onFoodAdded = { _, _ -> },
        )
    }
}