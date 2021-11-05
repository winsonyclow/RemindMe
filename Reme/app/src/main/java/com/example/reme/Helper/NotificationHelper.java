package com.example.reme.Helper;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.ContextWrapper;

import androidx.core.app.NotificationCompat;

import com.example.reme.R;

public class NotificationHelper extends ContextWrapper {
    public static final String timeChannelID = "timeChannelID";
    public static final String timeChannelName = "Time Reminder";
    public static final String locationChannelID = "locationChannelID";
    public static final String locationChannelName = "Location Reminder";

    private NotificationManager mManager;

    public NotificationHelper(Context base) {
        super(base);
        createChannel();
    }

    public void createChannel(){
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel timeChannel = new NotificationChannel(timeChannelID,timeChannelName, NotificationManager.IMPORTANCE_HIGH);
            timeChannel.enableLights(true);
            timeChannel.enableVibration(true);
            timeChannel.setLightColor(R.color.defaultBackground3);
            timeChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

            getManager().createNotificationChannel(timeChannel);

            NotificationChannel LocationChannel = new NotificationChannel(locationChannelID,locationChannelName, NotificationManager.IMPORTANCE_HIGH);
            LocationChannel.enableLights(true);
            LocationChannel.enableVibration(true);
            LocationChannel.setLightColor(R.color.defaultBackground3);
            LocationChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

            getManager().createNotificationChannel(LocationChannel);
        }


    }

    public NotificationManager getManager() {
        if(mManager == null){
            mManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }

        return mManager;
    }

    public NotificationCompat.Builder getTimeChannelNotification(String title, String message, String id){
        return new NotificationCompat.Builder(getApplicationContext(), id)
                .setContentTitle(title)
                .setContentText(message)
                .setSmallIcon(R.drawable.ic_notifications);
    }

    public NotificationCompat.Builder getLocationChannelNotification(String title, String message){
        return new NotificationCompat.Builder(getApplicationContext(), locationChannelID)
                .setContentTitle(title)
                .setContentText(message)
                .setSmallIcon(R.drawable.ic_location);
    }
}
