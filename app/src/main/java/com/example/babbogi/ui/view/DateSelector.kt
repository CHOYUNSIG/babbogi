package com.example.babbogi.ui.view

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.babbogi.R
import java.time.LocalDate

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DateSelector(
    today: LocalDate,
    onDateBarClicked: () -> Unit,
    onDateChanged: (LocalDate) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = { onDateChanged(today.minusDays(1)) }) {
            Icon(
                painter = painterResource(id = R.drawable.ic_chevron_left_24),
                contentDescription = "하루 전",
            )
        }
        ElevatedCardWithDefault(
            onClick = onDateBarClicked,
            modifier = Modifier.width(250.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
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
        IconButton(onClick = { onDateChanged(today.plusDays(1)) }) {
            Icon(
                painter = painterResource(id = R.drawable.ic_chevron_right_24),
                contentDescription = "하루 후"
            )
        }
    }
}