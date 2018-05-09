package com.flx.popmovies.util;

import android.content.Context;

public class ContextSingleton {

    private static ContextSingleton INSTANCE = null;
    private Context mContext;

    private ContextSingleton(Context context) {
        this.mContext = context;
    }

    public static ContextSingleton getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new ContextSingleton(context);
        }

        return INSTANCE;
    }

    public Context getContext() {
        return mContext;
    }

}
