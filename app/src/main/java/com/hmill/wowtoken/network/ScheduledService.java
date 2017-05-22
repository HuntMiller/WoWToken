package com.hmill.wowtoken.network;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.hmill.wowtoken.util.TokenInfo;
import com.hmill.wowtoken.activities.MainActivity;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by HMill on 5/20/2017.
 */

public class ScheduledService extends Service {

    public static final String MY_SERVICE = ".ScheduledService";
    private Timer timer = new Timer();


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                    MainActivity.queueUrl(MainActivity.context, TokenInfo.URL_WITH_HISTORY);
                } catch (NullPointerException npe) {
                    Log.e("tag", npe.toString());
                }
            }
        }, 0, 5 * 60 * 1000);//5 Minutes
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        timer.purge();
        timer.cancel();
    }

}