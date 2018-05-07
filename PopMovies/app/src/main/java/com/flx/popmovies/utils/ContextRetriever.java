package com.flx.popmovies.utils;

import android.content.Context;

public class ContextRetriever {

    private static ContextRetriever INSTANCE = null;
    private Context mContext;

    private ContextRetriever(Context context) {
        this.mContext = context;
    }

    public static ContextRetriever getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new ContextRetriever(context);
        }

        return INSTANCE;
    }

    public Context getContext() {
        return mContext;
    }

}
