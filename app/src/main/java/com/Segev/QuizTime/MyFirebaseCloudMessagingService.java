package com.Segev.QuizTime;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

// send notifications from the firebase cloud messaging

public class MyFirebaseCloudMessagingService extends FirebaseMessagingService {


    public static int NOTIFICATION_ID=1;

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        generateNotification(remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody());
    }

    private void generateNotification(String title, String body) {
        // set the ringtone for the notification
        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        // set the form of the notification
        NotificationCompat.Builder builder1 = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_message)
                .setContentTitle(title)
                .setContentText(body)
                .setSound(soundUri)
                .setAutoCancel(true);
        // send a notification
        Intent intent = new Intent(this,SplashActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        builder1.setContentIntent(pendingIntent);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            builder1.setChannelId("com.Segev.QuizTime");
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel(
                    "com.Segev.QuizTime",
                    "NewQuiz",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            if(notificationManager != null){
                notificationManager.createNotificationChannel(channel);
            }
        }
        // if you get to the limit, you restart the counting
        if(NOTIFICATION_ID>107550145) {
            NOTIFICATION_ID=0;
        }

        notificationManager.notify(NOTIFICATION_ID++,builder1.build());
    }
}