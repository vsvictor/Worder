package com.education.worder.fragments;

public class Settings {
    private long dict;
    private int speed;
    private static Settings settings;

    public static Settings load(){
        if(settings == null){
            settings = new Settings();
        }
        return settings;
    }

    private Settings(){
        this.dict = 1;
        this.speed = 100;
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
}
