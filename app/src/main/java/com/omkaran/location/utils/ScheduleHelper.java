package com.omkaran.location.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import com.omkaran.location.receiver.ScheduleReceiver;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ScheduleHelper
{
    public static void scheduleNextService(Context context)
    {
        AlarmManager alarmManager =(AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent intent =new Intent(context,ScheduleReceiver.class);

        PendingIntent pendingIntent =PendingIntent.getBroadcast(context,100,intent,PendingIntent.FLAG_UPDATE_CURRENT| PendingIntent.FLAG_IMMUTABLE);
        //Next service after 1 minute
        long triggerTime =System.currentTimeMillis()+ 60 * 1000;
        // Next service time
        String nextTime =new SimpleDateFormat("hh:mm:ss a",Locale.getDefault()).format(new Date(triggerTime));

        // Save next service time
        context.getSharedPreferences("service_schedule",Context.MODE_PRIVATE)
                .edit()
                .putString("next_service",nextTime)
                .apply();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
        {
            if (alarmManager.canScheduleExactAlarms())
            {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,triggerTime,pendingIntent);

                Log.d("SCHEDULE","NEXT Service Scheduled: "+ nextTime);
            }
            else
            {
                Log.d("SCHEDULE","Exact Alarm Permission Not Granted");
            }
        }
        else
        {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,triggerTime,pendingIntent);
            Log.d("SCHEDULE","NEXT Service Scheduled: "+ nextTime);
        }
    }
    public static void scheduleImmediateService(Context context) {

        AlarmManager alarmManager =(AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent intent =new Intent(context, ScheduleReceiver.class);

        PendingIntent pendingIntent =PendingIntent.getBroadcast(context,100,intent,PendingIntent.FLAG_UPDATE_CURRENT| PendingIntent.FLAG_IMMUTABLE);

        long triggerTime =System.currentTimeMillis();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
        {
            if (alarmManager.canScheduleExactAlarms())
            {
                alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        triggerTime,
                        pendingIntent
                );

                Log.d("SCHEDULE","Immediate Service Scheduled");
            }
        }
        else
        {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,triggerTime,pendingIntent);

            Log.d("SCHEDULE","Immediate Service Scheduled");
        }
    }
}