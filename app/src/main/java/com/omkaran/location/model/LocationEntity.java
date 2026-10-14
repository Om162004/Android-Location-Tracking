package com.omkaran.location.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "location_table")
public class LocationEntity
{
    @PrimaryKey(autoGenerate = true)
    private int id;

    private double latitude;
    private double longitude;
    private float accuracy;
    private String address;
    private String dateTime;

    public LocationEntity(double latitude,double longitude,float accuracy,String address,String dateTime)
    {
        this.latitude = latitude;
        this.longitude = longitude;
        this.accuracy = accuracy;
        this.address = address;
        this.dateTime = dateTime;
    }

    // PrimaryKey annotation यहाँ नहीं लगाना है
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public double getLatitude() {
        return latitude;
    }
    public double getLongitude() {
        return longitude;
    }
    public float getAccuracy() {
        return accuracy;
    }
    public String getAddress() {
        return address;
    }
    public String getDateTime() {
        return dateTime;
    }
}