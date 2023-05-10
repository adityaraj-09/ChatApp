package com.example.insien.Services;

import static com.example.insien.Activity.ChatActivity.senderRoom;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.content.res.ResourcesCompat;

import com.example.insien.Activity.CallActivity;
import com.example.insien.Activity.ChatActivity;
import com.example.insien.Activity.HomeActivity;
import com.example.insien.Activity.SettingActivity;
import com.example.insien.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

public class FirebaseNotificationService extends FirebaseMessagingService {
      @Override
      public  void onMessageReceived(@NonNull RemoteMessage remoteMessage){

            super.onMessageReceived(remoteMessage);
            sendNotification(Objects.requireNonNull(remoteMessage.getNotification()).getTitle(),remoteMessage.getNotification().getBody());
      }
       void sendNotification(String  title,String messageBody) {
            String channelId = "1";
            NotificationCompat.Builder notificationBuilder =
                    new NotificationCompat.Builder(this, channelId);


            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            NotificationCompat.BigTextStyle bigText = new NotificationCompat.BigTextStyle();
            bigText.bigText(title);
            bigText.setBigContentTitle(messageBody);

            notificationBuilder.setSmallIcon(R.drawable.chat)
                            .setContentTitle(title)
                            .setContentText(messageBody)
                            .setAutoCancel(true)
                            .setSound(defaultSoundUri)
                    .setPriority(Notification.PRIORITY_MAX)
                    .setStyle(bigText);


            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                  NotificationChannel channel = new NotificationChannel(channelId,
                          "Channel human readable title",
                          NotificationManager.IMPORTANCE_DEFAULT);
                  notificationManager.createNotificationChannel(channel);
            }
            notificationManager.notify(0 , notificationBuilder.build());
      }
}
