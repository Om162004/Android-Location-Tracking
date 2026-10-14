package com.omkaran.location.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import androidx.core.content.ContextCompat;
import com.omkaran.location.services.LocationService;

public class ScheduleReceiver extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent)
    {
        Log.d("SCHEDULE","Alarm Triggered");
        Intent serviceIntent =new Intent(context, LocationService.class);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            ContextCompat.startForegroundService(context, serviceIntent);
        }
        else
        {
            context.startService(serviceIntent);
        }
    }
}
