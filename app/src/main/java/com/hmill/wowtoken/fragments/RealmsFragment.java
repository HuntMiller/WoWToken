package com.hmill.wowtoken.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.hmill.wowtoken.R;
import com.hmill.wowtoken.activities.MainActivity;
import com.hmill.wowtoken.activities.RealmPopup;
import com.hmill.wowtoken.adapters.RealmListAdapter;
import com.hmill.wowtoken.network.VolleySingleton;
import com.hmill.wowtoken.util.Constants;
import com.hmill.wowtoken.util.Realm;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.hmill.wowtoken.util.Constants.API_KEY;

public class RealmsFragment extends Fragment {

    private String mParam1;
    private String mParam2;

    static ListView homeRealmListView, realmListView;
    static RealmListAdapter realmListAdapter, homeListAdapter;
    public static ArrayList<Realm> loadedRealmList = new ArrayList<>();
    static ArrayList<Realm> homeArrayList = new ArrayList<>();
    static ArrayList<Realm> realmArrayList = new ArrayList<>();
    private static SharedPreferences prefs;
    static String homeServer;

    public RealmsFragment() {
        // Required empty public constructor
    }

    public static RealmsFragment newInstance() {
        RealmsFragment fragment = new RealmsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_realms, container, false);
        homeRealmListView = (ListView) v.findViewById(R.id.home_realm_list_view);
        realmListView = (ListView) v.findViewById(R.id.realm_list_view);

        prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        homeServer = prefs.getString(Constants.HOME_SERVER, null);

        getRealmStatus();

        return v;
    }

    public static void getRealmStatus() {
        final String ALL_EN_US_REALMS = "https://us.api.battle.net/wow/realm/status?locale=en_US&apikey=" + Constants.API_KEY;
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, ALL_EN_US_REALMS, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            MainActivity.rotateFAB(true);
                            loadedRealmList.clear();
                            JSONArray realms = (JSONArray) response.get("realms");
                            for (int i = 0; i < realms.length(); i++) {
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
                                loadedRealmList.add(realm);
                            }

                        } catch (JSONException e) {

                        }
                        updateRealms();
                        MainActivity.rotateFAB(false);
                        MainActivity.displaySnackbar("Updated data!", Snackbar.LENGTH_SHORT);
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(Constants.TAG, error.toString());

                    }
                });
        VolleySingleton.getInstance(MainActivity.context).addToRequestQueue(jsObjRequest);
    }

    public static void updateRealms() {

        realmArrayList.clear();
        for (Realm r : loadedRealmList) {
            realmArrayList.add(r);
        }
        realmListAdapter = new RealmListAdapter(realmArrayList, MainActivity.context);
        realmListView.setAdapter(realmListAdapter);
        realmListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Realm realm = realmArrayList.get(position);
                openRealmPopup(realm);
            }
        });


        homeArrayList.clear();
        //Search through realms to find your home realm
        for (int i = 0; i < realmArrayList.size(); i++) {
            //Add your home server(s) to home list
            if (homeServer.equals(realmArrayList.get(i).getName())) {
                homeArrayList.add(realmArrayList.get(i));
            }
        }

        homeListAdapter = new RealmListAdapter(homeArrayList, MainActivity.context);
        homeRealmListView.setAdapter(homeListAdapter);
        homeRealmListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Realm realm = homeArrayList.get(position);
                openRealmPopup(realm);
            }
        });

    }

    private static void openRealmPopup(Realm realm) {
        Intent i = new Intent(MainActivity.context, RealmPopup.class);
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
        MainActivity.context.startActivity(i);
    }

}
