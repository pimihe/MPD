// Name                 Pierce hepburn
// Student ID           S1711876
// Programme of Study   Computing

package org.s1711876.earthquakes.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

// singleton class used in room database implementation
@Database(entities = {Earthquake.class, XmlMd5.class}, version = 1)
public abstract class EarthquakeDatabase extends RoomDatabase {
    public static final String DB_NAME = "earthquake.sqlite";

    public static volatile EarthquakeDatabase classInstance;
    private static final Object CLASS_LOCK = new Object();

    // single function which returns the instance of th singleton
    public static EarthquakeDatabase getClassInstance(Context context) {
        if (classInstance == null) {
            synchronized (CLASS_LOCK) {
                if (classInstance == null) {
                    classInstance = Room.databaseBuilder(context.getApplicationContext(),EarthquakeDatabase.class, DB_NAME).build();
                }
            }
        }

        return classInstance;
    }

    public abstract EarthquakeDao earthquakeDao();
    public abstract XmlMd5Dao xmlMd5Dao();
}
