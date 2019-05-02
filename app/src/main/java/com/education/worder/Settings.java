package com.education.worder;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Settings {
    private static final String IS_FIRST_START = "isFirstStart";
    private static final String DICTIONARY = "dict";
    private static final String SPEED = "speed";
    private static final String MIXES = "mixes";
    private boolean isFirstStart;
    private long dict;
    private int speed;
    private boolean isMuxed;
    private static Settings settings;
    private SharedPreferences sh;

    public static Settings load(){
        settings = new Settings();
        return settings;
    }

    private Settings(){
        this.dict = 1;

        sh = PreferenceManager.getDefaultSharedPreferences(App.getContext());
        isFirstStart = sh.getBoolean(IS_FIRST_START, true);
        this.speed = sh.getInt(SPEED,40);
        this.isMuxed = sh.getBoolean(MIXES, true);
    }

    public long getDictionaryID() {
        return dict;
    }

    public void setDictionaryID(long dict) {
        this.dict = dict;
    }

    public int getSpeed() {
        return speed;
    }
    public void setSpeed(int speed) {
        this.speed = speed;
    }
    public boolean isMuxed(){return this.isMuxed;}

    public boolean isFirstStart(){
        return isFirstStart;
    }
    public void setFirstStart(boolean is){
        SharedPreferences.Editor ed = sh.edit();
        ed.putBoolean(IS_FIRST_START, is);
        ed.commit();
    }
}
