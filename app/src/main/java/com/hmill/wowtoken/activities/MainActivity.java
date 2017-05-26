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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
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

    private static Animation rotateAnimation;
    private static Handler mHandler;
    private static FragmentManager fragmentManager;
    private static DrawerLayout drawerLayout;
    private static CoordinatorLayout snackbarLayout;
    private NavigationView navigationView;
    private View headerView;
    private FrameLayout banner_container;
    private static FloatingActionButton fab;
    private TabHost host;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        //Set theme (has to be done before content view is set)
        if (getFaction()) {
            setTheme(R.style.AppThemeAllianceNoActionBar);
        } else {
            setTheme(R.style.AppThemeHordeNoActionBar);
        }
        setContentView(R.layout.activity_main);
        init();

    }

    @Override
    protected void onResume() {
        super.onResume();
        setBanner(getFaction());
        if (navigationIndex == Constants.INDEX_DATA) {
            //Parse URL for token data
            populateFragment();
        }
        //NavigationView setup
        setUpNavigationView();
        //Load fragment
        loadHomeFragment();
        //Toggle fab for screen rotations
        toggleFab();
        Log.d("tag", "Update Service Started");
        Intent i = new Intent(getApplicationContext(), ScheduledService.class);
        startService(i);
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
                rotateFAB(true);
                populateFragment();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (navigationIndex != Constants.INDEX_DATA) {
            navigationIndex = 0;
            CURRENT_TAG = Constants.TAG_DATA;
            loadHomeFragment();
        } else {
            finish();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.data) {

        } else if (id == R.id.realms) {

        } else if (id == R.id.faq) {

        } else if (id == R.id.settings) {

        }
        setUpNavigationView();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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

        fragmentManager = getSupportFragmentManager();
        setSupportActionBar(toolbar);
        mHandler = new Handler();
        navigationView.setNavigationItemSelectedListener(this);
        regionIndex = loadDefaultSelectedTab();
        rotateAnimation = AnimationUtils.loadAnimation(context, R.anim.rotate_ccw);

        host.setup();
        TabHost.TabSpec spec = host.newTabSpec(TokenInfo.NORTH_AMERICA);
        spec.setContent(R.id.NA_Tab);
        spec.setIndicator(TokenInfo.NORTH_AMERICA);
        host.addTab(spec);
        spec = host.newTabSpec(TokenInfo.EUROPEAN);
        spec.setContent(R.id.EU_Tab);
        spec.setIndicator(TokenInfo.EUROPEAN);
        host.addTab(spec);
        spec = host.newTabSpec(TokenInfo.CHINESE);
        spec.setContent(R.id.CN_Tab);
        spec.setIndicator(TokenInfo.CHINESE);
        host.addTab(spec);
        spec = host.newTabSpec(TokenInfo.TAIWAN);
        spec.setContent(R.id.TW_Tab);
        spec.setIndicator(TokenInfo.TAIWAN);
        host.addTab(spec);
        spec = host.newTabSpec(TokenInfo.KOREAN);
        spec.setContent(R.id.KR_Tab);
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
    }

    public static void rotateFAB(boolean startRotate){
        if(startRotate){
            rotateAnimation.setRepeatCount(Animation.INFINITE);
            fab.startAnimation(rotateAnimation);
        }
        else{
            rotateAnimation.setRepeatCount(0);
        }
    }

    private static int loadDefaultSelectedTab() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String defaultRegion = prefs.getString(Constants.DEFAULT_REGION, null);
        int ret = 0;
        if (defaultRegion.equals("North America")) {
            ret = TokenInfo.INDEX_NORTH_AMERICA;
        }
        if (defaultRegion.equals("Europe")) {
            ret = TokenInfo.INDEX_EUROPE;
        }
        if (defaultRegion.equals("China")) {
            ret = TokenInfo.INDEX_CHINA;
        }
        if (defaultRegion.equals("Taiwan")) {
            ret = TokenInfo.INDEX_TAIWAN;
        }
        if (defaultRegion.equals("Korea")) {
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

    /*
    Populate whatever fragment with whatever data it needs
     */
    public static void populateFragment() {
        Fragment frag = fragmentManager.findFragmentById(R.id.frame);
        if (frag instanceof DataFragment) {
            DataFragment dataFragment = (DataFragment) frag;
            dataFragment.queueUrl(context, TokenInfo.URL_WITH_HISTORY);
        }
        if (frag instanceof RealmsFragment) {
            RealmsFragment realmsFragment = (RealmsFragment) frag;
            realmsFragment.getRealmStatus();
        }

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