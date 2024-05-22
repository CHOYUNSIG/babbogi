package com.example.babbogi.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import java.time.LocalDate
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextField
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController

@Composable
fun HealthProfileScreen(
    navController: NavHostController = rememberNavController()
) {
    var UserHight: Float = 0.0F
    var UserWeght: Float = 0.0F
    var UserAge : LocalDate
    var UserSex: String = ""
    var UserAdultDisease: Boolean = false

    Column (modifier = Modifier.background(color = Color.White))
    {
        HealthScreenName()

        EnterUserHeightScreen(onSubmit = {})

        EnterUserWeightScreen(onSubmit = {})

        EnterUserSexScreen(onSubmit = {})

        EnterUserAdultDiseaseScreen()
    }

}

@Composable
fun HealthScreenName(){
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .background(color = Color.White)
    )
    {
        Text(
            "건강정보",
            color = Color.Black,
            fontSize = 26.sp,
            fontWeight = FontWeight.W600
        )
    }
}





@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnterUserHeightScreen(onSubmit: (Float) -> Unit) {
    val height = remember { mutableStateOf("") }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = Color.White)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically  // Align items vertically in the center
        ) {
            Box(
                modifier = Modifier
                    .weight(30.0F)
            ) {
                Text(
                    text = "키",
                    fontSize = 20.sp,
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    )
            }
            Box(
                modifier = Modifier
                .weight(100.0F)
            ){
                OutlinedTextField(
                    value = "",
                    onValueChange = {/*값 업데이트*/ },
                    label = { Text("본인의 키를 입력하시오") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier
                        .fillMaxWidth(),
                    enabled = true,
                    textStyle = TextStyle(
                        fontSize = 20.sp,
                    )
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnterUserWeightScreen(onSubmit: (Float) -> Unit) {
    val weight = remember { mutableStateOf("") }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = Color.White)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically  // Align items vertically in the center
        ) {
            Box(
                modifier = Modifier
                    .weight(30.0F)
            ) {
                Text(
                    text = "몸무게",
                    fontSize = 20.sp,
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    )
            }
            Box(
                modifier = Modifier
                    .weight(100.0F)
            ){
                OutlinedTextField(
                    value = "",
                    onValueChange = {/*값 업데이트*/ },
                    label = { Text("본인의 몸무게를 입력하시오") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier
                        .fillMaxWidth(),
                    enabled = true,
                    textStyle = TextStyle(
                        fontSize = 20.sp,
                    )
                )
            }
        }
    }
}


@Composable
fun EnterUserSexScreen(onSubmit: (Float) -> Unit) {
    var selectedGender by remember { mutableStateOf("None") }
    val options = listOf("남성", "여성")
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = Color.White)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .weight(30.0F)
            ) {
                Text(
                    text = "성별",
                    fontSize = 20.sp,
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                )
            }
            Box(
                modifier = Modifier
                    .weight(100.0F)
            ) {

                Row {
                    options.forEach { gender ->
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .padding(4.dp)
                                .clickable { selectedGender = gender }
                                .weight(100.0F)
                                .height(45.dp)
                        ) {
                            Canvas(modifier = Modifier.matchParentSize()) {
                                drawRoundRect(
                                    color = if (selectedGender == gender) Color(0xFF21A642) else Color(0x20000000),
                                    topLeft = Offset.Zero,
                                    size = Size(size.width, size.height),
                                    cornerRadius = CornerRadius(0f, 0f),
                                    style = Stroke(width = 5f),
                                )
                            }
                            Text(
                                text = gender,
                                fontSize = 20.sp,
                                color = Color.Black,
                                fontWeight = FontWeight.Normal)
                        }
                    }

                }
            }
        }
    }
}

@Composable
fun EnterUserAdultDiseaseScreen(){
    var expanded by remember { mutableStateOf(false) }
    var selectedDisease by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = Color.White)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically  // Align items vertically in the center
        ) {
            Box(
                modifier = Modifier
                    .weight(30.0F)
            ) {
                Text(
                    text = "성인병",
                    fontSize = 20.sp,
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                )
            }
            DropDown()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropDown(){
    val list = listOf("해당 없음", "당뇨", "고혈압")

    var selectedText by remember {
        mutableStateOf(list[0])
    }

    var isExpended by remember{
        mutableStateOf(false)
    }


    Column(
        modifier = Modifier
            .padding(start = 10.dp)
    ) {
        ExposedDropdownMenuBox(
            expanded = isExpended,
            onExpandedChange ={isExpended = !isExpended}
        ) {
            TextField(
                modifier = Modifier.menuAnchor(),
                value = selectedText,
                onValueChange = {},
                readOnly = true,
                trailingIcon = {ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpended)}
            )

        ExposedDropdownMenu(
            expanded = isExpended,
            onDismissRequest = {isExpended = false},
        ){
            list.forEachIndexed{ index, text ->
                DropdownMenuItem(
                    text = { Text(text = text) },
                    onClick = {
                        selectedText = list[index]
                        isExpended = false
                    },
                contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                )
            }
        }
        }
    }
}
@Preview
@Composable
fun PreviewHealthProfileScreen(){
    HealthProfileScreen()
}
