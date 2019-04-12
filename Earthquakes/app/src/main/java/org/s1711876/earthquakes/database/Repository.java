// Name                 Pierce hepburn
// Student ID           S1711876
// Programme of Study   Computing

package org.s1711876.earthquakes.database;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.persistence.db.SimpleSQLiteQuery;
import android.content.Context;
import android.location.Location;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

// class used to retrieve data from the room database and create livedata which can be observed to update view on data changes
public class Repository {
    private static Repository classInstance;
    private EarthquakeDatabase earthquakeDatabase;
    private Executor executor = Executors.newSingleThreadExecutor();

    private LiveData<List<Earthquake>> earthquakesLiveData;
    private MutableLiveData<List<Earthquake>> earthquakesFilteredLiveData = new MutableLiveData<List<Earthquake>>();

    private String activeFilterString;
    private Location location;

    public LiveData<List<Earthquake>> getEarthquakesLiveData() { return earthquakesLiveData; }
    public MutableLiveData<List<Earthquake>> getEarthquakesFilteredLiveData() { return earthquakesFilteredLiveData; }

    public static Repository getClassInstance(Context context) {
        if (classInstance == null) {
            classInstance = new Repository(context);
        }
        return classInstance;
    }

    private Repository(Context context) {
        earthquakeDatabase = EarthquakeDatabase.getClassInstance(context);
        earthquakesLiveData = getAllEarthquakes();
    }

    public void addEarthquakes(final List<Earthquake> earthquakes) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    if (earthquakes != null) earthquakeDatabase.earthquakeDao().insertMultipleEarthquake(earthquakes);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private LiveData<List<Earthquake>> getAllEarthquakes() {
        return earthquakeDatabase.earthquakeDao().getAllEarthquake();
    }

    public void setLastXmlMd5(final XmlMd5 xmlMd5) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                earthquakeDatabase.xmlMd5Dao().setLastXmlMd5(xmlMd5);
            }
        });
    }

    public XmlMd5 getLastXmlMd5() {
        return earthquakeDatabase.xmlMd5Dao().getLastXmlMd5();
    }

    public void applyFilter(final String where, final String order) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                String sqlString = "SELECT * from earthquake";
                if (where != null) sqlString = sqlString + " WHERE " + where;
                if (order != null) {
                    sqlString = sqlString + " ORDER BY " + order;
                } else {
                    sqlString = sqlString + " ORDER BY pubDate DESC";
                }
                activeFilterString = sqlString;
                earthquakesFilteredLiveData.postValue(earthquakeDatabase.earthquakeDao().getEarthquakesFiltered(new SimpleSQLiteQuery(sqlString)));
            }
        });
    }

    public void reapplyFilter() {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                String sqlString;
                if(activeFilterString == null) {
                    sqlString = "SELECT * from earthquake ORDER BY pubDate DESC";
                } else {
                    sqlString = activeFilterString;
                }
                earthquakesFilteredLiveData.postValue(earthquakeDatabase.earthquakeDao().getEarthquakesFiltered(new SimpleSQLiteQuery(sqlString)));

            }
        });
    }

    public void setLocation(Location location) { this.location = location; }
    public Location getLocation() { return location; }
}
