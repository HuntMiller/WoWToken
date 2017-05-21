package com.hmill.wowtoken.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.hmill.wowtoken.fragments.BlankFragment;
import com.hmill.wowtoken.util.Constants;
import com.hmill.wowtoken.fragments.DataFragment;
import com.hmill.wowtoken.fragments.FAQFragment;
import com.hmill.wowtoken.R;
import com.hmill.wowtoken.network.ScheduledService;
import com.hmill.wowtoken.util.TokenInfo;
import com.hmill.wowtoken.network.VolleySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    Toolbar toolbar;
    static DrawerLayout drawerLayout;
    NavigationView navigationView;
    FloatingActionButton fab;

    public static String CURRENT_TAG = Constants.TAG_DATA;
    public static int navigationIndex = Constants.INDEX_DATA;
    public static Context context;
    public static ArrayList tokens = new ArrayList();

    public static TokenInfo NA_Token = new TokenInfo();
    private static TokenInfo EU_Token = new TokenInfo();
    private static TokenInfo CN_Token = new TokenInfo();
    private static TokenInfo TW_Token = new TokenInfo();
    private static TokenInfo KR_Token = new TokenInfo();
    private static Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        fab = (FloatingActionButton) findViewById(R.id.fab);


        setSupportActionBar(toolbar);
        mHandler = new Handler();
        context = getApplicationContext();
        navigationView.setNavigationItemSelectedListener(this);


        Log.d("tag", "Update Service Started");
        Intent i = new Intent(getApplicationContext(), ScheduledService.class);
        startService(i);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (navigationIndex == Constants.INDEX_DATA) {
            //Parse URL for token data
            populateDataList();
        }
        //NavigationView setup
        setUpNavigationView();
        //Load fragment
        loadHomeFragment();
        //Toggle fab for screen rotations
        toggleFab();
    }

    @Override
    public void onStop() {
        super.onStop();
        //Stop Update Service
        Intent i = new Intent(context, ScheduledService.class);
        stopService(i);
    }

    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.fab:
                populateDataList();
                //Disable FAB for 1 minute so user can't spam endpoint
                fab.hide();
                Timer buttonTimer = new Timer();
                buttonTimer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                fab.show();
                            }
                        });
                    }
                }, 60000);
                break;
        }
    }

    private void populateDataList() {
        queueUrl(getApplicationContext(), TokenInfo.URL_WITHOUT_HISTORY);
        swapBlankFrag();
    }

    private void swapBlankFrag() {
        getSupportFragmentManager().beginTransaction().replace(R.id.frame, new BlankFragment()).commit();
        getSupportFragmentManager().beginTransaction().replace(R.id.frame, new DataFragment()).commit();
    }

    public static void queueUrl(Context c, String url) {
        Log.d(Constants.TAG, "called queueUrl()");

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

                            Log.d(Constants.TAG, "Parsed Json data into tokens");

                            addTokenData();

                            Snackbar snackbar = Snackbar.make(drawerLayout, "Updated Data!", Snackbar.LENGTH_SHORT);
                            snackbar.show();

                        } catch (JSONException e) {
                            Log.e("tag", e.toString());
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("tag", "Error parsing Json data: " + error.toString());
                        Snackbar snackbar = Snackbar.make(drawerLayout, "Something went wrong loading the data! Oh no :(", Snackbar.LENGTH_INDEFINITE);
                        snackbar.show();
                    }
                });
        VolleySingleton.getInstance(c).addToRequestQueue(jsObjRequest);

    }

    public static void addTokenData() {
        Log.d(Constants.TAG, "Adding tokens to tokenArrayList");
        tokens.clear();
        tokens.add(NA_Token);
        tokens.add(EU_Token);
        tokens.add(CN_Token);
        tokens.add(TW_Token);
        tokens.add(KR_Token);
    }

    private void selectNavMenu() {
        navigationView.getMenu().getItem(navigationIndex).setChecked(true);
    }

    public void setUpNavigationView() {
        //Setting Navigation View Item Selected Listener to handle the item click of the navigation menu
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            // This method will trigger on item Click of navigation menu
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                //Check to see which item was being clicked and perform appropriate action
                switch (menuItem.getItemId()) {
                    case R.id.data:
                        navigationIndex = Constants.INDEX_DATA;
                        CURRENT_TAG = Constants.TAG_DATA;
                        break;
                    case R.id.faq:
                        navigationIndex = Constants.INDEX_FAQ;
                        CURRENT_TAG = Constants.TAG_FAQ;
                        break;
                    default:
                        navigationIndex = Constants.INDEX_DATA;
                        CURRENT_TAG = Constants.TAG_DATA;
                        break;
                }

                //Check if the item is in checked state or not, if not make it checked
                if (menuItem.isChecked()) {
                    menuItem.setChecked(false);
                } else {
                    menuItem.setChecked(true);
                }
                menuItem.setChecked(true);

                loadHomeFragment();

                return true;
            }
        });


        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {

            @Override
            public void onDrawerClosed(View drawerView) {
                // Code here will be triggered once the drawer closes. obvious
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                // Code here will be triggered once the drawer open. also obvious
                super.onDrawerOpened(drawerView);
            }
        };

        //Setting the actionbarToggle to drawer layout
        drawerLayout.setDrawerListener(actionBarDrawerToggle);

        //calling sync state is necessary or else your hamburger icon wont show up
        actionBarDrawerToggle.syncState();
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.data) {

        } else if (id == R.id.faq) {

        }
        setUpNavigationView();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void setToolbarTitle() {
        String title = "error";
        switch (navigationIndex) {

            case Constants.INDEX_DATA:
                title = Constants.DATA_TOOLBAR_TITLE;
                break;
            case Constants.INDEX_FAQ:
                title = Constants.FAQ_TOOLBAR_TITLE;
                break;
        }
        if (!title.equals("error"))
            getSupportActionBar().setTitle(title);
    }

    public void loadHomeFragment() {

        // selecting appropriate nav menu item
        selectNavMenu();

        // set toolbar title
        setToolbarTitle();

        // if user select the current navigation menu again, don't do anything
        // just close the navigation drawer
        if (getSupportFragmentManager().findFragmentByTag(CURRENT_TAG) != null) {
            drawerLayout.closeDrawers();
            return;
        }

        //Fade animation for fragment transitions
        Runnable mPendingRunnable = new Runnable() {
            @Override
            public void run() {
                // update the main content by replacing fragments
                Fragment fragment = getHomeFragment();
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                        android.R.anim.fade_out);
                fragmentTransaction.replace(R.id.frame, fragment, CURRENT_TAG);
                fragmentTransaction.commit();

            }
        };

        // If mPendingRunnable is not null, then add to the message queue
        if (mPendingRunnable != null) {
            mHandler.post(mPendingRunnable);
        }

        toggleFab();

        //Closing drawer on item click
        drawerLayout.closeDrawers();

        // refresh toolbar menu
        invalidateOptionsMenu();

    }

    private Fragment getHomeFragment() {
        switch (navigationIndex) {
            case Constants.INDEX_DATA:
                DataFragment dataFragment = new DataFragment();
                return dataFragment;
            case Constants.INDEX_FAQ:
                FAQFragment FAQFragment = new FAQFragment();
                return FAQFragment;
            default:
                return new DataFragment();
        }
    }

    private void toggleFab() {
        Log.d(Constants.TAG, "Toggled FAB");
        if (navigationIndex == Constants.INDEX_DATA)
            fab.show();
        else
            fab.hide();
    }

}
