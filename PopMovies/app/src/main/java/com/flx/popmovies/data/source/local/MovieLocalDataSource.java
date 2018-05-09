package com.flx.popmovies.data.source.local;

import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;

import com.flx.popmovies.data.Movie;
import com.flx.popmovies.data.MovieResults;
import com.flx.popmovies.data.source.MoviesDataSource;
import com.flx.popmovies.util.ContextSingleton;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
    public void getMovies(String sortOrder, LoadMoviesResourceCallback callback) {

    }

    @Override
    public void getFavorites(LoadMoviesResourceCallback callback) {
        Cursor cursor = null;

        try {
            cursor = ContextSingleton.getInstance(null).getContext().getContentResolver()
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

        callback.onMoviesLoaded(movieResults);
    }

    @Override
    public void removeFavorite(long movieId, DeleteResourceCallback callback) {

        Uri uri = MoviesContract.MovieEntry.CONTENT_URI;
        uri = uri.buildUpon().appendPath(Long.toString(movieId)).build();

        int numDeleted = ContextSingleton.getInstance(null).getContext()
                .getContentResolver()
                .delete(uri, null, null);

        if (numDeleted != 0) {
            callback.onResourceDeleted();
        } else {
            callback.onDeleteFailed();
        }
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
            cursor = ContextSingleton.getInstance(null).getContext().getContentResolver()
                    .query(uri,
                            null,
                            null,
                            null,
                            null);
        } catch (Exception e) {
            callback.onDataNotAvailable();
            return;
        }

        if (cursor == null) {
            callback.onDataNotAvailable();
            return;
        }
        if (!cursor.moveToNext()) {
            callback.onDataNotAvailable();
            return;
        }
        callback.onMovieLoaded(buildMovie(cursor));
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
        long i = Integer.valueOf(cursor.getString(movieIdIndex));
        movie.setId(i);
        movie.setTitle(cursor.getString(titleIndex));
        movie.setVoteAverage(Double.valueOf(cursor.getString(ratingIndex)));
        movie.setReleaseDate(cursor.getString(releaseDateIndex));
        movie.setOverview(cursor.getString(synopsisIndex));
        movie.setPosterPath(cursor.getString(posterPath));

        Log.d("FAVO", cursor.getInt(isFavorite) +"");
        movie.setIsFavorite(cursor.getInt(isFavorite));

        return movie;
    }
    @Override
    public void getTrailers(long movieId, LoadTrailersResourceCallback callback) {

    }

    @Override
    public void getReviews(long movieId, LoadMoviesResourceCallback callback) {

    }

    @Override
    public void saveMovie(Movie movie, int isFavorite, SaveResourceCallback callback) {
        ContentValues contentValues = new ContentValues();

        contentValues.put(MoviesContract.MovieEntry.COLUMN_ID, movie.getId());
        contentValues.put(MoviesContract.MovieEntry.COLUMN_RATING, movie.getVoteAverage());
        contentValues.put(MoviesContract.MovieEntry.COLUMN_RELEASE_DATE, movie.getReleaseDate());
        contentValues.put(MoviesContract.MovieEntry.COLUMN_SYNOPSIS, movie.getOverview());
        contentValues.put(MoviesContract.MovieEntry.COLUMN_TITLE, movie.getTitle());
        contentValues.put(MoviesContract.MovieEntry.COLUMN_POSTER_PATH, movie.getPosterPath());
        contentValues.put(MoviesContract.MovieEntry.COLUMN_IS_FAVORITE, isFavorite);

        Uri uri = ContextSingleton.getInstance(null).getContext()
                .getContentResolver().insert(MoviesContract.MovieEntry.CONTENT_URI, contentValues);

        if (uri != null) {
            callback.onResourceSaved();
        } else {
            callback.onSaveFailed();
        }
    }

    @Override
    public void savePosterImage(String path, Bitmap posterImage, SaveResourceCallback callback) {
        ContextWrapper cw = new ContextWrapper(ContextSingleton.getInstance(null).getContext());

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
