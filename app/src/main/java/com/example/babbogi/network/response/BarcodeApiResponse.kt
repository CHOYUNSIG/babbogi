package com.example.babbogi.network.response

import kotlinx.serialization.Serializable

@Serializable
data class BarcodeApiResponse(
    val C005: C005
)

@Serializable
data class C005(
    val total_count: Int,
    val row: List<C005_row>? = null
)

@Serializable
data class C005_row(
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