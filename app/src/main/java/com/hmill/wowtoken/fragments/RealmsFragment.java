package com.hmill.wowtoken.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
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

import com.hmill.wowtoken.R;
import com.hmill.wowtoken.activities.MainActivity;
import com.hmill.wowtoken.adapters.RealmListAdapter;
import com.hmill.wowtoken.util.Constants;
import com.hmill.wowtoken.util.Realm;

import java.util.ArrayList;

public class RealmsFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    ListView homeRealmListView, realmListView;
    RealmListAdapter realmListAdapter, homeListAdapter;
    ArrayList<Realm> homeArrayList = new ArrayList<>();
    ArrayList<Realm> realmArrayList = new ArrayList<>();
    private SharedPreferences prefs;
    String homeServer;

    public RealmsFragment() {
        // Required empty public constructor
    }

    public static RealmsFragment newInstance(String param1, String param2) {
        RealmsFragment fragment = new RealmsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
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

        updateRealms();

        return v;
    }

    public void updateRealms(){

        realmArrayList.clear();
        for(Realm r : MainActivity.realmList){
            realmArrayList.add(r);
        }
        realmListAdapter = new RealmListAdapter(realmArrayList, getContext());
        realmListView.setAdapter(realmListAdapter);
        realmListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Realm realm = realmArrayList.get(position);
                MainActivity.openRealmPopup(realm);
            }
        });


        homeArrayList.clear();
        //Search through realms to find your home realm
        for(int i = 0; i < realmArrayList.size(); i++){
            //Add your home server(s) to home list
            if(homeServer.equals(realmArrayList.get(i).getName())){
                homeArrayList.add(realmArrayList.get(i));
            }
        }

        homeListAdapter = new RealmListAdapter(homeArrayList, getContext());
        homeRealmListView.setAdapter(homeListAdapter);
        homeRealmListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Realm realm = homeArrayList.get(position);
                MainActivity.openRealmPopup(realm);
            }
        });


    }

}
