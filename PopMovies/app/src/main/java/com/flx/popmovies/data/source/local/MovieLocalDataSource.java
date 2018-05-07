package com.flx.popmovies.data.source.local;

import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;

import com.flx.popmovies.data.Movie;
import com.flx.popmovies.data.source.MoviesDataSource;
import com.flx.popmovies.utils.ContextRetriever;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class MovieLocalDataSource implements MoviesDataSource {

    private static MovieLocalDataSource INSTANCE;


    public static MovieLocalDataSource getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new MovieLocalDataSource();
        }

        return INSTANCE;
    }

    private MovieLocalDataSource() {}

    @Override
    public void getMovies(String sortOrder, LoadResourceCallback callback) {

    }

    @Override
    public void getMovie(long movieId, GetResourceCallback callback) {

    }

    @Override
    public void getSavedMovie(long movieId, GetResourceCallback callback) {
        Cursor cursor = null;

        Uri uri = MoviesContract.MovieEntry.CONTENT_URI;
        uri = uri.buildUpon().appendPath(movieId + "").build();
        try {
            cursor = ContextRetriever.getInstance(null).getContext().getContentResolver()
                    .query(MoviesContract.MovieEntry.CONTENT_URI,
                            null,
                            null,
                            null,
                            null);
        } catch (Exception e) {
            callback.onDataNotAvailable();
        }

        if (cursor == null) {
            callback.onDataNotAvailable();
            return;
        }

        Movie movie = new Movie();

        int movieIdIndex = cursor.getColumnIndex(MoviesContract.MovieEntry.COLUMN_ID);
        int titleIndex = cursor.getColumnIndex(MoviesContract.MovieEntry.COLUMN_TITLE);
        int ratingIndex = cursor.getColumnIndex(MoviesContract.MovieEntry.COLUMN_RATING);
        int releaseDateIndex = cursor.getColumnIndex(MoviesContract.MovieEntry.COLUMN_RELEASE_DATE);
        int synopsisIndex = cursor.getColumnIndex(MoviesContract.MovieEntry.COLUMN_SYNOPSIS);
        int posterPath = cursor.getColumnIndex(MoviesContract.MovieEntry.COLUMN_POSTER_PATH);

        long i = Integer.valueOf(cursor.getString(movieIdIndex));
        movie.setId(i);
        movie.setTitle(cursor.getString(titleIndex));
        movie.setVoteAverage(Double.valueOf(cursor.getString(ratingIndex)));
        movie.setReleaseDate(cursor.getString(releaseDateIndex));
        movie.setOverview(cursor.getString(synopsisIndex));
        movie.setPosterPath(cursor.getString(posterPath));

        callback.onMovieLoaded(movie);
    }

    @Override
    public void getTrailers(long movieId, LoadResourceCallback callback) {

    }

    @Override
    public void getReviews(long movieId, LoadResourceCallback callback) {

    }

    @Override
    public void saveMovie(Movie movie, SaveResourceCallback callback) {
        ContentValues contentValues = new ContentValues();

        contentValues.put(MoviesContract.MovieEntry.COLUMN_ID, movie.getId());
        contentValues.put(MoviesContract.MovieEntry.COLUMN_RATING, movie.getVoteAverage());
        contentValues.put(MoviesContract.MovieEntry.COLUMN_RELEASE_DATE, movie.getReleaseDate());
        contentValues.put(MoviesContract.MovieEntry.COLUMN_SYNOPSIS, movie.getOverview());
        contentValues.put(MoviesContract.MovieEntry.COLUMN_TITLE, movie.getTitle());
        contentValues.put(MoviesContract.MovieEntry.COLUMN_POSTER_PATH, movie.getPosterPath());

        Uri uri = ContextRetriever.getInstance(null).getContext()
                .getContentResolver().insert(MoviesContract.MovieEntry.CONTENT_URI, contentValues);

        if (uri != null) {
            callback.onResourceSaved();
        } else {
            callback.onSaveFailed();
        }
    }

    @Override
    public void savePosterImage(String path, Bitmap posterImage, SaveResourceCallback callback) {
        ContextWrapper cw = new ContextWrapper(ContextRetriever.getInstance(null).getContext());

        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);

        File imagePath = new File(directory,path);


        FileOutputStream out = null;
        try {
            out = new FileOutputStream(imagePath);
            posterImage.compress(Bitmap.CompressFormat.PNG, 100, out);
            Log.d("File Saved", "File saved at: " + imagePath);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                    callback.onResourceSaved();
                }
            } catch (IOException e) {
                e.printStackTrace();
                callback.onSaveFailed();
            }
        }
    }
}
