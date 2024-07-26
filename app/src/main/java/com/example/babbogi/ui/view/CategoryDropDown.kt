//package com.example.babbogi.ui.view
//
//import androidx.compose.foundation.background
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.material3.DropdownMenuItem
//import androidx.compose.material3.ExperimentalMaterial3Api
//import androidx.compose.material3.ExposedDropdownMenuBox
//import androidx.compose.material3.ExposedDropdownMenuDefaults
//import androidx.compose.material3.MenuDefaults
//import androidx.compose.material3.Text
//import androidx.compose.material3.TextField
//import androidx.compose.material3.TextFieldDefaults
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.LaunchedEffect
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.runtime.setValue
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.tooling.preview.Preview
//
//@Composable
//fun CustomDropDown(
//    options: List<String>,
//    selectedOption: String?,
//    onOptionSelected: (String) -> Unit
//) {
//    var expanded by remember { mutableStateOf(false) }
//
//    Box(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
//        Text(
//            text = selectedOption ?: "전체",
//            modifier = Modifier
//                .clickable { expanded = true }
//                .padding(16.dp)
//                .fillMaxWidth()
//                .background(MaterialTheme.colorScheme.surface)
//                .padding(8.dp),
//            style = MaterialTheme.typography.body1
//        )
//        DropdownMenu(
//            expanded = expanded,
//            onDismissRequest = { expanded = false },
//            modifier = Modifier.fillMaxWidth()
//        ) {
//            DropdownMenuItem(onClick = {
//                onOptionSelected("전체")
//                expanded = false
//            }) {
//                Text("전체")
//            }
//            options.forEach { option ->
//                DropdownMenuItem(onClick = {
//                    onOptionSelected(option)
//                    expanded = false
//                }) {
//                    Text(option)
//                }
//            }
//        }
//    }
//}




