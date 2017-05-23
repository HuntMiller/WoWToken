package com.hmill.wowtoken.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.hmill.wowtoken.R;
import com.hmill.wowtoken.adapters.RealmListAdapter;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

/**
 * Created by HMill on 5/23/2017.
 */

public class RealmPopup extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_realmpopup);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        TextView name = (TextView) findViewById(R.id.popup_realm_name);
        TextView type = (TextView) findViewById(R.id.popup_realm_type);
        TextView battlegroup = (TextView) findViewById(R.id.popup_realm_battlegroup);
        TextView status = (TextView) findViewById(R.id.popup_realm_status);
        TextView population = (TextView) findViewById(R.id.popup_realm_population);
        TextView queue = (TextView) findViewById(R.id.popup_realm_queue);
        TextView timezone = (TextView) findViewById(R.id.popup_realm_timezone);
        TextView locale = (TextView) findViewById(R.id.popup_realm_locale);
        ListView connectedrealms = (ListView) findViewById(R.id.popup_connected_realms);
        ArrayList al = new ArrayList();
        ArrayAdapter adapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, al);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int)(width*.6), (int)(height*.6));

        Intent i = getIntent();
        Bundle b = i.getBundleExtra("realminfo");
        name.setText(b.getString("name"));
        String slug = b.getString("slug");
        type.setText("(" + b.getString("type").toUpperCase() + ")");
        battlegroup.setText(b.getString("battlegroup"));
        String stat = (b.getBoolean("status")) ? "Online" : "Offline";
        status.setText(stat);
        population.setText(b.getString("population"));
        String q = (b.getBoolean("queue")) ? "Yes" : "No";
        queue.setText(q);
        timezone.setText(b.getString("timezone"));
        locale.setText(b.getString("locale"));
        String unsanitized = b.getString("connectedrealms");
        String noBracks = unsanitized.replaceAll("[\\[\\](){}]","");
        String readySplit = noBracks.replaceAll("\"", "");
        String[] split = readySplit.split(",");
        for(int j = 0; j < split.length; j++){
            //Add realm if it isn't itself
            if(!slug.toLowerCase().matches(split[j].toString().toLowerCase()))
                al.add(split[j].toString());
        }
        connectedrealms.setAdapter(adapter);

    }
}
