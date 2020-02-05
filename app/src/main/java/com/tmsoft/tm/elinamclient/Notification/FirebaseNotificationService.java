package com.tmsoft.tm.elinamclient.Notification;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.tmsoft.tm.elinamclient.Activity.PublicNotificationActivity;
import com.tmsoft.tm.elinamclient.R;

public class FirebaseNotificationService extends FirebaseMessagingService {
    public FirebaseNotificationService() {
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        String notificationTitle = remoteMessage.getNotification().getTitle();
        String notificationBody = remoteMessage.getNotification().getBody();

        //setting the notification sound
        Uri defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.notification_icon)
                .setContentTitle(notificationTitle)
                .setContentText(notificationBody)
                .setDefaults(Notification.DEFAULT_SOUND|Notification.DEFAULT_LIGHTS|Notification.DEFAULT_VIBRATE)
                .setAutoCancel(true);

        /*//Sending the user to the activity they click
        Intent intent = new Intent(this, PublicNotificationActivity.class);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
        );*/

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, PublicNotificationActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);

        mBuilder.setContentIntent(contentIntent);


        //Creating a random unique notification id & Setting Notification ID
        int mNotificationId = (int) System.currentTimeMillis();
        //Get an instance of the notification service
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(mNotificationId, mBuilder.build());
    }
}
