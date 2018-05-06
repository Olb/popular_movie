package com.flx.popmovies.data.source.local;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.flx.popmovies.data.Movie;

@Database(entities = {Movie.class}, version = 1)
public abstract class MovieDatabase extends RoomDatabase {

    private static MovieDatabase INSTANCE;

    public abstract MoviesDao moviesDao();

    private static final Object sLock = new Object();

    public static MovieDatabase getInstance(Context context) {
        synchronized (sLock) {
            if (INSTANCE == null) {
                INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                        MovieDatabase.class, "Movies.db")
                        .build();
            }
            return INSTANCE;
        }
    }

    public static MovieDatabase getInstance() {
        synchronized (sLock) {
            return INSTANCE;
        }
    }
}
