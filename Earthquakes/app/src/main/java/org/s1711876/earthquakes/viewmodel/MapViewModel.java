// Name                 Pierce hepburn
// Student ID           S1711876
// Programme of Study   Computing

package org.s1711876.earthquakes.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import org.s1711876.earthquakes.database.Earthquake;
import org.s1711876.earthquakes.database.Repository;

import java.util.List;

// Map view model which allows Map fragment access to the quake livedata and does not do much else
public class MapViewModel extends AndroidViewModel {

    private Repository repository;

    public MapViewModel(@NonNull Application application) {
        super(application);
        repository = Repository.getClassInstance(application.getApplicationContext());
    }

    public LiveData<List<Earthquake>> getFilteredLiveData() { return repository.getEarthquakesFilteredLiveData(); }
}
