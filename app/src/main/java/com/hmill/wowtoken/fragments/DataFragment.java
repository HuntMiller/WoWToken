package com.hmill.wowtoken.fragments;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.hmill.wowtoken.R;
import com.hmill.wowtoken.activities.MainActivity;
import com.hmill.wowtoken.util.Constants;
import com.hmill.wowtoken.util.TokenInfo;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.DataPointInterface;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.OnDataPointTapListener;
import com.jjoe64.graphview.series.Series;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;


public class DataFragment extends Fragment {

    private static final boolean canZoomAndScale = true;
    private static final int dataPointsToShow = 250;

    private TextView region, lowPrice, currentPrice, highPrice, updated;
    private SeekBar seekBar;
    private GraphView graph;

    private TokenInfo token;
    private String regionTitle;


    public DataFragment() {
        // Required empty public constructor
    }

    public static DataFragment newInstance() {
        DataFragment fragment = new DataFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_data, container, false);
        region = (TextView) v.findViewById(R.id.region);
        currentPrice = (TextView) v.findViewById(R.id.currentPrice);
        lowPrice = (TextView) v.findViewById(R.id.lowPrice);
        highPrice = (TextView) v.findViewById(R.id.highPrice);
        updated = (TextView) v.findViewById(R.id.updated);
        seekBar = (SeekBar) v.findViewById(R.id.seekBar);
        graph = (GraphView) v.findViewById(R.id.graph);

        updateFragment();

        return v;
    }

    private int sanitize(String string) {
        String pureInts = string.replaceAll("[^0-9.]", "");
        return Integer.parseInt(pureInts);
    }

    /*
    Calculate percentage of X in between A and B
     */
    private int calculateSeekbarPercentage(int x, int a, int b) {
        double xx = (double) x;
        double aa = (double) a;
        double bb = (double) b;
        double percent = ((xx - aa) / (bb - aa)) * 100.0;
        return (int) percent;
    }

    private LineGraphSeries createSeriesFromHistory(ArrayList arrayList) {
        LineGraphSeries<DataPoint> series = new LineGraphSeries<>();
        for (int i = 0; i < arrayList.size(); i++) {
            Object o = arrayList.get(i);
            JSONArray ja = (JSONArray) o;
            try {
                int eInt = (int) ja.get(0);
                String eString = String.valueOf(eInt);
                long epoch = Long.parseLong(eString);
                Date date = new java.util.Date(epoch * 1000);

                DataPoint datapoint = new DataPoint(i, Integer.valueOf(ja.get(1).toString()));
                series.appendData(datapoint, true, arrayList.size());
            } catch (JSONException e) {

            }
        }

        return series;
    }

    public void updateFragment() {
        switch (MainActivity.regionIndex) {
            case TokenInfo.INDEX_NORTH_AMERICA:
                token = MainActivity.NA_Token;
                regionTitle = "North America";
                break;
            case TokenInfo.INDEX_EUROPE:
                token = MainActivity.EU_Token;
                regionTitle = "Europe";
                break;
            case TokenInfo.INDEX_CHINA:
                token = MainActivity.CN_Token;
                regionTitle = "China";
                break;
            case TokenInfo.INDEX_TAIWAN:
                token = MainActivity.TW_Token;
                regionTitle = "Taiwan";
                break;
            case TokenInfo.INDEX_KOREA:
                token = MainActivity.KR_Token;
                regionTitle = "Korea";
                break;
            default:
                token = MainActivity.NA_Token;
                regionTitle = "North America";
                break;
        }

        region.setText(regionTitle);
        currentPrice.setText(token.getCurrentPrice());
        lowPrice.setText(token.getLowPrice());
        highPrice.setText(token.getHighPrice());
        updated.setText(token.getUpdated());
        //Calculate % of seekbar
        String currentString = currentPrice.getText().toString();
        String lowString = lowPrice.getText().toString();
        String highString = highPrice.getText().toString();
        int currentInt = 0;
        int lowInt = 0;
        int highInt = 0;
        try {
            currentInt = sanitize(currentString);
            lowInt = sanitize(lowString);
            highInt = sanitize(highString);
        } catch (NumberFormatException e) {
            Log.e(Constants.TAG, e.toString());
        }
        int seekPerc = calculateSeekbarPercentage(currentInt, lowInt, highInt);
        seekBar.setProgress(seekPerc);

        ArrayList history = new ArrayList();
        history.clear();
        history = token.getHistory();
        LineGraphSeries series = createSeriesFromHistory(history);
        graph.removeAllSeries();
        graph.addSeries(series);

        graph.getViewport().setScalableY(canZoomAndScale);

        //Set zoom to show last X amount of datapoints
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(0);
        graph.getViewport().setMaxX(dataPointsToShow);
        graph.getViewport().scrollToEnd();


        series.setOnDataPointTapListener(new OnDataPointTapListener() {
            @Override
            public void onTap(Series series, DataPointInterface dataPoint) {
                int cost = (int) dataPoint.getY();
                NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.US);
                String formatPrice = numberFormat.format(cost) + "g";


                String string = formatPrice + "\n" + dataPoint.getX();
                MainActivity.displaySnackbar(string, Snackbar.LENGTH_LONG);
            }
        });
        series.setDrawDataPoints(true);
        series.setDataPointsRadius(5);


    }

}
