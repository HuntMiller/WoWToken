/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hmill.wowtoken.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.hmill.wowtoken.R;
import com.hmill.wowtoken.util.TokenInfo;

import java.util.ArrayList;

public class CustomAdapter extends ArrayAdapter<TokenInfo> implements View.OnClickListener {

    private ArrayList<TokenInfo> tokenArrayList;
    Context mContext;

    // View lookup
    private static class ViewHolder {
        LinearLayout container;
        TextView region;
        TextView updated;
        TextView lowPrice;
        TextView currentPrice;
        TextView highPrice;
        SeekBar seekBar;
    }

    public CustomAdapter(ArrayList<TokenInfo> tokens, Context context) {
        super(context, R.layout.row_item, tokens);
        this.tokenArrayList = tokens;
        this.mContext = context;
    }

    @Override
    public void onClick(View v) {
        int position = (Integer) v.getTag();
        Object object = getItem(position);
        TokenInfo tokenInfo = (TokenInfo) object;

        switch (v.getId()) {

        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        TokenInfo tokenInfo = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.row_item, parent, false);
            viewHolder.container = (LinearLayout) convertView.findViewById(R.id.container);
            viewHolder.region = (TextView) convertView.findViewById(R.id.region);
            viewHolder.updated = (TextView) convertView.findViewById(R.id.updated);
            viewHolder.lowPrice = (TextView) convertView.findViewById(R.id.lowPrice);
            viewHolder.currentPrice = (TextView) convertView.findViewById(R.id.currentPrice);
            viewHolder.highPrice = (TextView) convertView.findViewById(R.id.highPrice);
            viewHolder.seekBar = (SeekBar) convertView.findViewById(R.id.seekBar);

            result = convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result = convertView;
        }

        String region = tokenInfo.getRegion();
        String updated = tokenInfo.getUpdated();
        String lowPrice = tokenInfo.getLowPrice();
        String currentPrice = tokenInfo.getCurrentPrice();
        String highPrice = tokenInfo.getHighPrice();

        viewHolder.region.setText(region);
        viewHolder.updated.setText(updated);
        viewHolder.lowPrice.setText(lowPrice);
        viewHolder.currentPrice.setText(currentPrice);
        viewHolder.highPrice.setText(highPrice);
        //Calculate % of seekbar
        String sanitizedCurrent = viewHolder.currentPrice.getText().toString().replaceAll("[^0-9.]", "");
        String sanitizedLow = viewHolder.lowPrice.getText().toString().replaceAll("[^0-9.]", "");
        String sanitizedHigh = viewHolder.highPrice.getText().toString().replaceAll("[^0-9.]", "");
        int seekPerc = calculateSeekbarPercentage(Integer.valueOf(sanitizedCurrent), Integer.valueOf(sanitizedLow), Integer.valueOf(sanitizedHigh));
        //Set seekbar
        viewHolder.seekBar.setProgress(seekPerc);
        viewHolder.seekBar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        // Return the completed view to render on screen
        return convertView;
    }

    /*
    Calculate percentage of X in between A and B
     */
    private int calculateSeekbarPercentage(int x, int a, int b) {
        double xx = (double) x;
        double aa = (double) a;
        double bb = (double) b;
        double percent = ((xx - aa) / (bb - aa)) * 100.0;
        return (int) percent;
    }

}