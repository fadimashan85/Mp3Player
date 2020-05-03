package se.mobileinteraction.mp3player

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ContentValues.TAG
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import se.mobileinteraction.mp3player.R
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage


class MyFirebaseMessagingService : FirebaseMessagingService() {

     override fun onMessageReceived(remoteMessage: RemoteMessage) {
        // TODO(developer): Handle FCM messages here.
        // Check if message contains a data payload.
        if (remoteMessage.data.size > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.data)

            if ( /* Check if data needs to be processed by long running job */true) {
                // For long-running tasks (10 seconds or more) use Firebase Job Dispatcher.
//                scheduleJob()
            } else {
                // Handle message within 10 seconds
//                handleNow()
            }
        }

        // Check if message contains a notification payload.
        if (remoteMessage.notification != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.data)
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }


    override fun onNewToken(token: String) {
        Log.d("TOKEN", FirebaseInstanceId.getInstance().token)
        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
    }



    private fun sendNotification(messageBody: RemoteMessage) {

//
        val title = messageBody.data["title"]
        val content = messageBody.data["content"]

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//        notificationManager.notify(1,notificationBuilder.build())

        val NOTIFICATION_CHANNEL_ID = "HelloKotlin"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                "Hello Notification",
                NotificationManager.IMPORTANCE_DEFAULT
            )

            notificationChannel.apply {
                description = "channel Notification"
                enableLights(true)
                lightColor = (Color.GREEN)
                vibrationPattern = longArrayOf(0, 1000, 500, 500)
                enableVibration(true)

                notificationManager.createNotificationChannel(notificationChannel)
            }

            val notificationBuilder = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.item_count)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setContentTitle(title)
                .setContentText(content)

            notificationManager.notify(1,notificationBuilder.build())

        }
    }


}