// Name                 Pierce hepburn
// Student ID           S1711876
// Programme of Study   Computing

package org.s1711876.earthquakes.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import org.s1711876.earthquakes.EarthquakeApplication;
import org.s1711876.earthquakes.database.Repository;
import org.s1711876.earthquakes.filter.EarthquakeDateTimeFilter;
import org.s1711876.earthquakes.filter.EarthquakeDepthFilter;
import org.s1711876.earthquakes.filter.EarthquakeMagnitudeFilter;
import org.s1711876.earthquakes.filter.EarthquakeSortOrder;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class FilterViewModel extends AndroidViewModel {

    private static Repository repository;
    private static MutableLiveData<String> filterFeedbackLiveData = new MutableLiveData<>(); // livedata used to post feedback to the filter fragment to show user toast messages

    // get instance of repository
    public FilterViewModel(@NonNull Application application) {
        super(application);
        repository = Repository.getClassInstance(application.getApplicationContext());
    }

    public MutableLiveData<String> getFilterFeedbackLiveData() { return filterFeedbackLiveData; }

    // runs the filter async task
    public void applyFilter(EarthquakeDepthFilter df, EarthquakeMagnitudeFilter mf, EarthquakeDateTimeFilter tf,@NonNull EarthquakeSortOrder so) {
        FilterAsyncTask filterAsyncTask = new FilterAsyncTask();
        filterAsyncTask.execute(df, mf, tf, so);
    }

    private static class FilterAsyncTask extends AsyncTask<Object, Void, Void> {

        // function checks each filter object type and if not null adds what is needed to the whereString
        // which is used to query the room database
        @Override
        protected Void doInBackground(Object... params) {
            EarthquakeDepthFilter df = (EarthquakeDepthFilter) params[0];
            EarthquakeMagnitudeFilter mf = (EarthquakeMagnitudeFilter) params[1];
            EarthquakeDateTimeFilter tf = (EarthquakeDateTimeFilter) params[2];
            EarthquakeSortOrder so = (EarthquakeSortOrder) params[3];

            String whereString=null, sortString=null;

            if(df != null) {
                switch (df.getComparator()) {
                    case EQUAL:
                        whereString = "depth="+df.getDepth1();
                        break;
                    case LESS_THAN:
                        whereString = "depth<"+df.getDepth1();
                        break;
                    case GREATER_THAN:
                        whereString = "depth>"+df.getDepth1();
                        break;
                    case BETWEEN:
                        whereString = "depth BETWEEN "+df.getDepth1()+" AND "+df.getDepth2();
                        break;
                }
            }

            if(mf != null) {
                if(whereString!=null) {
                    whereString += " AND ";
                }else{
                    whereString="";
                }
                switch (mf.getComparator()) {
                    case EQUAL:
                        whereString += "magnitude="+mf.getMagnitude1();
                        break;
                    case LESS_THAN:
                        whereString += "magnitude<"+mf.getMagnitude1();
                        break;
                    case GREATER_THAN:
                        whereString += "magnitude>"+mf.getMagnitude1();
                        break;
                    case BETWEEN:
                        whereString += "magnitude BETWEEN "+mf.getMagnitude1()+" AND "+mf.getMagnitude2();
                        break;
                }
            }

            if(tf != null) {
                if(whereString!=null) {
                    whereString += " AND ";
                }else{
                    whereString="";
                }
                switch (tf.getComparator()) {
                    case EQUAL:
                        whereString += "pubDate BETWEEN "+tf.getDateTime1()+" AND "+(tf.getDateTime1()+86400);
                        break;
                    case LESS_THAN:
                        whereString += "pubDate<"+tf.getDateTime1();
                        break;
                    case GREATER_THAN:
                        whereString += "pubDate>"+tf.getDateTime1();
                        break;
                    case BETWEEN:
                        whereString += "pubDate BETWEEN "+tf.getDateTime1()+" AND "+tf.getDateTime2();
                        break;
                }
            }

            // once each query section has been checked sort is checked
            String sortOrderStr = "DESC";
            if(so.isSortByAscending()) sortOrderStr = "ASC";
            if(so.getSortOption() != null) {
                switch (so.getSortOption()) {
                    case DATE:
                        sortString = "pubDate "+sortOrderStr;
                        break;
                    case MOST_NORTHERLY:
                        sortString = "latitude "+sortOrderStr;
                        break;
                    case MOST_EASTERLY:
                        sortString = "longitude "+sortOrderStr;
                        break;
                    case DISTANCE_FROM:
                        // geocoder used to allow user to type in location and returns lat, long numbers
                        Geocoder coder = new Geocoder(EarthquakeApplication.getAppContext(), new Locale("en"));
                        List<Address> address = null;
                        try {
                            // limit location to square around uk to imporve accuracy
                            address = coder.getFromLocationName(so.getDistanceFrom(), 1 , 50.103190, -7.64133,60.15456,1.75159);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        if (address != null && address.size() == 1) {
                            // if location was found use innacurate but good enough over short distances math in sql statement to sort by distance from point
                            sortString = "(("+address.get(0).getLatitude()+" - latitude)*("+address.get(0).getLatitude()+" - latitude)) + (("+address.get(0).getLongitude()+" - longitude)*("+address.get(0).getLongitude()+" - longitude)) "+sortOrderStr;
                        }else{
                            filterFeedbackLiveData.postValue("Filter was not applied as the location could not be found");
                            return null;
                        }
                        break;
                    case DISTANCE_FROM_ME:
                        Location userLocation = repository.getLocation();
                        if (userLocation != null) {
                            double userLat = userLocation.getLatitude();
                            double userLng = userLocation.getLongitude();
                            // if location was found use innacurate but good enough over short distances math in sql statement to sort by distance from point
                            sortString = "(("+userLat+" - latitude)*("+userLat+" - latitude)) + (("+userLng+" - longitude)*("+userLng+" - longitude)) "+sortOrderStr;
                        }else{
                            filterFeedbackLiveData.postValue("Filter was not applied as your location could not be found");
                            return null;
                        }
                        break;
                    case DEPTH:
                        sortString = "depth "+sortOrderStr;
                        break;
                    case MAGNITUDE:
                        sortString = "magnitude "+sortOrderStr;
                        break;
                }
            }

            // apply filter to repository and notify user through feedback livedata
            filterFeedbackLiveData.postValue("Filter applied");
            repository.applyFilter(whereString, sortString);

            return null;
        }
    }

}
