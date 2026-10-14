package com.omkaran.location.database;

import static com.omkaran.location.database.AppDatabase.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import com.omkaran.location.dao.LocationDao;
import com.omkaran.location.model.LocationEntity;

@Database(entities = {LocationEntity.class},version = 1,exportSchema = false)

public abstract class AppDatabase extends RoomDatabase
{
    public abstract LocationDao locationDao();
    public static AppDatabase database ;

    public static synchronized AppDatabase getInstance(Context context)
    {
        if (database == null)
        {
            database = Room.databaseBuilder(
                    context.getApplicationContext(),
                    AppDatabase.class,
                    "location_database"
            ).build();
        }
        return database;
    }
}
