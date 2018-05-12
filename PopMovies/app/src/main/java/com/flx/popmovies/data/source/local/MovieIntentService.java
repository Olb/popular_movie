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

import com.flx.popmovies.PopMovies;
import com.flx.popmovies.data.Movie;
import com.flx.popmovies.data.MovieResults;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MovieIntentService extends JobIntentService {

    public static final String ACTION_GET_MOVIE_FROM_DB = "ACTION_GET_MOVIE_FROM_DB";
    public static final String ACTION_GET_FAVORITES_FROM_DB = "ACTION_GET_FAVORITES_FROM_DB";
    public static final String ACTION_SAVE_MOVIE_TO_DB = "ACTION_SAVE_MOVIE_TO_DB";
    public static final String ACTION_REMOVE_FAVORITE = "ACTION_REMOVE_FAVORITE";
    public static final String ACTION_SAVE_POSTER_IMAGE = "ACTION_SAVE_POSTER_IMAGE";
    public static final String ACTION_SAVED_POSTER_IMAGE = "ACTION_SAVED_POSTER_IMAGE";
    public static final String ACTION_RETRIEVED_MOVIE_FROM_DB = "ACTION_RETRIEVED_MOVIE_FROM_DB";
    public static final String RESULT_MOVIES = "RESULT_MOVIES";
    public static final String RESULT_MOVIE = "RESULT_MOVIE";
    public static final String REMOVE_FAVORITE = "REMOVE_FAVORITE";
    public static final String RESULT_MOVIE_SAVED = "RESULT_MOVIE_SAVED";
    public static final String RESULT_POSTER_SAVED = "RESULT_POSTER_SAVED";
    public static final String SAVE_MOVIE_PARCEL = "SAVE_MOVIE_PARCEL";
    public static final String SAVE_MOVIE_FAVORITE = "SAVE_MOVIE_FAVORITE";
    public static final String SAVE_POSTER_IMAGE_PARCEL = "SAVE_POSTER_IMAGE_PARCEL";
    public static final String SAVE_POSTER_PATH = "SAVE_POSTER_PATH";
    public static final String MOVIE_SAVED = "MOVIE_SAVED";
    public static final String MOVIE_RETRIEVED = "MOVIE_RETREIVED";
    public static final String FAVORITES_RETRIEVED = "FAVORITES_RETREIVED";
    public static final String REMOVED_FAVORITE = "REMOVED_FAVORITE";
    private static final String SAVE_DIRECTORY = "imageDir";
    private static final int FAVORITE = 1;

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        String action = intent.getAction();
        assert action != null;
        switch (action) {
            case ACTION_GET_FAVORITES_FROM_DB:
                getFavorites();
                break;
            case ACTION_REMOVE_FAVORITE: {
                long movieId;
                movieId = intent.getLongExtra(REMOVE_FAVORITE, 0);
                removeFavorite(movieId);
                break;
            }
            case ACTION_GET_MOVIE_FROM_DB: {
                long movieId = intent.getLongExtra(RESULT_MOVIE, 0);
                getSavedMovie(movieId);
                break;
            }
            case ACTION_SAVE_MOVIE_TO_DB:
                Movie movie = intent.getParcelableExtra(SAVE_MOVIE_PARCEL);
                int favorite = intent.getIntExtra(SAVE_MOVIE_FAVORITE, FAVORITE);
                saveMovie(movie, favorite);
                break;
            case ACTION_SAVE_POSTER_IMAGE:
                Bitmap bitmap = intent.getParcelableExtra(SAVE_POSTER_IMAGE_PARCEL);
                String path = intent.getStringExtra(SAVE_POSTER_PATH);
                savePosterImage(path, bitmap);
                break;
        }
    }

    private void savePosterImage(String path, Bitmap posterImage) {
        ContextWrapper cw = new ContextWrapper(PopMovies.getAppContext());

        File directory = cw.getDir(SAVE_DIRECTORY, Context.MODE_PRIVATE);

        File imagePath = new File(directory, path);

        Log.d("ACTUAL SAVE", imagePath.getPath());
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
                    LocalBroadcastManager.getInstance(PopMovies.getAppContext())
                            .sendBroadcast(localIntent);
                }
            } catch (IOException e) {
                e.printStackTrace();
                Intent localIntent = new Intent(ACTION_SAVED_POSTER_IMAGE);
                localIntent.putExtra(RESULT_POSTER_SAVED, false);
                LocalBroadcastManager.getInstance(PopMovies.getAppContext())
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
            Intent localIntent = new Intent(ACTION_RETRIEVED_MOVIE_FROM_DB);
            LocalBroadcastManager.getInstance(PopMovies.getAppContext())
                    .sendBroadcast(localIntent);
            return;
        }

        if (cursor == null) {
            Intent localIntent = new Intent(ACTION_RETRIEVED_MOVIE_FROM_DB);
            LocalBroadcastManager.getInstance(PopMovies.getAppContext())
                    .sendBroadcast(localIntent);

            return;
        }

        if (!cursor.moveToNext()) {
            Intent localIntent = new Intent(ACTION_RETRIEVED_MOVIE_FROM_DB);
            LocalBroadcastManager.getInstance(PopMovies.getAppContext())
                    .sendBroadcast(localIntent);

            return;
        }

        Intent localIntent = new Intent(ACTION_RETRIEVED_MOVIE_FROM_DB);
        localIntent.putExtra(MOVIE_RETRIEVED, buildMovie(cursor));
        LocalBroadcastManager.getInstance(PopMovies.getAppContext())
                .sendBroadcast(localIntent);

    }

    private void removeFavorite(long movieId) {

        Uri uri = MoviesContract.MovieEntry.CONTENT_URI;
        uri = uri.buildUpon().appendPath(Long.toString(movieId)).build();

        int numDeleted = getApplicationContext()
                .getContentResolver()
                .delete(uri, null, null);

        Intent localIntent = new Intent(REMOVED_FAVORITE);
        localIntent.putExtra(REMOVE_FAVORITE, numDeleted);
        LocalBroadcastManager.getInstance(PopMovies.getAppContext())
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
            Intent localIntent = new Intent(FAVORITES_RETRIEVED);
            LocalBroadcastManager.getInstance(PopMovies.getAppContext())
                    .sendBroadcast(localIntent);
            return;
        }

        if (cursor == null) {
            Intent localIntent = new Intent(FAVORITES_RETRIEVED);
            LocalBroadcastManager.getInstance(PopMovies.getAppContext())
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

        Intent localIntent = new Intent(FAVORITES_RETRIEVED);
        localIntent.putExtra(RESULT_MOVIES, movieResults);
        LocalBroadcastManager.getInstance(PopMovies.getAppContext())
                .sendBroadcast(localIntent);
    }

    private void saveMovie(Movie movie, int isFavorite) {
        ContentValues contentValues = new ContentValues();

        contentValues.put(MoviesContract.MovieEntry.COLUMN_ID, movie.getId());
        contentValues.put(MoviesContract.MovieEntry.COLUMN_RATING, movie.getVoteAverage());
        contentValues.put(MoviesContract.MovieEntry.COLUMN_RELEASE_DATE, movie.getReleaseDate());
        contentValues.put(MoviesContract.MovieEntry.COLUMN_SYNOPSIS, movie.getOverview());
        contentValues.put(MoviesContract.MovieEntry.COLUMN_TITLE, movie.getTitle());
        contentValues.put(MoviesContract.MovieEntry.COLUMN_POSTER_PATH, movie.getPosterPath());
        contentValues.put(MoviesContract.MovieEntry.COLUMN_IS_FAVORITE, isFavorite);
        contentValues.put(MoviesContract.MovieEntry.COLUMN_RUNTIME, movie.getRunTime());

        Uri uri = PopMovies.getAppContext()
                .getContentResolver().insert(MoviesContract.MovieEntry.CONTENT_URI, contentValues);


        boolean saveResult = false;
        if (uri != null) {
            saveResult = true;
        }

        Intent localIntent = new Intent(RESULT_MOVIE_SAVED);
        localIntent.putExtra(MOVIE_SAVED, saveResult);

        LocalBroadcastManager.getInstance(PopMovies.getAppContext())
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
