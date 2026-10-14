package com.omkaran.location;

import static androidx.core.content.ContextCompat.startActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.omkaran.location.adapter.LocationAdapter;
import com.omkaran.location.database.AppDatabase;
import com.omkaran.location.databinding.ActivityMainBinding;
import com.omkaran.location.model.LocationEntity;
import com.omkaran.location.utils.ScheduleHelper;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
{

    private ActivityMainBinding binding;
    private AppDatabase database;
    private LocationAdapter locationAdapter;

    private final List<LocationEntity> locationList =
            new ArrayList<>();

    // Location Permission
    private final ActivityResultLauncher<String[]> permissionLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.RequestMultiplePermissions(),
                    result -> {
                        Boolean fineLocation =
                                result.get(
                                        Manifest.permission.ACCESS_FINE_LOCATION
                                );
                        Boolean coarseLocation =
                                result.get(
                                        Manifest.permission.ACCESS_COARSE_LOCATION
                                );
                        if (Boolean.TRUE.equals(fineLocation)|| Boolean.TRUE.equals(coarseLocation))
                        {
                            requestNotificationPermission();
                            ScheduleHelper.scheduleImmediateService(this);
                        }
                    }
            );

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        // ViewBinding
        binding =ActivityMainBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());
        // Database
        database =AppDatabase.getInstance(this);
        // RecyclerView
        setupRecyclerView();
        // Observe Room data
        observeLocations();
        // Check Location Permission
        checkLocationPermission();
        // Map Button
        binding.btnMap.setOnClickListener(v -> {

            Intent intent =new Intent(MainActivity.this,MapActivity.class);
            startActivity(intent);
        });
    }
    private void requestNotificationPermission()
    {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
        {
            if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED)
            {
                requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS},101);
            }
        }
    }
    private void setupRecyclerView()
    {
            locationAdapter =new LocationAdapter(locationList);

            binding.recyclerViewLocations.setLayoutManager(
                    new LinearLayoutManager(this));

            binding.recyclerViewLocations.setAdapter(locationAdapter);
    }
    private void observeLocations()
    {
        database.locationDao()
                .getAllLocations()
                .observe(this, locations -> {

                    locationList.clear();
                    locationList.addAll(locations);
                    locationAdapter.updateList(locations);

                    // Latest Location
                    if (!locations.isEmpty())
                    {
                        LocationEntity latest =locations.get(locations.size() - 1);
                        showCurrentLocation(latest);
                    }
                });
    }
    // Show Current Location
    private void showCurrentLocation(LocationEntity location)
    {
        binding.tvCurrentLatitude.setText("Latitude: "+ location.getLatitude());
        binding.tvCurrentLongitude.setText("Longitude: "+ location.getLongitude());
        binding.tvCurrentAccuracy.setText("Accuracy: "+ location.getAccuracy()+ " m");
        binding.tvCurrentAddress.setText("Address: "+ location.getAddress());
    }
    // Check Location Permission
    private void checkLocationPermission()
     {
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        {
            requestNotificationPermission();
            ScheduleHelper.scheduleImmediateService(this);
        }
        else
        {
            permissionLauncher.launch(new String[]
                            {
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                    }
            );
        }
    }
    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        binding = null;
    }
}