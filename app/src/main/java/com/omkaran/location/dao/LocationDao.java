package com.omkaran.location.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import com.omkaran.location.model.LocationEntity;
import java.util.List;

@Dao
public interface LocationDao
{
    @Insert
    void insertLocation(LocationEntity location);

    @Query("SELECT * FROM location_table ORDER BY id DESC")
    LiveData<List<LocationEntity>> getAllLocations();
}