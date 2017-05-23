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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.hmill.wowtoken.R;
import com.hmill.wowtoken.activities.MainActivity;
import com.hmill.wowtoken.adapters.RealmListAdapter;
import com.hmill.wowtoken.util.Constants;

public class RealmsFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    ImageView homeStatus;
    TextView homeRealm;
    ListView realmListView;
    RealmListAdapter adapter;
    private SharedPreferences prefs;

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
        realmListView = (ListView) v.findViewById(R.id.realm_list_view);
        homeStatus = (ImageView) v.findViewById(R.id.home_realm_status_image_view);
        homeRealm = (TextView) v.findViewById(R.id.home_realm_name_text_view);

        prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        String homeServer = prefs.getString(Constants.HOME_SERVER, null);
        boolean isOnline = false;
        for(int i = 0; i < MainActivity.realmList.size(); i++){
            if(homeServer.equals(MainActivity.realmList.get(i).getName())){
                isOnline = MainActivity.realmList.get(i).getStatus();
                Log.e(Constants.TAG, MainActivity.realmList.get(i).getName());
            }
        }
        Log.e(Constants.TAG, String.valueOf(isOnline));
        if(isOnline)
            homeStatus.setBackgroundResource(android.R.drawable.btn_star_big_on);
        else
            homeStatus.setBackgroundResource(android.R.drawable.btn_star_big_off);
        homeRealm.setText(homeServer);
        adapter = new RealmListAdapter(MainActivity.realmList, getContext());
        realmListView.setAdapter(adapter);

        return v;
    }

}
