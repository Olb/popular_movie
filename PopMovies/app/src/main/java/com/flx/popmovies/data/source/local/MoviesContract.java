package com.flx.popmovies.data.source.local;

import android.net.Uri;
import android.provider.BaseColumns;

public final class MoviesContract {

    public MoviesContract() {}

    public static final String AUTHORITY = "com.flx.popmovies";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    public static final String PATH_MOVIES = "movies";

    public static final class MovieEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_MOVIES).build();

        public static final String TABLE_NAME = "movie";

        public static final String COLUMN_ID = "movieId";

        public static final String COLUMN_TITLE = "title";

        public static final String COLUMN_RATING = "rating";

        public static final String COLUMN_RELEASE_DATE = "releaseDate";

        public static final String COLUMN_SYNOPSIS = "synopsis";
    }
}
