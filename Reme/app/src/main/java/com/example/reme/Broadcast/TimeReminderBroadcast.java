package com.example.reme.Broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.reme.Helper.NotificationHelper;
import com.example.reme.R;

public class TimeReminderBroadcast extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String title = intent.getStringExtra("Title");
        String subtitle = intent.getStringExtra("Subtitle");
        String id = intent.getStringExtra("ID");

        NotificationHelper notificationHelper = new NotificationHelper(context);
        NotificationCompat.Builder builder = notificationHelper.getTimeChannelNotification(title, subtitle, id);
        notificationHelper.getManager().notify(1, builder.build());
    }
}
