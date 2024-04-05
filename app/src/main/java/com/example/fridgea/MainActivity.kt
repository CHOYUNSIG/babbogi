package com.example.fridgea

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import com.example.fridgea.ui.CameraViewModel
import com.example.fridgea.ui.CameraViewModelFactory
import com.example.fridgea.ui.theme.FridgeaTheme


class MainActivity : ComponentActivity() {
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
                    CameraScreen(cameraViewModel)
                }
            }
        }
    }
}

