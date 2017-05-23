package com.hmill.wowtoken.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.hmill.wowtoken.R;
import com.hmill.wowtoken.util.TokenInfo;
import com.hmill.wowtoken.network.VolleySingleton;
import com.hmill.wowtoken.activities.MainActivity;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by HMill on 5/20/2017.
 */

public class MyWidgetProvider extends AppWidgetProvider {

    private static final String ACTION_CLICK = "ACTION_CLICK";
    private static TokenInfo NA_Token = new TokenInfo();
    private static TokenInfo EU_Token = new TokenInfo();
    private static TokenInfo CN_Token = new TokenInfo();
    private static TokenInfo TW_Token = new TokenInfo();
    private static TokenInfo KR_Token = new TokenInfo();

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        // Get all ids
        ComponentName thisWidget = new ComponentName(context, MyWidgetProvider.class);
        int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
        for (int widgetId : allWidgetIds) {
            queueUrl(TokenInfo.URL_WITHOUT_HISTORY);

            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
            // Set the text
            remoteViews.setTextViewText(R.id.update_NA, NA_Token.getCurrentPrice());
            remoteViews.setTextViewText(R.id.update_EU, NA_Token.getCurrentPrice());
            remoteViews.setTextViewText(R.id.update_CN, NA_Token.getCurrentPrice());
            remoteViews.setTextViewText(R.id.update_TW, NA_Token.getCurrentPrice());
            remoteViews.setTextViewText(R.id.update_KR, NA_Token.getCurrentPrice());

            // Register an onClickListener
            Intent intent = new Intent(context, MyWidgetProvider.class);

            intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);

            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            remoteViews.setOnClickPendingIntent(R.id.update_NA, pendingIntent);
            appWidgetManager.updateAppWidget(widgetId, remoteViews);
        }
    }

    public static void queueUrl(String url) {

        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            /*
                            North American JSON Parsing
                             */
                            JSONObject NA = (JSONObject) response.get(TokenInfo.NORTH_AMERICA);
                            JSONObject formattedNA = (JSONObject) NA.get(TokenInfo.FORMATTED);
                            //Set Region to North America
                            NA_Token.setRegion(formattedNA.get(TokenInfo.REGION).toString());
                            //Set Update time NA
                            NA_Token.setUpdated(formattedNA.get(TokenInfo.UPDATED).toString());
                            //Set Low Price NA
                            NA_Token.setLowPrice(formattedNA.get(TokenInfo.LOW_PRICE).toString());
                            //Set Current Price NA
                            NA_Token.setCurrentPrice(formattedNA.get(TokenInfo.CURRENT_PRICE).toString());
                            //Set High Price NA
                            NA_Token.setHighPrice(formattedNA.get(TokenInfo.HIGH_PRICE).toString());


                            /*
                            European JSON parsing
                             */
                            JSONObject EU = (JSONObject) response.get(TokenInfo.EUROPEAN);
                            JSONObject formattedEuropean = (JSONObject) EU.get(TokenInfo.FORMATTED);
                            //Set Region to North America
                            EU_Token.setRegion(formattedEuropean.get(TokenInfo.REGION).toString());
                            //Set Update time NA
                            EU_Token.setUpdated(formattedEuropean.get(TokenInfo.UPDATED).toString());
                            //Set Low Price NA
                            EU_Token.setLowPrice(formattedEuropean.get(TokenInfo.LOW_PRICE).toString());
                            //Set Current Price NA
                            EU_Token.setCurrentPrice(formattedEuropean.get(TokenInfo.CURRENT_PRICE).toString());
                            //Set High Price NA
                            EU_Token.setHighPrice(formattedEuropean.get(TokenInfo.HIGH_PRICE).toString());


                            /*
                            Chinese JSON Parsing
                             */
                            JSONObject CN = (JSONObject) response.get(TokenInfo.CHINESE);
                            JSONObject formattedChinese = (JSONObject) CN.get(TokenInfo.FORMATTED);
                            //Set Region to China
                            CN_Token.setRegion(formattedChinese.get(TokenInfo.REGION).toString());
                            //Set Update time CN
                            CN_Token.setUpdated(formattedChinese.get(TokenInfo.UPDATED).toString());
                            //Set Low Price CN
                            CN_Token.setLowPrice(formattedChinese.get(TokenInfo.LOW_PRICE).toString());
                            //Set Current Price CN
                            CN_Token.setCurrentPrice(formattedChinese.get(TokenInfo.CURRENT_PRICE).toString());
                            //Set High Price CN
                            CN_Token.setHighPrice(formattedChinese.get(TokenInfo.HIGH_PRICE).toString());


                            /*
                            Taiwanese JSON Parsing
                             */
                            JSONObject TW = (JSONObject) response.get(TokenInfo.TAIWAN);
                            JSONObject formattedTaiwan = (JSONObject) TW.get(TokenInfo.FORMATTED);
                            //Set Region to Taiwan
                            TW_Token.setRegion(formattedTaiwan.get(TokenInfo.REGION).toString());
                            //Set Update time TW
                            TW_Token.setUpdated(formattedTaiwan.get(TokenInfo.UPDATED).toString());
                            //Set Low Price TW
                            TW_Token.setLowPrice(formattedTaiwan.get(TokenInfo.LOW_PRICE).toString());
                            //Set Current Price TW
                            TW_Token.setCurrentPrice(formattedTaiwan.get(TokenInfo.CURRENT_PRICE).toString());
                            //Set High Price TW
                            TW_Token.setHighPrice(formattedTaiwan.get(TokenInfo.HIGH_PRICE).toString());


                            /*
                            Korean JSON Parsing
                             */
                            JSONObject KR = (JSONObject) response.get(TokenInfo.KOREAN);
                            JSONObject formattedKorean = (JSONObject) KR.get(TokenInfo.FORMATTED);
                            //Set Region to Korean
                            KR_Token.setRegion(formattedKorean.get(TokenInfo.REGION).toString());
                            //Set Update time KR
                            KR_Token.setUpdated(formattedKorean.get(TokenInfo.UPDATED).toString());
                            //Set Low Price KR
                            KR_Token.setLowPrice(formattedKorean.get(TokenInfo.LOW_PRICE).toString());
                            //Set Current Price KR
                            KR_Token.setCurrentPrice(formattedKorean.get(TokenInfo.CURRENT_PRICE).toString());
                            //Set High Price KR
                            KR_Token.setHighPrice(formattedKorean.get(TokenInfo.HIGH_PRICE).toString());

                        } catch (JSONException e) {
                            Log.e("tag", e.toString());
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });
        try {
            VolleySingleton.getInstance(MainActivity.context).addToRequestQueue(jsObjRequest);
        } catch (NullPointerException npe) {

        }


    }

}
