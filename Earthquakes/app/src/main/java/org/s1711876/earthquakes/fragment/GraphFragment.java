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

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.DataPointInterface;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.OnDataPointTapListener;
import com.jjoe64.graphview.series.PointsGraphSeries;
import com.jjoe64.graphview.series.Series;

import org.s1711876.earthquakes.EarthquakeDetail;
import org.s1711876.earthquakes.R;
import org.s1711876.earthquakes.database.Earthquake;
import org.s1711876.earthquakes.viewmodel.GraphViewModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class GraphFragment extends Fragment {

    private Context context;

    // get graph view items
    // graphing library from http://www.android-graphview.org/
    @BindView(R.id.graphMagnitude) GraphView graphMagnitude;
    @BindView(R.id.graphDepth) GraphView graphDepth;
    @BindView(R.id.scatterGraph) GraphView scatterGraph;

    private GraphViewModel viewModel;
    private List<Earthquake> earthquakeData = new ArrayList<>();

    public GraphFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_graph, container, false);

        initViewModel();

        ButterKnife.bind(this, view);

        graphMagnitude.setTitle("Magnitude Ordered By Current Sort");
        graphMagnitude.getGridLabelRenderer().setHorizontalLabelsVisible(false);
        graphMagnitude.getGridLabelRenderer().setVerticalAxisTitle("Magnitude (M)");

        graphDepth.setTitle("Depth Ordered By Current Sort");
        graphDepth.getGridLabelRenderer().setHorizontalLabelsVisible(false);
        graphDepth.getGridLabelRenderer().setVerticalAxisTitle("Depth (KM)");

        scatterGraph.setTitle("Magnitude Against Depth");
        scatterGraph.getGridLabelRenderer().setHorizontalAxisTitle("Depth (KM)");
        scatterGraph.getGridLabelRenderer().setVerticalAxisTitle("Magnitude (M)");

        return view;
    }

    private void initViewModel() { // creates view model for this fragment and starts observing live data for changes
        final Observer<List<Earthquake>> earthquakeObserver = new Observer<List<Earthquake>>() {
            @Override
            public void onChanged(@Nullable List<Earthquake> earthquakes) {
                earthquakeData.clear();
                earthquakeData.addAll(earthquakes);
                refreshGraph();
            }
        };
        viewModel = ViewModelProviders.of(this).get(GraphViewModel.class);
        viewModel.getFilteredLiveData().observe(this, earthquakeObserver);
    }

    // refreshes graphs. used when new filter applied or on first load
    public void refreshGraph()
    {
        PlotGraphAsyncTask plotGraphAsyncTask = new PlotGraphAsyncTask();
        plotGraphAsyncTask.execute(graphMagnitude,graphDepth,scatterGraph,earthquakeData,context);
    }

    // async task to load graph to prevent blocking
    public static class PlotGraphAsyncTask extends AsyncTask<Object, Void, Void> {
        @Override
        protected Void doInBackground(Object... params) {

            GraphView graphMagnitude = (GraphView) params[0];
            GraphView graphDepth = (GraphView) params[1];
            GraphView scatterGraph = (GraphView) params[2];
            final List<Earthquake> earthquakeData = (List<Earthquake>) params[3];
            final Context context = (Context) params[4];

            graphMagnitude.removeAllSeries();

            graphDepth.removeAllSeries();

            scatterGraph.removeAllSeries();

            int earthquakesSize = earthquakeData.size();
            if(earthquakesSize == 0) return null;

            DataPoint[] dataPointsMagnitude = new DataPoint[earthquakesSize];
            DataPoint[] dataPointsDepth = new DataPoint[earthquakesSize];
            DataPoint[] scatterPoints = new DataPoint[earthquakesSize];

            // scatter plot requires x to be added in ascending order so requires own sorted arraylist
            final ArrayList<Earthquake> tempList = new ArrayList<>();
            tempList.addAll(earthquakeData);
            Collections.sort(tempList, new Comparator<Earthquake>() {
                @Override
                public int compare(Earthquake o1, Earthquake o2) {
                    return o1.getDepth()-o2.getDepth();
                }
            });

            for (int i=0; i<earthquakesSize; i++)
            {
                dataPointsMagnitude[i] = new DataPoint(i, earthquakeData.get(i).getMagnitude());
                dataPointsDepth[i] = new DataPoint(i, earthquakeData.get(i).getDepth());

                double xValue = tempList.get(i).getDepth()+((double)(i+1)/10000);
                scatterPoints[i] = new DataPoint(xValue, tempList.get(i).getMagnitude());
            }

            // below this point in function series are added to each graph view
            graphMagnitude.addSeries(new LineGraphSeries<>(dataPointsMagnitude));
            PointsGraphSeries<DataPoint> magnitudePoints = new PointsGraphSeries<>(dataPointsMagnitude);
            magnitudePoints.setSize(7);
            magnitudePoints.setOnDataPointTapListener(new OnDataPointTapListener() {
                @Override
                public void onTap(Series series, DataPointInterface dataPoint) {
                    Intent intent = new Intent(context, EarthquakeDetail.class);
                    intent.putExtra("PUBLIC_EARTHQUAKE_KEY",earthquakeData.get((int)Math.round(dataPoint.getX())));
                    context.startActivity(intent);
                }
            });
            graphMagnitude.addSeries(magnitudePoints);

            graphDepth.addSeries(new LineGraphSeries<>(dataPointsDepth));
            PointsGraphSeries<DataPoint> depthPoints = new PointsGraphSeries<>(dataPointsDepth);
            depthPoints.setSize(7);
            depthPoints.setOnDataPointTapListener(new OnDataPointTapListener() {
                @Override
                public void onTap(Series series, DataPointInterface dataPoint) {
                    Intent intent = new Intent(context, EarthquakeDetail.class);
                    intent.putExtra("PUBLIC_EARTHQUAKE_KEY",earthquakeData.get((int)Math.round(dataPoint.getX())));
                    context.startActivity(intent);
                }
            });
            graphDepth.addSeries(depthPoints);

            PointsGraphSeries<DataPoint> pointSeries = new PointsGraphSeries<>(scatterPoints);
            pointSeries.setSize(7);
            pointSeries.setOnDataPointTapListener(new OnDataPointTapListener() {
                @Override
                public void onTap(Series series, DataPointInterface dataPoint) {

                    // hack used here as graph library doesnt allow id to be used when assigning points so every point is seen as the same when clicked
                    // to get around this the id is added to the end of the x value so 5 would turn to 5.0003
                    String xString = dataPoint.getX()+"";
                    String offsetIndexString = xString.substring(xString.indexOf(".")+3).trim();
                    if(offsetIndexString.length() == 1) offsetIndexString = offsetIndexString+"0";
                    if(offsetIndexString.startsWith("0")) offsetIndexString = offsetIndexString.substring(1);

                    int index = Integer.parseInt(offsetIndexString)-1;

                    Intent intent = new Intent(context, EarthquakeDetail.class);
                    intent.putExtra("PUBLIC_EARTHQUAKE_KEY",tempList.get(index));
                    context.startActivity(intent);
                }
            });
            scatterGraph.addSeries(pointSeries);
            return null;
        }
    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        this.context = context;
    }

}
