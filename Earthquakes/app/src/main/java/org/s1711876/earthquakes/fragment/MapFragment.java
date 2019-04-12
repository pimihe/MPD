// Name                 Pierce hepburn
// Student ID           S1711876
// Programme of Study   Computing

package org.s1711876.earthquakes.fragment;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.s1711876.earthquakes.EarthquakeApplication;
import org.s1711876.earthquakes.EarthquakeDetail;
import org.s1711876.earthquakes.R;
import org.s1711876.earthquakes.database.Earthquake;
import org.s1711876.earthquakes.viewmodel.MapViewModel;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MapFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private Context context;
    private List<Earthquake> earthquakeData = new ArrayList<>();
    private int[] markerPosArr;
    private static GoogleMap googleMap;

    @BindView(R.id.progressContainer) LinearLayout progressContainer;
    @BindView(R.id.progressTextView) TextView progressTextView;
    @BindView(R.id.progress) ProgressBar progress;

    public MapFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        initViewModel();

        ButterKnife.bind(this, view);

        // progress bar setup
        progress.setIndeterminate(false);

        // get reference to map fragment and set function to run when ready
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.googleMap);
        try {
            mapFragment.getMapAsync(this);
        } catch (Exception e) {
            Toast.makeText(context, "Failed to load map try reloading app", Toast.LENGTH_LONG).show();
        }

        return view;
    }

    private void initViewModel() {
        final Observer<List<Earthquake>> earthquakeObserver = new Observer<List<Earthquake>>() {
            @Override
            public void onChanged(@Nullable List<Earthquake> earthquakes) {
                earthquakeData.clear();
                if (earthquakes != null) {
                    earthquakeData.addAll(earthquakes);
                    if (googleMap != null) refreshMapPoints();
                    progressContainer.setVisibility(LinearLayout.VISIBLE);
                }
            }
        };
        MapViewModel viewModel;
        viewModel = ViewModelProviders.of(this).get(MapViewModel.class);
        viewModel.getFilteredLiveData().observe(this, earthquakeObserver);
    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onMapReady(GoogleMap mapReference) {
        googleMap = mapReference;
        googleMap.setOnMarkerClickListener(this);
        refreshMapPoints();
    }

    public void refreshMapPoints()
    {
        googleMap.clear();

        int earthquakesSize = earthquakeData.size();
        Earthquake[] quakesArr = new Earthquake[earthquakesSize];

        for(int i = 0; i < earthquakesSize;i++) {
            quakesArr[i] = earthquakeData.get(i);
        }

        MarkerAsyncTask markerAsyncTask = new MarkerAsyncTask();
        markerAsyncTask.execute(quakesArr);
    }

    // adding markers must be done on the ui thread and adding custom markers is resource intensive therefor adding 50-100 markers results in complete unresponsiveness for 2-3 seconds
    // to try and fix this the below async task is an attempt to lazy load markers resulting in a laggy but not completely unusable experience for 4-5 seconds
    public class MarkerAsyncTask extends AsyncTask<Earthquake, Void, Void> {
        @Override
        protected Void doInBackground(Earthquake... earthquakes) {
            markerPosArr = new int[earthquakeData.size()];
            for (int i=0; i<earthquakes.length; i++) {
                try {
                    Thread.sleep(75);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Earthquake earthquake = earthquakes[i];
                LatLng pos = new LatLng(earthquake.getLatitude(), earthquake.getLongitude());
                float hueValue = (float)((90/(double)(earthquakes.length))*(i+1));
                final MarkerOptions options = new MarkerOptions()
                        .position(pos)
                        .icon(BitmapDescriptorFactory.defaultMarker(hueValue));
                if (getActivity() != null) {
                    final int finalI = i;
                    final int finalQuakesLength = earthquakes.length;
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            String ldPt1 = EarthquakeApplication.getAppContext().getString(R.string.ld_mp_pnt)+" ";
                            String ldPt2 = " "+EarthquakeApplication.getAppContext().getString(R.string.of)+" ";
                            String ldStr = ldPt1+(finalI+1)+ldPt2+finalQuakesLength;
                            progressTextView.setText(ldStr);
                            int percComplete = (int) (((double)(finalI+1)/(double)finalQuakesLength)*100);
                            progress.setProgress(percComplete);
                            markerPosArr[finalI] = Integer.parseInt(googleMap.addMarker(options).getId().replace("m",""));
                            if(finalI+1 == finalQuakesLength)  progressContainer.setVisibility(LinearLayout.GONE); // progress finished so hide progress report
                        }
                    });
                }
            }
            return null;
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {// whenever marker is clicked load up the earthquake detail
        int pos = Integer.parseInt(marker.getId().replace("m",""));
        Intent intent = new Intent(context, EarthquakeDetail.class);

        int index = 0;
        for(int i = 0; i < markerPosArr.length;i++) {
            if(markerPosArr[i] == pos) index = i;
        }

        intent.putExtra("PUBLIC_EARTHQUAKE_KEY",earthquakeData.get(index));
        context.startActivity(intent);
        return false;
    }
}
