package com.flx.popmovies.data.source.local;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.flx.popmovies.data.Movie;
import com.flx.popmovies.data.MovieResults;
import com.flx.popmovies.data.source.MoviesDataSource;
import com.flx.popmovies.util.ContextSingleton;

public class MovieLocalDataSource implements MoviesDataSource {

    private static MovieLocalDataSource INSTANCE;

    private MovieLocalDataSource() {
    }

    public static MovieLocalDataSource getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new MovieLocalDataSource();
        }

        return INSTANCE;
    }

    @Override
    public void getFavorites(final LoadMoviesResourceCallback callback) {

        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                MovieResults movieResults = intent.getParcelableExtra(MovieIntentService.RESULT_MOVIES);
                if (movieResults == null || movieResults.getMovies() == null) {
                    callback.onDataNotAvailable();
                    return;
                }
                callback.onMoviesLoaded(movieResults);
            }
        };

        IntentFilter intentFilter = new IntentFilter(
                MovieIntentService.ACTION_GET_FAVORITES_FROM_DB);


        LocalBroadcastManager.getInstance(ContextSingleton.getInstance(null).getContext())
                .registerReceiver(broadcastReceiver,
                        intentFilter);

        Intent intent = new Intent(ContextSingleton.getInstance(null).getContext(), MovieIntentService.class);
        intent.setAction(MovieIntentService.ACTION_GET_FAVORITES_FROM_DB);

        ContextSingleton.getInstance(null).getContext().startService(intent);

    }

    @Override
    public void removeFavorite(long movieId, final DeleteResourceCallback callback) {

        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int numDeleted = intent.getIntExtra(MovieIntentService.REMOVE_FAVORITE, 0);
                if (numDeleted != 0) {
                    callback.onResourceDeleted();
                } else {
                    callback.onDeleteFailed();
                }
            }
        };

        IntentFilter intentFilter = new IntentFilter(
                MovieIntentService.ACTION_REMOVE_FAVORITE);


        LocalBroadcastManager.getInstance(ContextSingleton.getInstance(null).getContext())
                .registerReceiver(broadcastReceiver,
                        intentFilter);

        Intent intent = new Intent(ContextSingleton.getInstance(null).getContext(), MovieIntentService.class);
        intent.setAction(MovieIntentService.ACTION_REMOVE_FAVORITE);
        intent.putExtra(MovieIntentService.REMOVE_FAVORITE, movieId);

        ContextSingleton.getInstance(null).getContext().startService(intent);

    }

    @Override
    public void getSavedMovie(long movieId, final GetResourceCallback callback) {

        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Movie movie = intent.getParcelableExtra(MovieIntentService.RESULT_MOVIE);
                if (movie == null) {
                    callback.onDataNotAvailable();
                    return;
                }
                callback.onMovieLoaded(movie);
            }
        };

        IntentFilter intentFilter = new IntentFilter(
                MovieIntentService.ACTION_GET_MOVIE_FROM_DB);


        LocalBroadcastManager.getInstance(ContextSingleton.getInstance(null).getContext())
                .registerReceiver(broadcastReceiver,
                        intentFilter);

        Intent intent = new Intent(ContextSingleton.getInstance(null).getContext(), MovieIntentService.class);
        intent.setAction(MovieIntentService.ACTION_GET_MOVIE_FROM_DB);
        intent.putExtra(MovieIntentService.RESULT_MOVIE, movieId);

        ContextSingleton.getInstance(null).getContext().startService(intent);

    }

    @Override
    public void saveMovie(Movie movie, int isFavorite, final SaveResourceCallback callback) {
        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                boolean result = intent.getBooleanExtra(MovieIntentService.RESULT_MOVIE_SAVED, false);
                if (result) {
                    callback.onResourceSaved();
                    return;
                }
                callback.onSaveFailed();
            }
        };

        IntentFilter intentFilter = new IntentFilter(
                MovieIntentService.ACTION_SAVE_MOVIE_TO_DB);


        LocalBroadcastManager.getInstance(ContextSingleton.getInstance(null).getContext())
                .registerReceiver(broadcastReceiver,
                        intentFilter);

        Intent intent = new Intent(ContextSingleton.getInstance(null).getContext(), MovieIntentService.class);
        intent.setAction(MovieIntentService.ACTION_SAVE_MOVIE_TO_DB);

        intent.putExtra(MovieIntentService.SAVE_MOVIE_PARCEL, movie);
        intent.putExtra(MovieIntentService.SAVE_MOVIE_FAVORITE, isFavorite);

        ContextSingleton.getInstance(null).getContext().startService(intent);
    }

    @Override
    public void savePosterImage(String path, Bitmap posterImage, final SaveResourceCallback callback) {
        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                boolean result = intent.getBooleanExtra(MovieIntentService.RESULT_POSTER_SAVED, false);
                if (result) {
                    callback.onResourceSaved();
                    return;
                }
                callback.onSaveFailed();
            }
        };

        IntentFilter intentFilter = new IntentFilter(
                MovieIntentService.ACTION_SAVE_POSTER_IMAGE);


        LocalBroadcastManager.getInstance(ContextSingleton.getInstance(null).getContext())
                .registerReceiver(broadcastReceiver,
                        intentFilter);

        Intent intent = new Intent(ContextSingleton.getInstance(null).getContext(), MovieIntentService.class);
        intent.setAction(MovieIntentService.ACTION_SAVE_POSTER_IMAGE);

        intent.putExtra(MovieIntentService.SAVE_POSTER_IMAGE_PARCEL, posterImage);
        intent.putExtra(MovieIntentService.SAVE_POSTER_PATH, path);

        ContextSingleton.getInstance(null).getContext().startService(intent);
    }

    @Override
    public void getMovieRuntime(Movie movie, GetResourceRunTimeCallback callback) {
        throw new UnsupportedOperationException("Cannot call this method for local data source");
    }

    @Override
    public void getTrailers(long movieId, LoadTrailersResourceCallback callback) {
        throw new UnsupportedOperationException("Cannot get trailers locally");
    }

    @Override
    public void getReviews(long movieId, LoadReviewsResourceCallback callback) {
        throw new UnsupportedOperationException("Cannot get reviews locally");
    }

    @Override
    public void getMovie(long movieId, GetResourceCallback callback) {
        throw new UnsupportedOperationException("Cannot call this method in local remote source");
    }

    @Override
    public void getMovies(String sortOrder, LoadMoviesResourceCallback callback) {
        throw new UnsupportedOperationException("Cannot call this method on local");
    }
}
