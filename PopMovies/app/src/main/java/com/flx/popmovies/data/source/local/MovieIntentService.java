package com.flx.popmovies.data.source.local;


import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.JobIntentService;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.flx.popmovies.data.Movie;
import com.flx.popmovies.data.MovieResults;
import com.flx.popmovies.util.ContextSingleton;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MovieIntentService extends JobIntentService {

    public static final String ACTION_GET_MOVIE_FROM_DB = "get-movie";
    public static final String ACTION_GET_FAVORITES_FROM_DB = "get-movies";
    public static final String ACTION_SAVE_MOVIE_TO_DB = "save-movie";
    public static final String ACTION_REMOVE_FAVORITE = "remove-favorite";
    public static final String ACTION_SAVE_POSTER_IMAGE = "save-poster-image";

    public static final String RESULT_MOVIES = "movies";
    public static final String RESULT_MOVIE = "movie";
    public static final String REMOVE_FAVORITE = "remove-favorite-result";
    public static final String RESULT_MOVIE_SAVED = "movie-saved";
    public static final String RESULT_POSTER_SAVED = "poster-saved";
    public static final String SAVE_MOVIE_PARCEL = "movie-saved-parcel";
    public static final String SAVE_MOVIE_FAVORITE = "movie-saved-favorite";
    public static final String SAVE_POSTER_IMAGE_PARCEL = "save-poster-image-parcel";
    public static final String SAVE_POSTER_PATH = "save-poster-path";

    public static final int FAVORITE = 1;

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        String action = intent.getAction();
        if (action.equals(ACTION_GET_FAVORITES_FROM_DB)) {
            getFavorites();
        } else if (action.equals(ACTION_REMOVE_FAVORITE)) {
            long movieId;
            movieId = intent.getLongExtra(REMOVE_FAVORITE, 0);
            removeFavorite(movieId);
        } else if (action.equals(ACTION_GET_MOVIE_FROM_DB)) {
            long movieId = intent.getLongExtra(RESULT_MOVIE, 0);
            getSavedMovie(movieId);
        } else if (action.equals(ACTION_SAVE_MOVIE_TO_DB)) {
            Movie movie = intent.getParcelableExtra(SAVE_MOVIE_PARCEL);
            int favorite = intent.getIntExtra(SAVE_MOVIE_FAVORITE, FAVORITE);
            saveMovie(movie, favorite);
        } else if (action.equals(ACTION_SAVE_POSTER_IMAGE)) {
            Bitmap bitmap = intent.getParcelableExtra(SAVE_POSTER_IMAGE_PARCEL);
            String path = intent.getStringExtra(SAVE_POSTER_PATH);
            savePosterImage(path, bitmap);
        }
    }

    public void savePosterImage(String path, Bitmap posterImage) {
        ContextWrapper cw = new ContextWrapper(ContextSingleton.getInstance(null).getContext());

        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);

        File imagePath = new File(directory, path);


        FileOutputStream out = null;
        try {
            out = new FileOutputStream(imagePath);
            posterImage.compress(Bitmap.CompressFormat.PNG, 100, out);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                    Intent localIntent = new Intent(ACTION_SAVE_POSTER_IMAGE);
                    localIntent.putExtra(RESULT_POSTER_SAVED, true);
                    LocalBroadcastManager.getInstance(ContextSingleton.getInstance(null).getContext())
                            .sendBroadcast(localIntent);
                }
            } catch (IOException e) {
                e.printStackTrace();
                Intent localIntent = new Intent(ACTION_SAVE_POSTER_IMAGE);
                localIntent.putExtra(RESULT_POSTER_SAVED, false);
                LocalBroadcastManager.getInstance(ContextSingleton.getInstance(null).getContext())
                        .sendBroadcast(localIntent);
            }
        }
    }

    private void getSavedMovie(long movieId) {
        Cursor cursor;

        Uri uri = MoviesContract.MovieEntry.CONTENT_URI;
        uri = uri.buildUpon().appendPath(movieId + "").build();

        try {
            cursor = getApplicationContext().getContentResolver()
                    .query(uri,
                            null,
                            null,
                            null,
                            null);
        } catch (Exception e) {
            Intent localIntent = new Intent(ACTION_GET_MOVIE_FROM_DB);
            LocalBroadcastManager.getInstance(ContextSingleton.getInstance(null).getContext())
                    .sendBroadcast(localIntent);
            return;
        }

        if (cursor == null) {
            Intent localIntent = new Intent(ACTION_GET_MOVIE_FROM_DB);
            LocalBroadcastManager.getInstance(ContextSingleton.getInstance(null).getContext())
                    .sendBroadcast(localIntent);

            return;
        }

        if (!cursor.moveToNext()) {
            Intent localIntent = new Intent(ACTION_GET_MOVIE_FROM_DB);
            LocalBroadcastManager.getInstance(ContextSingleton.getInstance(null).getContext())
                    .sendBroadcast(localIntent);

            return;
        }

        Intent localIntent = new Intent(ACTION_GET_MOVIE_FROM_DB);
        localIntent.putExtra(RESULT_MOVIE, buildMovie(cursor));
        LocalBroadcastManager.getInstance(ContextSingleton.getInstance(null).getContext())
                .sendBroadcast(localIntent);

    }

    private void removeFavorite(long movieId) {

        Uri uri = MoviesContract.MovieEntry.CONTENT_URI;
        uri = uri.buildUpon().appendPath(Long.toString(movieId)).build();

        int numDeleted = getApplicationContext()
                .getContentResolver()
                .delete(uri, null, null);

        Log.d("DELETETIONS", ": " + numDeleted);
        Intent localIntent = new Intent(ACTION_REMOVE_FAVORITE);
        localIntent.putExtra(REMOVE_FAVORITE, numDeleted);
        LocalBroadcastManager.getInstance(ContextSingleton.getInstance(null).getContext())
                .sendBroadcast(localIntent);
    }

    private void getFavorites() {
        Cursor cursor;

        try {
            cursor = getApplicationContext().getContentResolver()
                    .query(MoviesContract.MovieEntry.CONTENT_URI,
                            null,
                            null,
                            null,
                            null);
        } catch (Exception e) {
            Intent localIntent = new Intent(ACTION_GET_FAVORITES_FROM_DB);
            LocalBroadcastManager.getInstance(ContextSingleton.getInstance(null).getContext())
                    .sendBroadcast(localIntent);
            return;
        }

        if (cursor == null) {
            Intent localIntent = new Intent(ACTION_GET_FAVORITES_FROM_DB);
            LocalBroadcastManager.getInstance(ContextSingleton.getInstance(null).getContext())
                    .sendBroadcast(localIntent);

            return;
        }

        List<Movie> movieList = new ArrayList<>();
        while (cursor.moveToNext()) {
            Movie movie = buildMovie(cursor);
            movie.setIsFavorite(1);
            movieList.add(movie);
        }

        Movie[] movies = new Movie[movieList.size()];
        movies = movieList.toArray(movies);
        MovieResults movieResults = new MovieResults();
        movieResults.setMovies(movies);

        Intent localIntent = new Intent(ACTION_GET_FAVORITES_FROM_DB);
        localIntent.putExtra(RESULT_MOVIES, movieResults);
        LocalBroadcastManager.getInstance(ContextSingleton.getInstance(null).getContext())
                .sendBroadcast(localIntent);
    }

    public void saveMovie(Movie movie, int isFavorite) {
        ContentValues contentValues = new ContentValues();

        contentValues.put(MoviesContract.MovieEntry.COLUMN_ID, movie.getId());
        contentValues.put(MoviesContract.MovieEntry.COLUMN_RATING, movie.getVoteAverage());
        contentValues.put(MoviesContract.MovieEntry.COLUMN_RELEASE_DATE, movie.getReleaseDate());
        contentValues.put(MoviesContract.MovieEntry.COLUMN_SYNOPSIS, movie.getOverview());
        contentValues.put(MoviesContract.MovieEntry.COLUMN_TITLE, movie.getTitle());
        contentValues.put(MoviesContract.MovieEntry.COLUMN_POSTER_PATH, movie.getPosterPath());
        contentValues.put(MoviesContract.MovieEntry.COLUMN_IS_FAVORITE, isFavorite);
        contentValues.put(MoviesContract.MovieEntry.COLUMN_RUNTIME, movie.getRunTime());

        Uri uri = ContextSingleton.getInstance(null).getContext()
                .getContentResolver().insert(MoviesContract.MovieEntry.CONTENT_URI, contentValues);


        boolean saveResult = false;
        if (uri != null) {
            saveResult = true;
        }

        Intent localIntent = new Intent(ACTION_SAVE_MOVIE_TO_DB);
        localIntent.putExtra(RESULT_MOVIE_SAVED, saveResult);
        LocalBroadcastManager.getInstance(ContextSingleton.getInstance(null).getContext())
                .sendBroadcast(localIntent);
    }

    private Movie buildMovie(Cursor cursor) {
        Movie movie = new Movie();

        int movieIdIndex = cursor.getColumnIndex(MoviesContract.MovieEntry.COLUMN_ID);
        int titleIndex = cursor.getColumnIndex(MoviesContract.MovieEntry.COLUMN_TITLE);
        int ratingIndex = cursor.getColumnIndex(MoviesContract.MovieEntry.COLUMN_RATING);
        int releaseDateIndex = cursor.getColumnIndex(MoviesContract.MovieEntry.COLUMN_RELEASE_DATE);
        int synopsisIndex = cursor.getColumnIndex(MoviesContract.MovieEntry.COLUMN_SYNOPSIS);
        int posterPath = cursor.getColumnIndex(MoviesContract.MovieEntry.COLUMN_POSTER_PATH);
        int isFavorite = cursor.getColumnIndex(MoviesContract.MovieEntry.COLUMN_IS_FAVORITE);
        int runtime = cursor.getColumnIndex(MoviesContract.MovieEntry.COLUMN_RUNTIME);

        movie.setId(Integer.valueOf(cursor.getString(movieIdIndex)));
        movie.setTitle(cursor.getString(titleIndex));
        movie.setVoteAverage(Double.valueOf(cursor.getString(ratingIndex)));
        movie.setReleaseDate(cursor.getString(releaseDateIndex));
        movie.setOverview(cursor.getString(synopsisIndex));
        movie.setPosterPath(cursor.getString(posterPath));
        movie.setIsFavorite(cursor.getInt(isFavorite));
        movie.setRunTime(cursor.getString(runtime));

        return movie;
    }
}
