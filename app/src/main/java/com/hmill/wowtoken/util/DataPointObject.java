package com.hmill.wowtoken.util;

/**
 * Created by HMill on 5/21/2017.
 */

public class DataPointObject {

    private String time;
    private String value;

    public DataPointObject() {
    }

    public DataPointObject(String time, String value) {
        this.time = time;
        this.value = value;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTime() {
        return time;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}
