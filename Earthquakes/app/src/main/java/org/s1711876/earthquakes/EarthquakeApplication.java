// Name                 Pierce hepburn
// Student ID           S1711876
// Programme of Study   Computing

package org.s1711876.earthquakes;

import android.app.Application;
import android.content.Context;
import android.widget.Toast;

// class used to get the context of the application when needed by view models
public class EarthquakeApplication extends Application {

    private static Context context;

    public void onCreate() {
        super.onCreate();
        EarthquakeApplication.context = getApplicationContext();

        // API acknowledgement when app is first opened
        Toast.makeText(this, "The earthquake data used in this app was collected from http://quakes.bgs.ac.uk/feeds/MhSeismology.xml", Toast.LENGTH_LONG).show();
    }

    public static Context getAppContext() {
        return EarthquakeApplication.context;
    }
}