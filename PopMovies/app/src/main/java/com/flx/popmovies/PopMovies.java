package com.flx.popmovies;

import android.app.Application;
import android.content.Context;

public class PopMovies extends Application {

    private static Context context;

    public void onCreate() {
        super.onCreate();
        PopMovies.context = getApplicationContext();
    }

    public static Context getAppContext() {
        return PopMovies.context;
    }
}
