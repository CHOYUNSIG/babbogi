package com.example.babbogi.ui

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.babbogi.R
import java.time.LocalDate

@RequiresApi(Build.VERSION_CODES.O)
@Preview
@Composable
fun MainScreen() {
    Column (modifier = Modifier.background(color = Color.White)) {
        AppName()
        SelectDate()
        InputUserData()
        ListUserMeals()
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
//날짜 선택
fun SelectDate() {
    val today = LocalDate.now()  // 현재 날짜를 가져옴
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,  // 가로 중앙 정렬
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { /*전날 페이지*/ },
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_chevron_left_24),
                    contentDescription = "이전",
                )
            }
            ElevatedCard(
                modifier = Modifier
                    .width(250.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = today.toString(),
                        color = Color.DarkGray,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Normal,
                        textAlign = TextAlign.Center
                    )
                }
            }
            IconButton(onClick = { /*다음날 페이지*/ }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_chevron_right_24),
                    contentDescription = "다음"
                )
            }
        }
    }
}

@Composable
//앱 이름
fun AppName() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = Color.White)
    ) {
        Text(
            "밥보기",
            color = Color.DarkGray,
            fontSize = 26.sp,
            fontWeight = FontWeight.W900
        )
    }
}

@Composable
// 건강정보 추가하기(메인 화면)
fun InputUserData(){
    Box(modifier = Modifier.padding(16.dp)) {
        Canvas(modifier = Modifier.matchParentSize()) {
            val pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
            drawRoundRect(
                color = Color.Black,
                topLeft = Offset.Zero,
                size = Size(size.width, size.height),
                cornerRadius = CornerRadius(0f, 0f),
                style = Stroke(width = 2.dp.toPx(), pathEffect = pathEffect),
                alpha = 0.5f
            )
        }
        ElevatedButton(
            onClick = { /*TODO*/ },
            modifier = Modifier
                .fillMaxWidth(),
            shape = RectangleShape,
            colors = ButtonDefaults.elevatedButtonColors(
                containerColor = Color.White,
            ),
            elevation = ButtonDefaults.elevatedButtonElevation(
                defaultElevation = 4.dp,  // 기본 고도
                pressedElevation = 8.dp,  // 버튼이 눌렸을 때의 고도
                focusedElevation = 6.dp,  // 포커스가 맞춰졌을 때의 고도
                hoveredElevation = 6.dp   // 호버링 했을 때의 고도
            )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = Color(0xEDEFED))
            ) {
                Column {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            tint = Color.Green,
                            modifier = Modifier
                                .size(160.dp)
                                .padding(20.dp)
                                .alpha(0.4f),
                            painter = painterResource(id = R.drawable.ic_add_box_24),
                            contentDescription = "정보 추가하기 아이콘"
                        )
                    }

                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "사용자 건강 정보 추가하기",
                            color = Color.Black.copy(alpha = 0.5f),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Normal,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ListUserMeals(){
    ElevatedCard(
        modifier = Modifier
            .padding(16.dp)
            .background(color = Color.White),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 5.dp)
    )
    {
        Column {
            Box(
                contentAlignment = Alignment.TopStart,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "사용자 식사 정보",
                        color = Color.Black.copy(alpha = 0.5f),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.W600,
                    )
                    IconButton(
                        onClick = { /* TODO */ },
                        modifier = Modifier.size(48.dp)
                    )
                    {
                        Icon(
                            tint = Color.Green,
                            modifier = Modifier
                                .size(160.dp),
                            painter = painterResource(id = R.drawable.ic_add_box_24),
                            contentDescription = "정보 추가하기 아이콘"
                        )
                    }
                }
            }
        }
    }
}