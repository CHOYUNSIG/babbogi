package com.example.babbogi.ui.view

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.babbogi.R
import java.time.LocalDate

private val dayWidth = 32.dp

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Calendar(
    initDate: LocalDate = LocalDate.now(),
    allowFuture: Boolean = false,
    onSubmit: (date: LocalDate) -> Unit
) {
    var today by remember { mutableStateOf(initDate) }

    ElevatedCardWithDefault {
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
                    var day = today.withDayOfMonth(1).let {
                        it.minusDays(it.dayOfWeek.value.toLong())
                    }
                    repeat(6) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            repeat(7) {
                                val now = day.plusDays(0)
                                CalendarDay(
                                    day = if (day.month == today.month) day.dayOfMonth else null,
                                    isSelected = day == today,
                                    enable = allowFuture || day <= LocalDate.now(),
                                    onClick = { today = now }
                                )
                                day = day.plusDays(1)
                            }
                        }
                    }
                }
            }
            Row(
                horizontalArrangement = Arrangement.End,
                modifier = Modifier.fillMaxWidth()
            ) {
                FixedColorButton(
                    onClick = { onSubmit(today) },
                    text = "확인",
                )
            }
        }
    }
}

@Composable
private fun CalendarDay(
    day: Int? = null,
    isSelected: Boolean = false,
    enable: Boolean = true,
    onClick: () -> Unit = {}
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.size(dayWidth)
    ) {
        if (day != null) {
            Button(
                onClick = onClick,
                enabled = enable,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isSelected) Color.Black.copy(alpha = 0.1f)
                    else Color.Transparent,
                ),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(all = 0.dp)
            ) {
                Text(text = day.toString(), color = Color.Unspecified)
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DateSelector(
    initDate: LocalDate = LocalDate.now(),
    allowFuture: Boolean = false,
    onDateChanged: (LocalDate) -> Unit
) {
    var today by remember { mutableStateOf(initDate) }
    var showCalendar by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = {
                today = today.minusDays(1)
                onDateChanged(today)
            }
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_chevron_left_24),
                contentDescription = "하루 전",
            )
        }
        ElevatedCardWithDefault(
            onClick = { showCalendar = true },
            modifier = Modifier.weight(1f)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Text(
                    text = today.toString(),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Normal,
                    textAlign = TextAlign.Center
                )
            }
        }
        IconButton(
            onClick = {
                if (allowFuture || today.toEpochDay() < LocalDate.now().toEpochDay()) {
                    today = today.plusDays(1)
                    onDateChanged(today)
                }
            },
            enabled = allowFuture || today.toEpochDay() < LocalDate.now().toEpochDay()
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_chevron_right_24),
                contentDescription = "하루 후"
            )
        }
    }

    if (showCalendar) Dialog(onDismissRequest = { showCalendar = false }) {
        Calendar(initDate = today, allowFuture = allowFuture) {
            showCalendar = false
            today = it
            onDateChanged(it)
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview
@Composable
fun PreviewCalendar() {
    Calendar(onSubmit = {})
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview
@Composable
fun PreviewDateSelector() {
    DateSelector(initDate = LocalDate.now(), onDateChanged = {})
}