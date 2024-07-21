package com.example.babbogi.util

import kotlin.random.Random

data class SearchResult (
    val name: String,
    val mainCategory: String,
    val subCategory: String,
    val company: String?,
)


// 테스트용 데이터
fun getRandomTestSearchResult() = SearchResult(
    name = listOf("name", "long long name", "another").random(),
    mainCategory = listOf("닭튀김", "김밥", "빵 및 분식류").random(),
    subCategory = "subCategory${Random.nextInt(1, 3)}",
    company = "company${Random.nextInt(1, 3)}",
)