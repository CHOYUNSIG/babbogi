package com.example.babbogi.network.firebase

import android.content.ContentValues.TAG
import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class BabbogiFirebaseMessagingService: FirebaseMessagingService() {
    override fun onNewToken(token: String) {
        Log.d(TAG, "Refreshed token: $token")

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // FCM registration token to your app server.
        // ex) sendRegistrationToServer(token)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        Log.d(TAG, "Received message: ${message.notification?.body}")
    }
}