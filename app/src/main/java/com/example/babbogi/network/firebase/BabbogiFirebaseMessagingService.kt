package com.example.babbogi.network.firebase

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.example.babbogi.MainActivity
import com.example.babbogi.R
import com.example.babbogi.model.BabbogiModel
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import java.util.concurrent.atomic.AtomicInteger

class BabbogiFirebaseMessagingService: FirebaseMessagingService() {
    private val channelId = "Channel ID"

    object NotificationID {
        private val c = AtomicInteger(0)
        val id: Int get() = c.incrementAndGet()
    }

    override fun onNewToken(token: String) {
        Log.d("onNewToken", "Refreshed token: $token")
        BabbogiModel.token = token
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onMessageReceived(message: RemoteMessage) {
        Log.d("onMessageReceived", "Received message")
        val notification = message.notification ?: return
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE)
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setContentTitle(notification.title)
            .setContentText(notification.body)
            .setContentIntent(pendingIntent)
            .setSmallIcon(R.mipmap.ic_launcher_round)
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channel = NotificationChannel(channelId, "Channel", NotificationManager.IMPORTANCE_DEFAULT)
        notificationManager.createNotificationChannel(channel)

        // Unique ID for each notification
        val notificationId = NotificationID.id
        notificationManager.notify(notificationId, notificationBuilder.build())
    }
}