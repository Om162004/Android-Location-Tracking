package com.omkaran.location.services;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.location.LocationManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.omkaran.location.R;
import com.omkaran.location.database.AppDatabase;
import com.omkaran.location.model.LocationEntity;
import com.omkaran.location.utils.LocationHelper;
import com.omkaran.location.utils.ScheduleHelper;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LocationService extends Service
{
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private AppDatabase database;
    private ExecutorService executorService;
    private static final String CHANNEL_ID = "location_channel";
    private static final long UPDATE_INTERVAL = 1* 60 * 1000;

    @Override
    public void onCreate() {

        super.onCreate();
        fusedLocationClient =LocationServices.getFusedLocationProviderClient(this);
        database = AppDatabase.getInstance(this);
        executorService =Executors.newSingleThreadExecutor();
        createNotificationChannel();
        startForeground(1,createNotification());
        if (isLocationEnabled())
        {
            startLocationUpdates();
        }
        else
        {
            showLocationOffNotification();
            Log.d("LOCATION_SERVICE","Location/GPS is OFF");
            stopSelf();
        }
        Log.d("LOCATION_SERVICE","Service Started");
    }
    private boolean isLocationEnabled()
    {
        LocationManager locationManager =(LocationManager) getSystemService(LOCATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P)
        {
            return locationManager.isLocationEnabled();
        }
        else
        {
            return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                    locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        }
    }
    private void showLocationOffNotification() {

        Notification notification =new NotificationCompat.Builder(this,CHANNEL_ID)
                        .setContentTitle("Location Service")
                        .setContentText("Location/GPS is OFF")
                        .setStyle(new NotificationCompat.BigTextStyle().bigText("Location/GPS is OFF.\n" + "Please turn ON Location."))
                        .setSmallIcon(R.drawable.ic_launcher_foreground)
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setOngoing(false)
                        .setAutoCancel(false)
                        .build();

        NotificationManager manager =getSystemService(NotificationManager.class);
        int notificationId =(int) System.currentTimeMillis();
        manager.notify(notificationId,notification);
    }
    private void startLocationUpdates()
    {
        LocationRequest locationRequest =new LocationRequest.Builder
                (Priority.PRIORITY_HIGH_ACCURACY,UPDATE_INTERVAL)
                        .setMinUpdateIntervalMillis(1*60 * 1000)
                        .build();

        locationCallback = new LocationCallback()
        {
            @Override
            public void onLocationResult(LocationResult locationResult)
            {
                if (locationResult == null)
                {
                    Log.d("LOCATION_SERVICE", "Location Result is NULL");
                    return;
                }
                for (Location location : locationResult.getLocations())
                {
                    Log.d("LOCATION_SERVICE","Location Received: "+ location.getLatitude() + ", "+ location.getLongitude());
                    saveLocation(location);
                }
            }
        };
        if (ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED&&ActivityCompat.checkSelfPermission
                (this,Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            return;
        }
        fusedLocationClient.requestLocationUpdates(locationRequest,locationCallback,getMainLooper());
    }
    private void showStatusNotification(String lastService,String nextService)
    {
        NotificationManager manager =getSystemService(NotificationManager.class);

        int lastNotificationId =(int) System.currentTimeMillis();

        int nextNotificationId =lastNotificationId + 1;
        // Last Service Notification
        Notification lastNotification =new NotificationCompat.Builder(this,CHANNEL_ID)
                        .setContentTitle("Last Service Executed On")
                        .setContentText(lastService)
                        .setSmallIcon(R.drawable.ic_launcher_foreground)
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setOngoing(false)
                        .setAutoCancel(false)
                        .build();
        // Next Service Notification
        Notification nextNotification =new NotificationCompat.Builder(this,CHANNEL_ID)
                        .setContentTitle("Next Service Scheduled On")
                        .setContentText(nextService)
                        .setSmallIcon(R.drawable.ic_launcher_foreground)
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setOngoing(false)
                        .setAutoCancel(false)
                        .build();

        // Dono alag notifications
        manager.notify(lastNotificationId,lastNotification);
        manager.notify(nextNotificationId,nextNotification);
    }
    private void saveLocation(Location location)
    {
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        float accuracy = location.getAccuracy();

        String address =LocationHelper.getAddress(this, location);

        String dateTime =new SimpleDateFormat("dd-MM-yyyy hh:mm:ss a",Locale.getDefault()).format(new Date());

        LocationEntity locationEntity =new LocationEntity(latitude,longitude,accuracy,address,dateTime);

        executorService.execute(() -> {
            // Location save
            database.locationDao().insertLocation(locationEntity);

            Log.d("LOCATION_SERVICE","Location Saved: "+ latitude+ ", "+ longitude);

            String lastTime =new SimpleDateFormat("hh:mm:ss a",Locale.getDefault()).format(new Date());

            getSharedPreferences("service_schedule",MODE_PRIVATE)
                    .edit()
                    .putString("last_service", lastTime)
                    .apply();

            //showLastServiceNotification(lastTime);
            // Next service schedule
            ScheduleHelper.scheduleNextService(this);

            // Get next service time
            String nextTime =getSharedPreferences("service_schedule",MODE_PRIVATE).getString("next_service","--");

            // Permanent notification
            showStatusNotification(lastTime,nextTime);
            // Stop foreground service
            stopSelf();
        });
    }
    private void createNotificationChannel()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            NotificationChannel channel =new NotificationChannel(CHANNEL_ID,"Location Tracking",NotificationManager.IMPORTANCE_HIGH);

            NotificationManager manager =getSystemService(NotificationManager.class);

            manager.createNotificationChannel(channel);
        }
    }
    private Notification createNotification()
    {
        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Location Service")
                .setContentText("Location service is running...")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setOngoing(false)
                .build();
    }
    @Override
    public int onStartCommand(Intent intent,int flags,int startId)
    {
        return START_NOT_STICKY;
    }
    @Override
    public void onDestroy()
    {
        super.onDestroy();
        if (fusedLocationClient != null&& locationCallback != null)
        {
            fusedLocationClient.removeLocationUpdates(locationCallback);
        }
        if (executorService != null)
        {
            executorService.shutdown();
        }
        Log.d("LOCATION_SERVICE","Service Stopped");
    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }
}