// Name                 Pierce hepburn
// Student ID           S1711876
// Programme of Study   Computing

package org.s1711876.earthquakes;

import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.s1711876.earthquakes.database.Earthquake;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


import butterknife.ButterKnife;

import static org.s1711876.earthquakes.utility.UtilHelper.getFormattedLocationStringList;
import static org.s1711876.earthquakes.utility.UtilHelper.getHexColorFromNoRange;

public class EarthquakeDetail extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap gMapRef;

    Earthquake earthquake;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_earthquake_detail);

        // same code as main activity to remove top bar on some devices and get most screen space
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // to transfer the earthquake object to this activity it had to be turned to parcelable so convert back to object
        earthquake = getIntent().getExtras().getParcelable("PUBLIC_EARTHQUAKE_KEY");

        ButterKnife.bind(this);

        // get reference to map fragment and set function to run when ready
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.gMapFrag);
        mapFragment.getMapAsync(this);

        LinearLayout eqSummaryRoot = findViewById(R.id.eqSummaryRoot);
        TextView eqPubDate = findViewById(R.id.eqPubDate);
        TextView eqLocation1 = findViewById(R.id.eqLocation1);
        TextView eqLocation2 = findViewById(R.id.eqLocation2);
        TextView eqMagnitude = findViewById(R.id.eqMagnitude);
        TextView eqDepth = findViewById(R.id.eqDepth);
        TextView eqDepthChevron = findViewById(R.id.eqDepthChevron);
        ConstraintLayout eqCircleConLay = findViewById(R.id.eqCircleConLay);

        eqSummaryRoot.setBackgroundResource(0);

        // if device is portrait
        int orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            eqSummaryRoot.setOrientation(LinearLayout.HORIZONTAL);
        }

        Date date = new Date(earthquake.getPubDate()*1000);
        SimpleDateFormat formatter = new SimpleDateFormat("d/M/yyyy HH:mm");
        String formattedDate = formatter.format(date);

        eqPubDate.setText(formattedDate);

        List<String> locList = getFormattedLocationStringList(earthquake.getLocation());
        if(locList.size()>0) eqLocation1.setText(locList.get(0));
        if(locList.size()>1) eqLocation2.setText(locList.get(1));

        String magnitdeTxt = earthquake.getMagnitude()+ EarthquakeApplication.getAppContext().getString(R.string.M);
        String depthTxt = earthquake.getDepth()+ EarthquakeApplication.getAppContext().getString(R.string.KM);
        eqMagnitude.setText(magnitdeTxt);
        eqDepth.setText(depthTxt);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        GradientDrawable drawable = (GradientDrawable)eqCircleConLay.getBackground();
        drawable.setStroke(15, Color.parseColor( getHexColorFromNoRange(earthquake.getMagnitude(), -0.5,5,true) ) ); // set stroke width and stroke color
        Drawable wrappedDrawable = eqDepthChevron.getBackground().mutate();
        wrappedDrawable = DrawableCompat.wrap(wrappedDrawable);
        DrawableCompat.setTint(wrappedDrawable, Color.parseColor( getHexColorFromNoRange(earthquake.getDepth(), 1,15,false) ));
        DrawableCompat.setTintMode(wrappedDrawable, PorterDuff.Mode.SRC_IN);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.action_back:
                this.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BACK));
                this.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_BACK));
                return true;
            default:
                return super.onOptionsItemSelected(menuItem);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_detail, menu);
        return true;
    }

    // when the map is ready adds the location marker
    @Override
    public void onMapReady(GoogleMap mapRef) {
        gMapRef = mapRef;
        LatLng pos = new LatLng(earthquake.getLatitude(), earthquake.getLongitude());
        gMapRef.moveCamera(CameraUpdateFactory.newLatLng(pos));
        gMapRef.addMarker(new MarkerOptions().position(pos).title(earthquake.getLocation()));
    }

}
