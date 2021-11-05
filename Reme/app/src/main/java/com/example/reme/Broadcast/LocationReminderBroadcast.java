package com.example.reme.Broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.example.reme.Helper.NotificationHelper;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.util.List;

import static android.content.ContentValues.TAG;

public class LocationReminderBroadcast extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        NotificationHelper notificationHelper = new NotificationHelper(context);
        String title = intent.getStringExtra("Title");

        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);

        if(geofencingEvent.hasError()){
            Log.d(TAG, "onReceive: Error receiving geofence event!");
            return;
        }

        List<Geofence> geofenceList = geofencingEvent.getTriggeringGeofences();
        for (Geofence geofence: geofenceList){
            Log.d(TAG, "onReceive: " + geofence.getRequestId());
        }
        int transitionType = geofencingEvent.getGeofenceTransition();

        switch (transitionType){
            case Geofence.GEOFENCE_TRANSITION_ENTER:
                NotificationCompat.Builder builderEnter = notificationHelper.getLocationChannelNotification(title, "Location Reach!");
                notificationHelper.getManager().notify(1, builderEnter.build());
                break;
            case Geofence.GEOFENCE_TRANSITION_EXIT:
                NotificationCompat.Builder builderExit = notificationHelper.getLocationChannelNotification(title, "Exit from the location");
                notificationHelper.getManager().notify(1, builderExit.build());
                break;
        }
    }
}