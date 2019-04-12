// Name                 Pierce hepburn
// Student ID           S1711876
// Programme of Study   Computing

package org.s1711876.earthquakes.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.db.SupportSQLiteQuery;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.RawQuery;

import java.util.List;

// dao for earthquake in room database
@Dao
public interface EarthquakeDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertMultipleEarthquake(List<Earthquake> earthquake);

    @Query("DELETE FROM earthquake")
    int deleteAll();

    @RawQuery(observedEntities = Earthquake.class)
    List<Earthquake> getEarthquakesFiltered(SupportSQLiteQuery query);

    @Query("SELECT * FROM earthquake")
    LiveData<List<Earthquake>> getAllEarthquake();

}
