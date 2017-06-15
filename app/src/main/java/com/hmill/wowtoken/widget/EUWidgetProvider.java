package com.hmill.wowtoken.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.widget.RemoteViews;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.hmill.wowtoken.R;
import com.hmill.wowtoken.network.VolleySingleton;
import com.hmill.wowtoken.util.TokenInfo;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by HMill on 5/20/2017.
 */

public class EUWidgetProvider extends AppWidgetProvider {

    private static final String REGION = "EU: ";
    private static final String FULL_REGION = TokenInfo.EUROPEAN;
    private static TokenInfo Token = new TokenInfo();
    private static RemoteViews remoteViews;

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        queueUrl(TokenInfo.URL_WITHOUT_HISTORY, context, appWidgetManager, appWidgetIds);
    }

    public static void updateViews(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds){
        final int count = appWidgetIds.length;
        for (int i = 0; i < count; i++) {
            int widgetId = appWidgetIds[i];

            remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
            String timeString = "";
            try{
                String[] split = Token.getUpdated().split(",");
                String[] secondSplit = split[1].split(" ");
                //               Time                    Timezone
                timeString += secondSplit[2] + " " + secondSplit[3] + " ";
                //              Date                Year
                //timeString += split[0] + " " + secondSplit[1];
            }catch(NullPointerException e){

            }

            remoteViews.setTextColor(R.id.timeView, Color.WHITE);
            remoteViews.setTextViewText(R.id.timeView, timeString);
            remoteViews.setTextColor(R.id.priceView, Color.WHITE);
            remoteViews.setTextViewText(R.id.priceView, REGION + Token.getCurrentPrice());

            Intent intent = new Intent(context, NAWidgetProvider.class);
            intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            remoteViews.setOnClickPendingIntent(R.id.actionButton, pendingIntent);
            appWidgetManager.updateAppWidget(widgetId, remoteViews);
        }
    }

    public static void queueUrl(String url, final Context context, final AppWidgetManager appWidgetManager, final int[] appWidgetIds) {

        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject nonFormatted = (JSONObject) response.get(FULL_REGION);
                            JSONObject formatted = (JSONObject) nonFormatted.get(TokenInfo.FORMATTED);
                            Token.setRegion(formatted.get(TokenInfo.REGION).toString());
                            Token.setUpdated(formatted.get(TokenInfo.UPDATED).toString());
                            Token.setLowPrice(formatted.get(TokenInfo.LOW_PRICE).toString());
                            Token.setCurrentPrice(formatted.get(TokenInfo.CURRENT_PRICE).toString());
                            Token.setHighPrice(formatted.get(TokenInfo.HIGH_PRICE).toString());
                            updateViews(context, appWidgetManager, appWidgetIds);
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
            VolleySingleton.getInstance(context).addToRequestQueue(jsObjRequest);
        } catch (NullPointerException npe) {

        }

    }

}