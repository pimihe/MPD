// Name                 Pierce hepburn
// Student ID           S1711876
// Programme of Study   Computing

package org.s1711876.earthquakes;

import android.Manifest;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.Toast;

import org.s1711876.earthquakes.database.Earthquake;
import org.s1711876.earthquakes.enums.ActiveFragment;
import org.s1711876.earthquakes.fragment.FilterFragment;
import org.s1711876.earthquakes.fragment.GraphFragment;
import org.s1711876.earthquakes.fragment.MapFragment;
import org.s1711876.earthquakes.fragment.RegisterFragment;
import org.s1711876.earthquakes.viewmodel.MainViewModel;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends AppCompatActivity {

    private MainViewModel viewModel;
    private Context context;

    // use butter knife package to bind view items
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.filterPlaceholder) FrameLayout filterPlaceholder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;
        initViewModel();

        // request user permissions
        requestLocationPermission();

        // hide the bar at top of some devices to maximise screen space
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        // if filter fragment doesn't exist yet add it otherwise do nothing
        if(getSupportFragmentManager().findFragmentByTag("filter") == null) {
            FilterFragment filterFragment = new FilterFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.filterPlaceholder, filterFragment, "filter").commit();
        }

        // set filter menu to invisible on startup since view model value for visible or not has not been set to true yet
        updateFilterMenuState();

        // logic to select the correct fragment and display within fragment placeholder when oncreate is run
        switch (viewModel.getActiveFragment()) {
            case REGISTER:
                if(getSupportFragmentManager().findFragmentByTag("register") == null) {
                    RegisterFragment registerFragment = new RegisterFragment();
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragmentPlaceholder, registerFragment, "register").commit();
                }
                break;
            case MAP:
                if(getSupportFragmentManager().findFragmentByTag("map") == null) {
                    MapFragment mapFragment = new MapFragment();
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragmentPlaceholder, mapFragment, "map").commit();
                }
                break;
            case GRAPH:
                if(getSupportFragmentManager().findFragmentByTag("graph") == null) {
                    GraphFragment graphFragment = new GraphFragment();
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragmentPlaceholder, graphFragment, "graph").commit();
                }
                break;
        }
    }

    private void initViewModel() {
        // get reference to main activity view model
        viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        final Observer<List<Earthquake>> obs = new Observer<List<Earthquake>>() { // runs function whenever earthquakes live data changes
            @Override
            public void onChanged(@Nullable List<Earthquake> earthquakes) {
                // function applies the current filter set via the filter fragment by the user
                viewModel.reapplyFilter();
            }
        };

        final Observer<String> obsFeedback = new Observer<String>() {
            @Override
            public void onChanged(@Nullable String result) {
                Toast.makeText(context, result, Toast.LENGTH_LONG).show();
            }
        };


        // start observing the live data
        viewModel.getEarthquakesLiveData().observe(this, obs);
        viewModel.getFeedbackLivedata().observe(this, obsFeedback);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // place links to fragments in the toolbar dropdown menu
        getMenuInflater().inflate(R.menu.toolbar, menu);
        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // if user presses back button when filter is open intercept this and close filter menu
        switch(keyCode){
            case KeyEvent.KEYCODE_BACK:
                if(filterPlaceholder.getAlpha() == 1.0f) {
                    filterPlaceholder.animate().alpha(0.0f);
                    filterPlaceholder.setVisibility(View.GONE);
                    viewModel.setFilterOpen(false);
                }else{
                    return super.onKeyDown(keyCode, event);
                }
        }
        return true;
    }

    private void updateFilterMenuState() { // used to update whether filter menu is visible or not
        if(viewModel.isFilterOpen()) {
            filterPlaceholder.setVisibility(View.VISIBLE);
            filterPlaceholder.animate().alpha(1.0f);
        }else{
            filterPlaceholder.setVisibility(View.GONE);
            filterPlaceholder.animate().alpha(0.0f);
        }
    }

    // removes and reinserts filter display fragment to reset form fields
    public void resetFilterFragment() {
        if(getSupportFragmentManager().findFragmentByTag("filter") != null) {
            FilterFragment filterFragment = new FilterFragment();
            getSupportFragmentManager().beginTransaction().remove(getSupportFragmentManager().findFragmentByTag("filter")).commit();
            getSupportFragmentManager().beginTransaction().replace(R.id.filterPlaceholder, filterFragment, "filter").commit();
        }
    }

    // toggles the filter display
    public void toggleFilterDisplay() {
        viewModel.setFilterOpen(!viewModel.isFilterOpen());
        updateFilterMenuState();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) { // when user selects option from toolbar dropdown logic here displays the correct fragment or display menu if search icon button pressed
        switch (menuItem.getItemId()) {
            case R.id.action_filter:
                toggleFilterDisplay();
                return true;
            case R.id.route_register:
                if(getSupportFragmentManager().findFragmentByTag("register") == null) {
                    viewModel.setActiveFragment(ActiveFragment.REGISTER);
                    RegisterFragment registerFragment = new RegisterFragment();
                    getSupportFragmentManager().beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).replace(R.id.fragmentPlaceholder, registerFragment, "register").commit();
                }
                return true;
            case R.id.route_map:
                if(getSupportFragmentManager().findFragmentByTag("map") == null) {
                    viewModel.setActiveFragment(ActiveFragment.MAP);
                    MapFragment mapFragment = new MapFragment();
                    getSupportFragmentManager().beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).replace(R.id.fragmentPlaceholder, mapFragment, "map").commit();
                }
                return true;
            case R.id.route_graph:
                if(getSupportFragmentManager().findFragmentByTag("graph") == null) {
                    viewModel.setActiveFragment(ActiveFragment.GRAPH);
                    GraphFragment graphFragment = new GraphFragment();
                    getSupportFragmentManager().beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).replace(R.id.fragmentPlaceholder, graphFragment, "graph").commit();
                }
                return true;
            default:
                return super.onOptionsItemSelected(menuItem);
        }
    }

    // permission for location functions
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @AfterPermissionGranted(1)
    public void requestLocationPermission() {
        String[] perms = {Manifest.permission.ACCESS_FINE_LOCATION};
        if(EasyPermissions.hasPermissions(this, perms)) {
            // Acquire a reference to the system Location Manager
            LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

            // start listening for location updates
            LocationListener locationListener = new LocationListener() {
                public void onLocationChanged(Location location) {
                    // if new location found
                    if (location != null) viewModel.setRepositoryLocation(location);
                }

                // required functions for LocationListener
                public void onStatusChanged(String provider, int status, Bundle extras) { }
                public void onProviderEnabled(String provider) { }
                public void onProviderDisabled(String provider) { }
            };
            // ide flags error due to no permissions but should be ok as permission checks done on app startup and here is only reached after permission given
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 0, locationListener);
        }
        else {
            EasyPermissions.requestPermissions(this, "Please grant the location permission", 1, perms);
        }
    }
}
