// Name                 Pierce hepburn
// Student ID           S1711876
// Programme of Study   Computing

package org.s1711876.earthquakes.fragment;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;

import android.view.View;
import android.view.ViewGroup;

import org.s1711876.earthquakes.R;
import org.s1711876.earthquakes.adapter.EarthquakeAdapter;
import org.s1711876.earthquakes.database.Earthquake;
import org.s1711876.earthquakes.viewmodel.RegisterViewModel;

import java.util.ArrayList;
import java.util.List;

public class RegisterFragment extends Fragment {

    private List<Earthquake> earthquakeData = new ArrayList<>();
    private RecyclerView recyclerView;
    private GridLayoutManager manager;
    private EarthquakeAdapter earthquakeAdapter;

    public RegisterFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);

        initViewModel();

        return view;
    }

    private void initViewModel() { // init view model starts listening to the livedata so register can be changed when new filter or data is received
        final Observer<List<Earthquake>> earthquakeObserver = new Observer<List<Earthquake>>() {
            @Override
            public void onChanged(@Nullable List<Earthquake> earthquakes) {
                earthquakeData.clear();
                if (earthquakes != null) {
                    earthquakeData.addAll(earthquakes);

                    // code below makes the register items responsive depending on device screen size
                    final Point size = new Point();
                    getActivity().getWindowManager().getDefaultDisplay().getSize(size);
                    manager = new GridLayoutManager(getActivity(), 12, GridLayoutManager.VERTICAL, false);
                    manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                        @Override public int getSpanSize(int position) {
                            if(size.x<700)return 12;
                            if(size.x<1081)return 6;
                            if(size.x<1200)return 4;
                            return 3;
                        }
                    });
                    recyclerView.setLayoutManager(manager);
                    earthquakeAdapter = new EarthquakeAdapter(getActivity(), earthquakeData);
                    recyclerView.setAdapter(earthquakeAdapter);
                }
            }
        };

        // get instance of view model
        RegisterViewModel viewModel = ViewModelProviders.of(this).get(RegisterViewModel.class);
        viewModel.getFilteredLiveData().observe(this, earthquakeObserver);
    }

}
