package com.example.babbogi.ui.view

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.babbogi.R
import java.time.LocalDate

private val dayWidth = 32.dp

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Calendar(onSubmit: (date: LocalDate) -> Unit) {
    var today by remember { mutableStateOf(LocalDate.now()) }

    ElevatedCardWithDefault {
        var day = -(today.minusDays((today.dayOfMonth).toLong()).dayOfWeek.value % 7)
        ColumnWithDefault {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                IconButton(onClick = { today = today.minusMonths(1) }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_chevron_left_24),
                        contentDescription = "이전 달"
                    )
                }
                Text(
                    text = today.year.toString() + "년 " + today.month.value.toString() + "월",
                    fontSize = 24.sp,
                )
                IconButton(onClick = { today = today.plusMonths(1) }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_chevron_right_24),
                        contentDescription = "다음달"
                    )
                }
            }
            ElevatedCardWithDefault {
                ColumnWithDefault {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        repeat(7) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center,
                                modifier = Modifier
                                    .size(dayWidth)
                                    .background(
                                        color = when (it) {
                                            0 -> Color.Red
                                            6 -> Color.Blue
                                            else -> Color.Black
                                        }.copy(0.1f),
                                        shape = RoundedCornerShape(10.dp),
                                    )
                            ) { Text(text = "일월화수목금토"[it].toString()) }
                        }
                    }
                    repeat(6) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            repeat(7) {
                                val now = day
                                CalendarDay(day = if (0 < day && day <= today.lengthOfMonth()) day else null,
                                    isSelected = day == today.dayOfMonth,
                                    onClick = { today = today.withDayOfMonth(now) })
                                day++
                            }
                        }
                    }
                }
            }
            Row(
                horizontalArrangement = Arrangement.End,
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(onClick = { onSubmit(today) }) {
                    Text("Submit")
                }
            }
        }
    }
}

@Composable
fun CalendarDay(day: Int? = null, isSelected: Boolean = false, onClick: () -> Unit = {}) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.size(dayWidth)
    ) {
        if (day != null) {
            Button(
                onClick = onClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isSelected) Color.Black.copy(alpha = 0.1f)
                    else Color.Transparent,
                ),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(all = 0.dp)
            ) {
                Text(text = day.toString(), color = Color.Black)
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview
@Composable
fun PreviewCalendar() {
    Calendar(onSubmit = {})
}