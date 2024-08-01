package com.example.babbogi.ui.theme

import androidx.compose.material3.Text
import androidx.compose.material3.Typography
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineBreak
import androidx.compose.ui.text.style.TextAlign
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
    ),
)

val nanumFontFamily = FontFamily(
    Font(R.font.nanumsquare_acb),
    /*
    Font(R.font.nanumgothic, FontWeight.Black),
    Font(R.font.nanumgothicbold, FontWeight.Black),
    Font(R.font.nanumgothicextrabold, FontWeight.Black),
    Font(R.font.nanumgothiclight, FontWeight.Black),
    Font(R.font.nanumbarunpenb, FontWeight.Black),
    Font(R.font.nanumbarunpenr, FontWeight.Black),
    Font(R.font.nanumsquare_acb, FontWeight.Black),
    */
)

val BabbogiTypography = Typography(
    titleLarge = TextStyle(
        fontFamily = nanumFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 26.sp,
        textAlign = TextAlign.Center,
        lineBreak = LineBreak.Heading,
    ),
    titleMedium = TextStyle(
        fontFamily = nanumFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp,
        textAlign = TextAlign.Center,
        lineBreak = LineBreak.Heading,
    ),
    bodySmall = TextStyle(
        fontWeight = FontWeight.Light,
        fontSize = 12.sp,
        textAlign = TextAlign.Center,
        color = Color.Gray,
    )
)
