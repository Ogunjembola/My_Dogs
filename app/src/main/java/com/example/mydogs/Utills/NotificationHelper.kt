package com.example.mydogs.Utills

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.mydogs.R
import com.example.mydogs.view.MainActivity
import kotlin.coroutines.coroutineContext

class NotificationHelper(val context: Context) {
    private val CHANNEL_ID = " Dogs Channel Id"
    private val NOTIFICATION_ID = 123
fun createNotification(){
    createNotificationChannel()

    val intent = Intent(context, MainActivity:: class.java).apply {
        flags= Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    }
    val pendingIntent = PendingIntent.getActivity(context,0,intent,0)
    val icon = BitmapFactory.decodeResource(context.resources, R.drawable.dog)
    val

            notification = NotificationCompat.Builder(context, CHANNEL_ID)
        .setSmallIcon(R.drawable.dog_icon)
        .setLargeIcon(icon)
        .setContentTitle( "Dogs retrieved")
        .setContentText(" This notification has some content")
        .setStyle(
            NotificationCompat.BigPictureStyle()
                .bigPicture(icon)
                .bigLargeIcon(null)
        )
        .setContentIntent(pendingIntent)
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .build()
    NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, notification)
}

        // creating notification Channel
    private fun createNotificationChannel(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val name = CHANNEL_ID
            val descriptionText = "Channel description"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID,name,importance).apply {
                description= descriptionText
            }
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}