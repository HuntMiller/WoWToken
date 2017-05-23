package com.hmill.wowtoken.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TabHost;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.hmill.wowtoken.fragments.RealmsFragment;
import com.hmill.wowtoken.fragments.SettingsFragment;
import com.hmill.wowtoken.util.Constants;
import com.hmill.wowtoken.fragments.DataFragment;
import com.hmill.wowtoken.fragments.FAQFragment;
import com.hmill.wowtoken.R;
import com.hmill.wowtoken.network.ScheduledService;
import com.hmill.wowtoken.util.Realm;
import com.hmill.wowtoken.util.TokenInfo;
import com.hmill.wowtoken.network.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    public static String CURRENT_TAG = Constants.TAG_DATA;
    public static int navigationIndex = Constants.INDEX_DATA;
    public static int regionIndex;
    public static Context context;
    public static ArrayList tokens = new ArrayList();
    public static TokenInfo NA_Token = new TokenInfo();
    public static TokenInfo EU_Token = new TokenInfo();
    public static TokenInfo CN_Token = new TokenInfo();
    public static TokenInfo TW_Token = new TokenInfo();
    public static TokenInfo KR_Token = new TokenInfo();
    public static ArrayList<Realm> realmList = new ArrayList<>();

    private static Handler mHandler;
    private static FragmentManager fragmentManager;

    private static DrawerLayout drawerLayout;
    private static CoordinatorLayout snackbarLayout;
    private NavigationView navigationView;
    private View headerView;
    private FrameLayout banner_container;
    private FloatingActionButton fab;
    private TabHost host;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Set theme
        context = this;
        if (getFaction()) {
            setTheme(R.style.AppThemeAllianceNoActionBar);
        } else {
            setTheme(R.style.AppThemeHordeNoActionBar);
        }
        regionIndex = loadDefaultSelectedTab();
        setContentView(R.layout.activity_main);
        init();
        Log.d("tag", "Update Service Started");
        Intent i = new Intent(getApplicationContext(), ScheduledService.class);
        startService(i);
    }

    private void init() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        snackbarLayout = (CoordinatorLayout) findViewById(R.id.snackbar_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        headerView = navigationView.getHeaderView(0);
        banner_container = (FrameLayout) headerView.findViewById(R.id.banner_container);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        host = (TabHost) findViewById(R.id.tabhost);
        host.setup();

        TabHost.TabSpec spec = host.newTabSpec(TokenInfo.NORTH_AMERICA);
        spec.setContent(R.id.tab1);
        spec.setIndicator(TokenInfo.NORTH_AMERICA);
        host.addTab(spec);
        spec = host.newTabSpec(TokenInfo.EUROPEAN);
        spec.setContent(R.id.tab2);
        spec.setIndicator(TokenInfo.EUROPEAN);
        host.addTab(spec);
        spec = host.newTabSpec(TokenInfo.CHINESE);
        spec.setContent(R.id.tab3);
        spec.setIndicator(TokenInfo.CHINESE);
        host.addTab(spec);
        spec = host.newTabSpec(TokenInfo.TAIWAN);
        spec.setContent(R.id.tab4);
        spec.setIndicator(TokenInfo.TAIWAN);
        host.addTab(spec);
        spec = host.newTabSpec(TokenInfo.KOREAN);
        spec.setContent(R.id.tab5);
        spec.setIndicator(TokenInfo.KOREAN);
        host.addTab(spec);
        host.setCurrentTab(regionIndex);

        host.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                setTabColor(host);
                DataFragment df = (DataFragment) fragmentManager.findFragmentById(R.id.frame);

                if (tabId.matches(TokenInfo.NORTH_AMERICA))
                    regionIndex = TokenInfo.INDEX_NORTH_AMERICA;
                if (tabId.matches(TokenInfo.EUROPEAN))
                    regionIndex = TokenInfo.INDEX_EUROPE;

                if (tabId.matches(TokenInfo.CHINESE))
                    regionIndex = TokenInfo.INDEX_CHINA;

                if (tabId.matches(TokenInfo.TAIWAN))
                    regionIndex = TokenInfo.INDEX_TAIWAN;

                if (tabId.matches(TokenInfo.KOREAN))
                    regionIndex = TokenInfo.INDEX_KOREA;

                df.updateFragment();
            }
        });
        setTabColor(host);

        fragmentManager = getSupportFragmentManager();
        setSupportActionBar(toolbar);
        mHandler = new Handler();
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setBanner(getFaction());
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
                break;
        }
    }

    @Override
    public void onBackPressed(){
        if(navigationIndex != Constants.INDEX_DATA){
            navigationIndex = 0;
            CURRENT_TAG = Constants.TAG_DATA;
            loadHomeFragment();
        }
        else{
            finish();
        }
    }

    private static int loadDefaultSelectedTab() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String defaultRegion = prefs.getString(Constants.DEFAULT_REGION, null);
        int ret = 0;
        if(defaultRegion.equals("North America")){
            ret = TokenInfo.INDEX_NORTH_AMERICA;
        }
        if(defaultRegion.equals("Europe")){
            ret = TokenInfo.INDEX_EUROPE;
        }
        if(defaultRegion.equals("China")){
            ret = TokenInfo.INDEX_CHINA;
        }
        if(defaultRegion.equals("Taiwan")){
            ret = TokenInfo.INDEX_TAIWAN;
        }
        if(defaultRegion.equals("Korea")){
            ret = TokenInfo.INDEX_KOREA;
        }
        return ret;
    }

    private static void setTabColor(TabHost host) {
        //Not selected tabs
        for (int i = 0; i < host.getTabWidget().getChildCount(); i++) {
            if (getFaction()) {
                host.getTabWidget().getChildAt(i).setBackgroundColor(context.getResources().getColor(R.color.colorAllianceShadow));
                TextView tv = (TextView) host.getTabWidget().getChildAt(i).findViewById(android.R.id.title);
                tv.setTextColor(context.getResources().getColor(R.color.colorAllianceTrim));
            } else {
                host.getTabWidget().getChildAt(i).setBackgroundColor(context.getResources().getColor(R.color.colorHordeShadow));
                TextView tv = (TextView) host.getTabWidget().getChildAt(i).findViewById(android.R.id.title);
                tv.setTextColor(context.getResources().getColor(R.color.colorHordeTrim));
            }

        }
        //Selected tab
        if (getFaction()) {
            host.getTabWidget().setCurrentTab(0);
            host.getTabWidget().getChildAt(host.getCurrentTab()).setBackgroundResource(R.drawable.alliance_gradient);
            TextView tv = (TextView) host.getCurrentTabView().findViewById(android.R.id.title);
            tv.setTextColor(context.getResources().getColor(R.color.colorAllianceShadow));
        } else {
            host.getTabWidget().setCurrentTab(0);
            host.getTabWidget().getChildAt(host.getCurrentTab()).setBackgroundResource(R.drawable.horde_gradient);
            TextView tv = (TextView) host.getCurrentTabView().findViewById(android.R.id.title);
            tv.setTextColor(context.getResources().getColor(R.color.colorHordeShadow));
        }

    }

    private void setBanner(Boolean faction) {
        //True = Alliance
        if (faction) {
            banner_container.setBackgroundResource(R.drawable.alliancebanner);

        }
        //False = Horde
        else {
            banner_container.setBackgroundResource(R.drawable.hordebanner);
        }
    }

    private static boolean getFaction() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getBoolean(Constants.FACTION, true);
    }

    public static void populateDataList() {
        queueUrl(context, TokenInfo.URL_WITH_HISTORY);
        getRealmStatus();
    }

    public static void queueUrl(final Context c, String url) {
        Log.d(Constants.TAG, "called queueUrl()");
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        try {
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
                            try {
                                Fragment f = fragmentManager.findFragmentById(R.id.frame);
                                if (f instanceof DataFragment) {
                                    DataFragment df = (DataFragment) fragmentManager.findFragmentById(R.id.frame);
                                    df.updateFragment();
                                }
                            } catch (NullPointerException e) {

                            }

                            displaySnackbar("Updated data!", Snackbar.LENGTH_SHORT);
                        } catch (JSONException e) {
                            Log.e("tag", e.toString());
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("tag", "Error parsing Json data: " + error.toString());
                        displaySnackbar("Something went wrong loading the data! Oh no :(", Snackbar.LENGTH_INDEFINITE);
                    }
                });
        VolleySingleton.getInstance(c).addToRequestQueue(jsObjRequest);

    }

    public static void getRealmStatus(){
        final String ALL_EN_US_REALMS = "https://us.api.battle.net/wow/realm/status?locale=en_US&apikey=g42yjbzr44um5djjhs2nswdzj2jmkqmx";
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, ALL_EN_US_REALMS, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try{
                            realmList.clear();
                            JSONArray realms = (JSONArray) response.get("realms");
                            for(int i = 0; i < realms.length(); i++){
                                JSONObject o = (JSONObject) realms.get(i);
                                Realm realm = new Realm();
                                realm.setBattlegroup(o.get(Realm.BATTLEGROUP).toString());
                                realm.setConnectedRealms(o.get(Realm.CONNECTED_REALMS).toString());
                                realm.setLocale(o.get(Realm.LOCALE).toString());
                                realm.setName(o.get(Realm.NAME).toString());
                                realm.setPopulation(o.get(Realm.POPULATION).toString());
                                realm.setQueue(Boolean.parseBoolean(o.get(Realm.QUEUE).toString()));
                                realm.setSlug(o.get(Realm.SLUG).toString());
                                realm.setTimezone(o.get(Realm.TIMEZONE).toString());
                                realm.setStatus(Boolean.parseBoolean(o.get(Realm.STATUS).toString()));
                                realm.setType(o.get(Realm.TYPE).toString());
                                realmList.add(realm);
                            }

                        }catch(JSONException e){

                        }
                        try {
                            Fragment f = fragmentManager.findFragmentById(R.id.frame);
                            if (f instanceof RealmsFragment) {
                                RealmsFragment rf = (RealmsFragment) fragmentManager.findFragmentById(R.id.frame);
                                rf.updateRealms();
                            }
                        } catch (NullPointerException e) {

                        }

                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(Constants.TAG, error.toString());

                    }
                });
        VolleySingleton.getInstance(context).addToRequestQueue(jsObjRequest);
    }

    public static void displaySnackbar(String input, int length) {
        Snackbar snackbar = Snackbar.make(snackbarLayout, input, length);
        View snackbarView = snackbar.getView();
        if (getFaction()) {
            snackbarView.setBackgroundColor(context.getResources().getColor(R.color.colorAlliance));
        } else {
            snackbarView.setBackgroundColor(context.getResources().getColor(R.color.colorHorde));
        }
        TextView tv = (TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text);
        tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        tv.setGravity(Gravity.CENTER_HORIZONTAL);
        snackbar.show();
    }

    public static void addTokenData() {
        tokens.clear();
        tokens.add(NA_Token);
        tokens.add(EU_Token);
        tokens.add(CN_Token);
        tokens.add(TW_Token);
        tokens.add(KR_Token);
        Log.d(Constants.TAG, "Added regional tokens to tokens (ArrayList)");
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
                    case R.id.realms:
                        navigationIndex = Constants.INDEX_REALMS;
                        CURRENT_TAG = Constants.TAG_REALMS;
                        break;
                    case R.id.faq:
                        navigationIndex = Constants.INDEX_FAQ;
                        CURRENT_TAG = Constants.TAG_FAQ;
                        break;
                    case R.id.settings:
                        navigationIndex = Constants.INDEX_SETTINGS;
                        CURRENT_TAG = Constants.TAG_SETTINGS;
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

    public void setToolbarTitle() {
        String title = "error";
        switch (navigationIndex) {

            case Constants.INDEX_DATA:
                title = Constants.DATA_TOOLBAR_TITLE;
                break;
            case Constants.INDEX_REALMS:
                title = Constants.REALMS_TOOLBAR_TITLE;
                break;
            case Constants.INDEX_FAQ:
                title = Constants.FAQ_TOOLBAR_TITLE;
                break;
            case Constants.INDEX_SETTINGS:
                title = Constants.SETTINGS_TOOLBAR_TITLE;
                break;
        }
        if (!title.equals("error")) {
            //If alliance setting is chosen, set toolbar text with alliance color
            if (getFaction()) {
                SpannableString a = new SpannableString(title);
                a.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorAllianceTrim)), 0, title.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                getSupportActionBar().setTitle(a);
                getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.color.colorAlliance));
            }
            //Else set toolbar text with horde color
            else {
                SpannableString h = new SpannableString(title);
                h.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorHordeTrim)), 0, title.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                getSupportActionBar().setTitle(h);
                getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.color.colorHorde));
            }

        }

    }

    public static void openRealmPopup(Realm realm){
        Intent i = new Intent(context, RealmPopup.class);
        Bundle b = new Bundle();
        b.putString("name", realm.getName());
        b.putString("type", realm.getType());
        b.putString("battlegroup", realm.getBattlegroup());
        b.putBoolean("status", realm.getStatus());
        b.putString("population", realm.getPopulation());
        b.putBoolean("queue", realm.getQueue());
        b.putString("timezone", realm.getTimezone());
        b.putString("locale", realm.getLocale());
        b.putString("connectedrealms", realm.getConnectedRealms());
        b.putString("slug", realm.getSlug());
        i.putExtra("realminfo", b);
        context.startActivity(i);
    }

    public void loadHomeFragment() {

        // selecting appropriate nav menu item
        selectNavMenu();

        // set toolbar title
        setToolbarTitle();

        // if user select the current navigation menu again, don't do anything
        // just close the navigation drawer
        if (fragmentManager.findFragmentByTag(CURRENT_TAG) != null) {
            drawerLayout.closeDrawers();
            return;
        }

        //Fade animation for fragment transitions
        Runnable mPendingRunnable = new Runnable() {
            @Override
            public void run() {
                // update the main content by replacing fragments
                Fragment fragment = getHomeFragment();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
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
            case Constants.INDEX_REALMS:
                RealmsFragment realmsFragment = new RealmsFragment();
                return realmsFragment;
            case Constants.INDEX_FAQ:
                FAQFragment FAQFragment = new FAQFragment();
                return FAQFragment;
            case Constants.INDEX_SETTINGS:
                SettingsFragment settingsFragment = new SettingsFragment();
                return settingsFragment;
            default:
                return new DataFragment();
        }
    }

    private void toggleFab() {
        Log.d(Constants.TAG, "Toggled FAB");
        if (getFaction()) {
            fab.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorAlliance)));
            fab.setImageResource(R.drawable.alliancerefresh);
            host.setBackgroundColor(getResources().getColor(R.color.colorAllianceShadow));
        } else {
            fab.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorHorde)));
            fab.setImageResource(R.drawable.horderefresh);
            host.setBackgroundColor(getResources().getColor(R.color.colorHordeShadow));
        }
        //FAB
        if (navigationIndex == Constants.INDEX_DATA || navigationIndex == Constants.INDEX_REALMS) {
            fab.show();
        } else {
            fab.hide();
        }
        //Tabs
        if (navigationIndex == Constants.INDEX_DATA) {
            host.setVisibility(View.VISIBLE);
        } else {
            host.setVisibility(View.GONE);
        }
    }

}