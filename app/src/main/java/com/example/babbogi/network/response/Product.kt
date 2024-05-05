package com.example.babbogi.network.response

import kotlinx.serialization.Serializable

@Serializable
data class BarcodeApiResponse(
    val C005: ProductList
)

@Serializable
data class ProductList(
    val total_count: Int,
    val row: List<Product>? = null
)

@Serializable
data class Product(
    val PRDLST_REPORT_NO: String,
    val PRMS_DT: String,
    val END_DT: String,
    val PRDLST_NM: String,
    val POG_DAYCNT: String,
    val PRDLST_DCNM: String,
    val BSSH_NM: String,
    val INDUTY_NM: String,
    val SITE_ADDR: String,
    val CLSBIZ_DT: String,
    val BAR_CD: String
)