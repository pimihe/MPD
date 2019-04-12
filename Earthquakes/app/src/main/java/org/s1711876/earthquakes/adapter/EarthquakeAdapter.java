// Name                 Pierce hepburn
// Student ID           S1711876
// Programme of Study   Computing

package org.s1711876.earthquakes.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.jetbrains.annotations.NotNull;
import org.s1711876.earthquakes.EarthquakeApplication;
import org.s1711876.earthquakes.EarthquakeDetail;
import org.s1711876.earthquakes.R;
import org.s1711876.earthquakes.database.Earthquake;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static org.s1711876.earthquakes.utility.UtilHelper.getFormattedLocationStringList;
import static org.s1711876.earthquakes.utility.UtilHelper.getHexColorFromNoRange;

public class EarthquakeAdapter extends RecyclerView.Adapter<EarthquakeAdapter.ViewHolder> {

    private List<Earthquake> earthquakes;
    private Context context;
    private static final String PUBLIC_EARTHQUAKE_KEY = "PUBLIC_EARTHQUAKE_KEY";

    public EarthquakeAdapter(Context context, List<Earthquake> items) {
        this.context = context;
        this.earthquakes = items;
    }

    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NotNull ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.earthquake_summary, viewGroup, false);
        return new ViewHolder(view);
    }

    // bind data to each item in list
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Earthquake earthquake = earthquakes.get(position);

        Date date = new Date(earthquake.getPubDate()*1000);
        SimpleDateFormat formatter = new SimpleDateFormat("d/M/yyyy HH:mm");
        String formattedDate = formatter.format(date);

        List<String> locList = getFormattedLocationStringList(earthquake.getLocation());
        if(locList.size()>0) holder.eqLocation1.setText(locList.get(0));
        if(locList.size()>1) holder.eqLocation2.setText(locList.get(1));

        holder.eqPubDate.setText(formattedDate);
        String magnitudeTxt = earthquake.getMagnitude()+ EarthquakeApplication.getAppContext().getString(R.string.M);
        String depthTxt = earthquake.getDepth()+ EarthquakeApplication.getAppContext().getString(R.string.KM);
        holder.eqMagnitude.setText(magnitudeTxt);
        holder.eqDepth.setText(depthTxt);

        // code to change color of magnitude circle and depth arrow and set content
        GradientDrawable drawable = (GradientDrawable)holder.eqCircleConLay.getBackground();
        drawable.setStroke(15, Color.parseColor( getHexColorFromNoRange(earthquake.getMagnitude(), -0.5,5,true) ) ); // set stroke width and stroke color
        Drawable wrappedDrawable = holder.eqDepthChevron.getBackground().mutate();
        wrappedDrawable = DrawableCompat.wrap(wrappedDrawable);
        DrawableCompat.setTint(wrappedDrawable, Color.parseColor( getHexColorFromNoRange(earthquake.getDepth(), 1,15,false) ));
        DrawableCompat.setTintMode(wrappedDrawable, PorterDuff.Mode.SRC_IN);

        // when a earthquake is clicked in list
        holder.view.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, EarthquakeDetail.class);
                intent.putExtra(PUBLIC_EARTHQUAKE_KEY,earthquake);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() { return earthquakes.size(); }

    // binds views to xml layout
    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.eqSummaryRoot) LinearLayout eqSummaryRoot;
        @BindView(R.id.linLay1) LinearLayout linLay1;
        @BindView(R.id.linLay2) LinearLayout linLay2;
        @BindView(R.id.eqCircleConLay) ConstraintLayout eqCircleConLay;
        @BindView(R.id.eqPubDate) TextView eqPubDate;
        @BindView(R.id.eqLocation1) TextView eqLocation1;
        @BindView(R.id.eqLocation2) TextView eqLocation2;
        @BindView(R.id.eqMagnitude) TextView eqMagnitude;
        @BindView(R.id.eqDepth) TextView eqDepth;
        @BindView(R.id.eqDepthChevron) TextView eqDepthChevron;
        public View view;
        ViewHolder(View view) {
            super(view);
            this.view = view;
            ButterKnife.bind(this, view);
        }
    }
}