package com.example.fridgea

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import com.example.fridgea.ui.CameraScreen
import com.example.fridgea.ui.MainScreen
import com.example.fridgea.ui.model.CameraViewModel
import com.example.fridgea.ui.model.CameraViewModelFactory
import com.example.fridgea.ui.theme.FridgeaTheme


class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val cameraViewModel = ViewModelProvider(
            this,
            CameraViewModelFactory()
        )[CameraViewModel::class.java]

        setContent {
            FridgeaTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen()
                }
            }
        }
    }
}

