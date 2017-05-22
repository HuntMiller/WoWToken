package com.hmill.wowtoken.util;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by HMill on 5/19/2017.
 */

public class TokenInfo {

    public static final String URL_WITHOUT_HISTORY = "https://data.wowtoken.info/snapshot.json";
    public static final String URL_WITH_HISTORY = "https://data.wowtoken.info/wowtoken.json";
    public static final String NORTH_AMERICA = "NA";
    public static final String EUROPEAN = "EU";
    public static final String CHINESE = "CN";
    public static final String TAIWAN = "TW";
    public static final String KOREAN = "KR";

    public static final int INDEX_NORTH_AMERICA = 0;
    public static final int INDEX_EUROPE = 1;
    public static final int INDEX_CHINA = 2;
    public static final int INDEX_TAIWAN = 3;
    public static final int INDEX_KOREA = 4;

    public static final String RAW = "raw";
    public static final String FORMATTED = "formatted";
    public static final String REGION = "region";
    public static final String TIMESTAMP = "timestamp";
    public static final String UPDATED = "updated";
    public static final String LOW_PRICE = "24min";
    public static final String CURRENT_PRICE = "buy";
    public static final String HIGH_PRICE = "24max";

    private String region;
    private String timestamp;
    private String updated;
    private String lowPrice;
    private String currentPrice;
    private String highPrice;
    private ArrayList history = new ArrayList();

    public TokenInfo() {
    }

    public TokenInfo(String region, String timestamp, String updated, String lowPrice, String currentPrice, String highPrice, ArrayList history) {
        this.region = region;
        this.timestamp = timestamp;
        this.updated = updated;
        this.lowPrice = lowPrice;
        this.currentPrice = currentPrice;
        this.highPrice = highPrice;
        this.history = history;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getUpdated() {
        return updated;
    }

    public void setUpdated(String updated) {
        this.updated = updated;
    }

    public String getLowPrice() {
        return lowPrice;
    }

    public void setLowPrice(String lowPrice) {
        this.lowPrice = lowPrice;
    }

    public String getCurrentPrice() {
        return currentPrice;
    }

    public void setCurrentPrice(String currentPrice) {
        this.currentPrice = currentPrice;
    }

    public String getHighPrice() {
        return highPrice;
    }

    public void setHighPrice(String highPrice) {
        this.highPrice = highPrice;
    }

    public void addToHistory(Object o) {
        history.add(o);
    }

    public ArrayList getHistory() {
        return history;
    }

    public void clearHistory() {
        history.clear();
    }

}
