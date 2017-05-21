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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;

import com.hmill.wowtoken.R;
import com.hmill.wowtoken.activities.MainActivity;
import com.hmill.wowtoken.util.Constants;

public class SettingsFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    RadioButton allianceRadio, hordeRadio;
    SharedPreferences prefs;

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
        prefs = PreferenceManager.getDefaultSharedPreferences(getContext());

        if(loadFaction()){
            allianceRadio.setChecked(true);
        }
        else{
            hordeRadio.setChecked(true);
        }

        allianceRadio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Store alliance in sharedpref
                prefs.edit().putBoolean(Constants.FACTION, true).apply();
                //updateToolbar();
                getActivity().recreate();
            }
        });

        hordeRadio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Store horde in sharedpref
                prefs.edit().putBoolean(Constants.FACTION, false).apply();
                //updateToolbar();
                getActivity().recreate();
            }
        });

        return v;
    }

    private boolean loadFaction(){
        return prefs.getBoolean(Constants.FACTION, false);
    }

    private void updateToolbar(){
        if(loadFaction()){
            SpannableString a = new SpannableString(Constants.SETTINGS_TOOLBAR_TITLE);
            a.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorAllianceTrim)), 0, Constants.SETTINGS_TOOLBAR_TITLE.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        //Else set toolbar text with horde color
        else{
            SpannableString h = new SpannableString(Constants.SETTINGS_TOOLBAR_TITLE);
            h.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorHordeTrim)), 0, Constants.SETTINGS_TOOLBAR_TITLE.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
    }

}
