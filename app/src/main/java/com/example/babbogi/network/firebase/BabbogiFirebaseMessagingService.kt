package com.example.babbogi.network.firebase

import android.util.Log
import com.example.babbogi.util.DataPreference
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class BabbogiFirebaseMessagingService: FirebaseMessagingService() {
    override fun onNewToken(token: String) {
        Log.d("onNewToken", "Refreshed token: $token")
        DataPreference.saveToken(token)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        Log.d("onMessageReceived", "Received message: ${message.notification?.body}")
    }
}