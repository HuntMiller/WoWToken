package com.hmill.wowtoken.util;

/**
 * Created by HMill on 5/22/2017.
 */

public class Realm {

    public static final String TYPE = "type";
    public static final String POPULATION = "population";
    public static final String QUEUE = "queue";
    public static final String STATUS = "status";
    public static final String NAME = "name";
    public static final String SLUG = "slug";
    public static final String BATTLEGROUP = "battlegroup";
    public static final String LOCALE = "locale";
    public static final String TIMEZONE = "timezone";
    public static final String CONNECTED_REALMS = "connected_realms";

    String type;
    String population;
    boolean queue;
    boolean status;
    String name;
    String slug;
    String battlegroup;
    String locale;
    String timezone;
    String connectedRealms;

    public Realm(){
    }

    public Realm(String type, String population, boolean queue, boolean status, String name, String slug, String battlegroup, String locale, String timezone, String connectedRealms){
        this.type = type;
        this.population = population;
        this.queue = queue;
        this.status = status;
        this.name = name;
        this.slug = slug;
        this.battlegroup = battlegroup;
        this.locale = locale;
        this.timezone = timezone;
        this.connectedRealms = connectedRealms;
    }

    public void setType(String type){
        this.type = type;
    }

    public String getType(){
        return type;
    }

    public void setPopulation(String population){
        this.population = population;
    }

    public String getPopulation(){
        return population;
    }

    public void setQueue(boolean queue){
        this.queue = queue;
    }

    public boolean getQueue(){
        return queue;
    }

    public void setStatus(boolean status){
        this.status = status;
    }

    public boolean getStatus(){
        return status;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getName(){
        return name;
    }

    public void setSlug(String slug){
        this.slug = slug;
    }

    public String getSlug(){
        return slug;
    }

    public void setBattlegroup(String battlegroup){
        this.battlegroup = battlegroup;
    }

    public String getBattlegroup(){
        return battlegroup;
    }

    public void setLocale(String locale){
        this.locale = locale;
    }

    public String getLocale(){
        return locale;
    }

    public void setTimezone(String timezone){
        this.timezone = timezone;
    }

    public String getTimezone(){
        return timezone;
    }

    public void setConnectedRealms(String connectedRealms){
        this.connectedRealms = connectedRealms;
    }

    public String getConnectedRealms(){
        return connectedRealms;
    }

}
