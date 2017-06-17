package com.hmill.wowtoken.fragments;

import android.animation.ValueAnimator;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.hmill.wowtoken.R;
import com.hmill.wowtoken.activities.MainActivity;
import com.hmill.wowtoken.network.VolleySingleton;
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
import org.json.JSONObject;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;


public class DataFragment extends Fragment {

    private static final boolean canZoomAndScale = true;
    //# data points shown
    private static final double oneDay = 1*24*60*60;
    private static final double threeDays = 3*24*60*60;
    private static final double oneWeek = 7*24*60*60;
    private static final double oneMonth = 30*24*60*60;
    private static final double toMilliseconds = 1000;

    private static TextView region, lowPrice, currentPrice, highPrice, updated, apiResult;
    private static SeekBar seekBar;
    private static GraphView graph;
    private static Button twentyFourHourButton, threeDayButton, oneWeekButton, oneMonthButton;

    private static TokenInfo token;
    private static String regionTitle;
    private static ArrayList history;

    public static ArrayList tokens = new ArrayList();
    public static TokenInfo NA_Token = new TokenInfo();
    public static TokenInfo EU_Token = new TokenInfo();
    public static TokenInfo CN_Token = new TokenInfo();
    public static TokenInfo TW_Token = new TokenInfo();
    public static TokenInfo KR_Token = new TokenInfo();


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
        apiResult = (TextView) v.findViewById(R.id.apiResult);
        seekBar = (SeekBar) v.findViewById(R.id.seekBar);
        graph = (GraphView) v.findViewById(R.id.graph);
        twentyFourHourButton = (Button) v.findViewById(R.id.twenty_four_hour_button);
        threeDayButton = (Button) v.findViewById(R.id.three_day_button);
        oneWeekButton = (Button) v.findViewById(R.id.one_week_button);
        oneMonthButton = (Button) v.findViewById(R.id.one_month_button);

        setupListeners();
        queueUrl(MainActivity.context, TokenInfo.URL_WITH_HISTORY);

