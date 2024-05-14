package com.example.babbogi.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.babbogi.R

// Set of Material typography styles to start with
val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    )
    /* Other default text styles to override
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
    */
)

val nanumFontFamily = FontFamily(
    Font(R.font.nanumsquare_acb, FontWeight.Normal),
    /*Font(R.font.nanumgothic, FontWeight.Black),
Font(R.font.nanumgothicbold, FontWeight.Black),
Font(R.font.nanumgothicextrabold, FontWeight.Black),
Font(R.font.nanumgothiclight, FontWeight.Black),
Font(R.font.nanumbarunpenb, FontWeight.Black),
Font(R.font.nanumbarunpenr, FontWeight.Black),
Font(R.font.nanumsquare_acb, FontWeight.Black),*/
)