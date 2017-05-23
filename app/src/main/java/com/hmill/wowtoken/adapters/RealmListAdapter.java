package com.hmill.wowtoken.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hmill.wowtoken.R;
import com.hmill.wowtoken.util.Realm;

import java.util.ArrayList;

/**
 * Created by HMill on 5/22/2017.
 */

public class RealmListAdapter extends ArrayAdapter<Realm> implements View.OnClickListener{

    private ArrayList<Realm> realmArrayList;
    Context mContext;

    // View lookup cache
    private static class ViewHolder {
        ImageView statusImageView;
        TextView realmTextView;
    }

    public RealmListAdapter(ArrayList<Realm> data, Context context) {
        super(context, R.layout.realm_listview_row_item, data);
        this.realmArrayList = data;
        this.mContext=context;

    }

    @Override
    public void onClick(View v) {

        int position=(Integer) v.getTag();
        Object object= getItem(position);
        Realm realm=(Realm)object;

        switch (v.getId()) {

        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Realm realm = getItem(position);
        ViewHolder viewHolder;
        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.realm_listview_row_item, parent, false);
            viewHolder.statusImageView = (ImageView) convertView.findViewById(R.id.realm_status_image_view);
            viewHolder.realmTextView = (TextView) convertView.findViewById(R.id.realm_name_text_view);

            result=convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }

        if(realm.getStatus())
            viewHolder.statusImageView.setBackgroundResource(android.R.drawable.btn_star_big_on);
        else
            viewHolder.statusImageView.setBackgroundResource(android.R.drawable.btn_star_big_off);
        viewHolder.realmTextView.setText(realm.getName());
        // Return the completed view to render on screen
        return convertView;
    }
}