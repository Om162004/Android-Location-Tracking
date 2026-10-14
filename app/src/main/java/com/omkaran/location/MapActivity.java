package com.omkaran.location;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.omkaran.location.database.AppDatabase;
import com.omkaran.location.model.LocationEntity;

public class MapActivity extends AppCompatActivity
        implements OnMapReadyCallback {

    private GoogleMap googleMap;
    private AppDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_map);

        // Database
        database = AppDatabase.getInstance(this);

        // Google Map
        SupportMapFragment mapFragment =(SupportMapFragment)
                        getSupportFragmentManager()
                                .findFragmentById(R.id.map);

        if (mapFragment != null)
        {
            mapFragment.getMapAsync(this);
        }
    }
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap)
    {
        this.googleMap = googleMap;

        // RecyclerView se selected location
        double selectedLatitude =getIntent().getDoubleExtra("latitude",Double.NaN);
        double selectedLongitude =getIntent().getDoubleExtra("longitude",Double.NaN);

        if (!Double.isNaN(selectedLatitude)&&!Double.isNaN(selectedLongitude))
        {
            String Address = getIntent().getStringExtra("Address");
            LatLng selectedLocation =new LatLng(selectedLatitude,selectedLongitude);
            String str= "Latitude: "+ selectedLatitude + ", Longitude: "+ selectedLongitude+", Address: "+ Address;
            googleMap.addMarker(new MarkerOptions().position(selectedLocation).title("Selected Location").snippet(str));
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(selectedLocation,15));
        }
        else
        {
            database.locationDao()
                    .getAllLocations()
                    .observe(this, locations -> {

                        if (locations == null || locations.isEmpty()) {
                            return;
                        }
                        PolylineOptions polylineOptions = new PolylineOptions();
                        // All saved locations
                        for (LocationEntity location : locations) {
                            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                            // Marker
                            String address = location.getAddress();
                            String details = "Latitude: " + location.getLatitude() + ", longitute: " + location.getLongitude() + ", Address: " + address;
                            googleMap.addMarker(new MarkerOptions().position(latLng).title("Location").snippet(details));
                            // Polyline point
                            polylineOptions.add(latLng);
                        }
                        // Movement history line
                        googleMap.addPolyline(polylineOptions);
                        LocationEntity latest = locations.get(locations.size() - 1);
                        LatLng latestLocation = new LatLng(latest.getLatitude(), latest.getLongitude());
                        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latestLocation, 15));

                    });
        }
    }
}