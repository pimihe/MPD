// Name                 Pierce hepburn
// Student ID           S1711876
// Programme of Study   Computing

package org.s1711876.earthquakes.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.annotation.NonNull;

import org.s1711876.earthquakes.EarthquakeApplication;
import org.s1711876.earthquakes.database.Earthquake;
import org.s1711876.earthquakes.database.Repository;
import org.s1711876.earthquakes.database.XmlMd5;
import org.s1711876.earthquakes.enums.ActiveFragment;
import org.s1711876.earthquakes.utility.NetworkHelper;
import org.s1711876.earthquakes.utility.XmlHelper;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

public class MainViewModel extends AndroidViewModel {

    private Repository repository;

    private ActiveFragment activeFragment = ActiveFragment.REGISTER;

    // live data to post feedback to main activity
    private MutableLiveData<String> mainFeedbackLiveData = new MutableLiveData<>(); // livedata used to post feedback to the main activity to show user toast messages

    private boolean filterOpen = false;

    public LiveData<List<Earthquake>> getEarthquakesLiveData() { return repository.getEarthquakesLiveData(); }

    public LiveData<String> getFeedbackLivedata() { return mainFeedbackLiveData; }

    public ActiveFragment getActiveFragment() { return activeFragment; }
    public void setActiveFragment(ActiveFragment activeFragment) { this.activeFragment = activeFragment; }

    public void setFilterOpen(boolean filterOpen) { this.filterOpen = filterOpen; }
    public boolean isFilterOpen() { return filterOpen; }

    public MainViewModel(@NonNull Application application) {
        super(application);
        repository = Repository.getClassInstance(application.getApplicationContext());
        repository.applyFilter(null, null);
        HandlerTask.run();
    }

    private Handler Handler = new Handler();
    private Runnable HandlerTask = new Runnable()
    {
        @Override
        public void run() {
            Handler.postDelayed(HandlerTask, 1000*15*60); // check for new xml using task every 15 mins
            AsyncNetworkTask asyncNetworkTask = new AsyncNetworkTask();
            asyncNetworkTask.execute("http://quakes.bgs.ac.uk/feeds/MhSeismology.xml");
        }
    };

    public void reapplyFilter() {
        repository.reapplyFilter();
    }

    public void setRepositoryLocation(Location location) {
        repository.setLocation(location);
    }

    // code for timed network request below
    private class AsyncNetworkTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... strings) {
            try {

                if(!NetworkHelper.getNetworkState(EarthquakeApplication.getAppContext())) {
                    mainFeedbackLiveData.postValue("Could not get new earthquake data check internet connection");
                    return null;
                }

                // get xml string
                String xmlString = NetworkHelper.getUrl(strings[0]);

                if (xmlString == null) {
                    mainFeedbackLiveData.postValue("Failed to get new earthquake data check internet connection");
                    return null;
                }

                // get md5 hash of the last xml string received
                String lastXmlMd5String = "";
                XmlMd5 lastXmlMd5 = repository.getLastXmlMd5();
                if (lastXmlMd5 != null) {
                    lastXmlMd5String = lastXmlMd5.getMd5();
                }

                // get new md5 string by hashing xml that just got
                String newXmlMd5String = getStringMd5(xmlString);
                if(newXmlMd5String.equals(lastXmlMd5String)) { // if hashes are same no change in data so nothing happens
                    return null;
                }

                // set new md5
                repository.setLastXmlMd5(new XmlMd5(1, getStringMd5(xmlString)));
                List<Earthquake> parsedQuakes = XmlHelper.parseXml(xmlString);
                repository.addEarthquakes(parsedQuakes);
                mainFeedbackLiveData.postValue("Earthquake data updated");
            } catch (IOException e) {
                mainFeedbackLiveData.postValue("Failed to get new earthquake data check internet connection");
                e.printStackTrace();
            }
            return null;
        }
    }

    // taken from https://stackoverflow.com/questions/3934331/how-to-hash-a-string-in-android and modified
    private String getStringMd5(String str) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(str.getBytes());
            byte messageDigest[] = md.digest();
            StringBuilder stringBuffer = new StringBuilder();
            for (int i = 0; i < messageDigest.length; i++) {
                stringBuffer.append(Integer.toHexString(0xFF & messageDigest[i]));
            }
            return stringBuffer.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

}