        return v;
    }

    private void setupListeners() {
        twentyFourHourButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Date date = new Date();
                graph.getViewport().setXAxisBoundsManual(true);
                graph.getViewport().setMinX(date.getTime());
                graph.getViewport().setMaxX(date.getTime() + oneDay*toMilliseconds);
                graph.getViewport().scrollToEnd();
            }
        });

        threeDayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Date date = new Date();
                graph.getViewport().setXAxisBoundsManual(true);
                graph.getViewport().setMinX(date.getTime());
                graph.getViewport().setMaxX(date.getTime() + threeDays*toMilliseconds);
                graph.getViewport().scrollToEnd();
            }
        });

        oneWeekButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Date date = new Date();
                graph.getViewport().setXAxisBoundsManual(true);
                graph.getViewport().setMinX(date.getTime());
                graph.getViewport().setMaxX(date.getTime() + oneWeek*toMilliseconds);
                graph.getViewport().scrollToEnd();
            }
        });

        oneMonthButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Date date = new Date();
                graph.getViewport().setXAxisBoundsManual(true);
                graph.getViewport().setMinX(date.getTime());
                graph.getViewport().setMaxX(date.getTime() + oneMonth*toMilliseconds);
                graph.getViewport().scrollToEnd();
            }
        });

    }

    private static int sanitize(String string) {
        String pureInts = string.replaceAll("[^0-9.]", "");
        return Integer.parseInt(pureInts);
    }

    /*
    Calculate percentage of X in between A and B
     */
    private static int calculateSeekbarPercentage(int x, int a, int b) {
        double xx = (double) x;
        double aa = (double) a;
        double bb = (double) b;
        double percent = ((xx - aa) / (bb - aa)) * 100.0;
        return (int) percent;
    }

    private static LineGraphSeries createSeriesFromHistory(ArrayList arrayList) {
        LineGraphSeries<DataPoint> series = new LineGraphSeries<>();
        for (int i = 0; i < arrayList.size(); i++) {
            Object o = arrayList.get(i);
            JSONArray ja = (JSONArray) o;
            try {
                int eInt = (int) ja.get(0);
                String eString = String.valueOf(eInt);
                long epoch = Long.parseLong(eString);
                Date date = new java.util.Date(epoch * 1000);

                DataPoint datapoint = new DataPoint(date, Integer.valueOf(ja.get(1).toString()));
                series.appendData(datapoint, true, arrayList.size());
            } catch (JSONException e) {

            }
        }

        return series;
    }

    /*
    Update fragment with a new tokens data
     */
    public static void updateFragment() {
        switch (MainActivity.regionIndex) {
            case TokenInfo.INDEX_NORTH_AMERICA:
                token = NA_Token;
                regionTitle = "North America";
                break;
            case TokenInfo.INDEX_EUROPE:
                token = EU_Token;
                regionTitle = "Europe";
                break;
            case TokenInfo.INDEX_CHINA:
                token = CN_Token;
                regionTitle = "China";
                break;
            case TokenInfo.INDEX_TAIWAN:
                token = TW_Token;
                regionTitle = "Taiwan";
                break;
            case TokenInfo.INDEX_KOREA:
                token = KR_Token;
                regionTitle = "Korea";
                break;
            default:
                token = NA_Token;
                regionTitle = "North America";
                break;
        }

        region.setText(regionTitle);
        currentPrice.setText(token.getCurrentPrice());
        lowPrice.setText(token.getLowPrice());
        highPrice.setText(token.getHighPrice());
        updated.setText(token.getUpdated());
        apiResult.setText("API Result: " + token.getAPIResult());
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

        ValueAnimator anim = ValueAnimator.ofInt(seekBar.getProgress(), seekPerc);
        if (Math.abs(seekPerc - seekBar.getProgress()) > 25)
            anim.setDuration(500);
        else
            anim.setDuration(250);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int animProgress = (Integer) animation.getAnimatedValue();
                seekBar.setProgress(animProgress);
            }
        });
        anim.start();

        seekBar.setProgress(seekPerc);
        seekBar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        history = new ArrayList();
        history.clear();
        history = token.getHistory();
        LineGraphSeries series = createSeriesFromHistory(history);
        graph.removeAllSeries();
        graph.addSeries(series);

        graph.getViewport().setScalableY(canZoomAndScale);
        graph.getGridLabelRenderer().setNumHorizontalLabels(3);
        graph.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(MainActivity.context));

        //Set zoom to show last X time worth of data
        Date date = new Date();
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(date.getTime());
        graph.getViewport().setMaxX(date.getTime() + threeDays*toMilliseconds);
        graph.getViewport().scrollToEnd();

        series.setOnDataPointTapListener(new OnDataPointTapListener() {
            @Override
            public void onTap(Series series, DataPointInterface dataPoint) {
                int cost = (int) dataPoint.getY();
                long epoch = (long) dataPoint.getX();
                Log.e(Constants.TAG, String.valueOf(epoch));
                Date date = new Date(epoch);
                NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.US);
                String formatPrice = numberFormat.format(cost) + "g";
                SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MM/dd/yy hh:mma");
                String formatDate = DATE_FORMAT.format(date);
                String string = formatDate + "\n" + formatPrice;
                MainActivity.displaySnackbar(string, Snackbar.LENGTH_INDEFINITE);
            }
        });
        series.setDrawDataPoints(true);
        series.setDataPointsRadius(5);

    }


    public static void queueUrl(final Context c, String url) {
        Log.d(Constants.TAG, "called queueUrl()");
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            MainActivity.rotateFAB(true);
                            //Update
                            JSONObject update = (JSONObject) response.get("update");
                            JSONObject history = (JSONObject) response.get("history");
                            /*
                            NA
                             */
                            JSONObject Na = (JSONObject) update.get(TokenInfo.NORTH_AMERICA);
                            JSONObject formatNA = (JSONObject) Na.get(TokenInfo.FORMATTED);
                            JSONArray NAHistory = (JSONArray) history.get(TokenInfo.NORTH_AMERICA);
                            NA_Token.setRegion(formatNA.get(TokenInfo.REGION).toString());
                            NA_Token.setUpdated(formatNA.get(TokenInfo.UPDATED).toString());
                            NA_Token.setLowPrice(formatNA.get(TokenInfo.LOW_PRICE).toString());
                            NA_Token.setCurrentPrice(formatNA.get(TokenInfo.CURRENT_PRICE).toString());
                            NA_Token.setHighPrice(formatNA.get(TokenInfo.HIGH_PRICE).toString());
                            NA_Token.setAPIResult(formatNA.get(TokenInfo.API_RESULT).toString());
                            NA_Token.clearHistory();
                            for (int i = 0; i < NAHistory.length(); i++) {
                                NA_Token.addToHistory(NAHistory.get(i));
                            }
                            /*
                            NA
                             */

                            /*
                            EU
                             */
                            JSONObject Eu = (JSONObject) update.get(TokenInfo.EUROPEAN);
                            JSONObject formatEU = (JSONObject) Eu.get(TokenInfo.FORMATTED);
                            JSONArray EUHistory = (JSONArray) history.get(TokenInfo.EUROPEAN);
                            EU_Token.setRegion(formatEU.get(TokenInfo.REGION).toString());
                            EU_Token.setUpdated(formatEU.get(TokenInfo.UPDATED).toString());
                            EU_Token.setLowPrice(formatEU.get(TokenInfo.LOW_PRICE).toString());
                            EU_Token.setCurrentPrice(formatEU.get(TokenInfo.CURRENT_PRICE).toString());
                            EU_Token.setHighPrice(formatEU.get(TokenInfo.HIGH_PRICE).toString());
                            EU_Token.setAPIResult(formatEU.get(TokenInfo.API_RESULT).toString());
                            EU_Token.clearHistory();
                            for (int i = 0; i < EUHistory.length(); i++) {
                                EU_Token.addToHistory(EUHistory.get(i));
                            }
                            /*
                            EU
                             */

                            /*
                            CN
                             */
                            JSONObject Cn = (JSONObject) update.get(TokenInfo.CHINESE);
                            JSONObject formatCN = (JSONObject) Cn.get(TokenInfo.FORMATTED);
                            JSONArray CNHistory = (JSONArray) history.get(TokenInfo.CHINESE);
                            CN_Token.setRegion(formatCN.get(TokenInfo.REGION).toString());
                            CN_Token.setUpdated(formatCN.get(TokenInfo.UPDATED).toString());
                            CN_Token.setLowPrice(formatCN.get(TokenInfo.LOW_PRICE).toString());
                            CN_Token.setCurrentPrice(formatCN.get(TokenInfo.CURRENT_PRICE).toString());
                            CN_Token.setHighPrice(formatCN.get(TokenInfo.HIGH_PRICE).toString());
                            CN_Token.setAPIResult(formatCN.get(TokenInfo.API_RESULT).toString());
                            CN_Token.clearHistory();
                            for (int i = 0; i < CNHistory.length(); i++) {
                                CN_Token.addToHistory(CNHistory.get(i));
                            }
                            /*
                            CN
                             */

                            /*
                            TW
                             */
                            JSONObject Tw = (JSONObject) update.get(TokenInfo.TAIWAN);
                            JSONObject formatTW = (JSONObject) Tw.get(TokenInfo.FORMATTED);
                            JSONArray TWHistory = (JSONArray) history.get(TokenInfo.TAIWAN);
                            TW_Token.setRegion(formatTW.get(TokenInfo.REGION).toString());
                            TW_Token.setUpdated(formatTW.get(TokenInfo.UPDATED).toString());
                            TW_Token.setLowPrice(formatTW.get(TokenInfo.LOW_PRICE).toString());
                            TW_Token.setCurrentPrice(formatTW.get(TokenInfo.CURRENT_PRICE).toString());
                            TW_Token.setHighPrice(formatTW.get(TokenInfo.HIGH_PRICE).toString());
                            TW_Token.setAPIResult(formatTW.get(TokenInfo.API_RESULT).toString());
                            TW_Token.clearHistory();
                            for (int i = 0; i < TWHistory.length(); i++) {
                                TW_Token.addToHistory(TWHistory.get(i));
                            }
                            /*
                            TW
                             */

                            /*
                            KR
                             */
                            JSONObject Kr = (JSONObject) update.get(TokenInfo.KOREAN);
                            JSONObject formatKR = (JSONObject) Kr.get(TokenInfo.FORMATTED);
                            JSONArray KRHistory = (JSONArray) history.get(TokenInfo.KOREAN);
                            KR_Token.setRegion(formatKR.get(TokenInfo.REGION).toString());
                            KR_Token.setUpdated(formatKR.get(TokenInfo.UPDATED).toString());
                            KR_Token.setLowPrice(formatKR.get(TokenInfo.LOW_PRICE).toString());
                            KR_Token.setCurrentPrice(formatKR.get(TokenInfo.CURRENT_PRICE).toString());
                            KR_Token.setHighPrice(formatKR.get(TokenInfo.HIGH_PRICE).toString());
                            KR_Token.setAPIResult(formatKR.get(TokenInfo.API_RESULT).toString());
                            KR_Token.clearHistory();
                            for (int i = 0; i < KRHistory.length(); i++) {
                                KR_Token.addToHistory(KRHistory.get(i));
                            }
                            /*
                            KR
                             */

                            Log.d(Constants.TAG, "Parsed Json data into tokens");
                            addTokenData();
                            updateFragment();
                            MainActivity.rotateFAB(false);
                            MainActivity.displaySnackbar("Updated data!", Snackbar.LENGTH_SHORT);
                        } catch (JSONException e) {
                            Log.e("tag", e.toString());
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("tag", "Error parsing Json data: " + error.toString());
                        MainActivity.displaySnackbar("Something went wrong loading the data! Oh no :(", Snackbar.LENGTH_INDEFINITE);
                    }
                });
        VolleySingleton.getInstance(c).addToRequestQueue(jsObjRequest);

    }

    private static void addTokenData() {
        tokens.clear();
        tokens.add(NA_Token);
        tokens.add(EU_Token);
        tokens.add(CN_Token);
        tokens.add(TW_Token);
        tokens.add(KR_Token);
        Log.d(Constants.TAG, "Added regional tokens to tokens (ArrayList)");
    }


}