package com.education.worder;

import android.Manifest;
import android.app.Application;
import android.content.Context;

public class App extends Application {
    private static final String TAG = App.class.getSimpleName();
    private static Context context;
    private static final String[] permission = {
            Manifest.permission.INTERNET,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
    }
    public String[] getPermission(){
        return permission;
    }

    public static Context getContext() {
        return context;
    }
}
