package com.hmill.wowtoken.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.Spinner;

import com.hmill.wowtoken.R;
import com.hmill.wowtoken.activities.MainActivity;
import com.hmill.wowtoken.util.Constants;
import com.hmill.wowtoken.util.Realm;
import com.hmill.wowtoken.util.TokenInfo;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SettingsFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String[] regionArray = new String[]{"North America", "Europe", "China", "Taiwan", "Korea"};

    private String mParam1;
    private String mParam2;

    private RadioButton allianceRadio, hordeRadio;
    private Spinner homeRealmSpinner, regionSpinner;
    private SharedPreferences prefs;
    private ArrayList realmList;
    boolean isAlliance;


    public SettingsFragment() {
        // Required empty public constructor
    }

    public static SettingsFragment newInstance(String param1, String param2) {
        SettingsFragment fragment = new SettingsFragment();
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
        View v = inflater.inflate(R.layout.fragment_settings, container, false);
        allianceRadio = (RadioButton) v.findViewById(R.id.alliance_radio);
        hordeRadio = (RadioButton) v.findViewById(R.id.horde_radio);
        homeRealmSpinner = (Spinner) v.findViewById(R.id.home_realm_spinner);
        regionSpinner = (Spinner) v.findViewById(R.id.region_spinner);
        prefs = PreferenceManager.getDefaultSharedPreferences(getContext());

        populateSpinners();
        setSpinnerSelections();

        isAlliance = prefs.getBoolean(Constants.FACTION, false);
        if (isAlliance) {
            allianceRadio.setChecked(true);
        } else {
            hordeRadio.setChecked(true);
        }


        setupListeners();

        return v;
    }

    private void populateSpinners() {
        realmList = new ArrayList();
        //populate spinner
        for (int i = 0; i < MainActivity.realmList.size(); i++) {
            Realm realm = MainActivity.realmList.get(i);
            realmList.add(realm.getName());
        }
        ArrayAdapter realmAdapter = new ArrayAdapter(getContext(), R.layout.spinner_item, realmList);
        realmAdapter.setDropDownViewResource(R.layout.spinner_item);
        homeRealmSpinner.setAdapter(realmAdapter);

        ArrayAdapter regionAdapter = new ArrayAdapter(getContext(), R.layout.spinner_item, regionArray);
        regionAdapter.setDropDownViewResource(R.layout.spinner_item);
        regionSpinner.setAdapter(regionAdapter);
    }

    private void setSpinnerSelections(){
        try {
            String homeServer = prefs.getString(Constants.HOME_SERVER, null);
            for (int i = 0; i < realmList.size(); i++) {
                //This is the index of your home server
                if (homeServer.equals(realmList.get(i).toString())) {
                    homeRealmSpinner.setSelection(i);
                }
            }

        } catch (NullPointerException e) {
            Log.e(Constants.TAG, "Home server was null: " + e.toString());
        }

        try {
            String defaultRegion = prefs.getString(Constants.DEFAULT_REGION, null);
            for (int i = 0; i < regionArray.length; i++) {
                //This is the index of your region
                if (defaultRegion.equals(regionArray[i].toString())) {
                    regionSpinner.setSelection(i);
                }
            }

        } catch (NullPointerException e) {
            Log.e(Constants.TAG, "Default region was null: " + e.toString());
        }
    }

    private void setupListeners() {
        allianceRadio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isAlliance){
                    //Store alliance in sharedpref
                    prefs.edit().putBoolean(Constants.FACTION, true).apply();
                    //updateToolbar();
                    getActivity().recreate();
                }

            }
        });

        hordeRadio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isAlliance){
                    //Store horde in sharedpref
                    prefs.edit().putBoolean(Constants.FACTION, false).apply();
                    //updateToolbar();
                    getActivity().recreate();
                }

            }
        });

        homeRealmSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selected = parent.getItemAtPosition(position).toString();
                prefs.edit().putString(Constants.HOME_SERVER, selected).commit();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        regionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selected = parent.getItemAtPosition(position).toString();
                prefs.edit().putString(Constants.DEFAULT_REGION, selected).commit();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

}
