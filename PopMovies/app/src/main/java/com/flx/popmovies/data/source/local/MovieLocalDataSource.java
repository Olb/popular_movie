package com.flx.popmovies.data.source.local;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.support.v4.content.LocalBroadcastManager;

import com.flx.popmovies.PopMovies;
import com.flx.popmovies.data.Movie;
import com.flx.popmovies.data.MovieResults;
import com.flx.popmovies.data.source.MoviesDataSource;

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
        final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                MovieResults movieResults = intent.getParcelableExtra(MovieIntentService.RESULT_MOVIES);
                LocalBroadcastManager.getInstance(PopMovies.getAppContext()).
                        unregisterReceiver(this);
                if (movieResults == null || movieResults.getMovies() == null) {
                    callback.onDataNotAvailable();
                    return;
                }
                callback.onMoviesLoaded(movieResults);
            }
        };

        IntentFilter intentFilter = new IntentFilter(
                MovieIntentService.FAVORITES_RETRIEVED);

        LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(PopMovies.getAppContext());
        broadcastManager.registerReceiver(broadcastReceiver,
                intentFilter);

        Intent intent = new Intent(PopMovies.getAppContext(), MovieIntentService.class);
        intent.setAction(MovieIntentService.ACTION_GET_FAVORITES_FROM_DB);

        MovieIntentService.enqueueWork(PopMovies.getAppContext(), MovieIntentService.class, 333, intent);
        broadcastManager.sendBroadcast(intent);
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
                MovieIntentService.REMOVED_FAVORITE);


        LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(PopMovies.getAppContext());
        broadcastManager.registerReceiver(broadcastReceiver,
                        intentFilter);

        Intent intent = new Intent(PopMovies.getAppContext(), MovieIntentService.class);
        intent.setAction(MovieIntentService.ACTION_REMOVE_FAVORITE);
        intent.putExtra(MovieIntentService.REMOVE_FAVORITE, movieId);

        MovieIntentService.enqueueWork(PopMovies.getAppContext(), MovieIntentService.class, 333, intent);
        broadcastManager.sendBroadcast(intent);
    }

    @Override
    public void getSavedMovie(long movieId, final GetResourceCallback callback) {
        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Movie movie = intent.getParcelableExtra(MovieIntentService.MOVIE_RETRIEVED);
                if (movie == null) {
                    callback.onDataNotAvailable();
                    return;
                }
                callback.onMovieLoaded(movie);
            }
        };

        IntentFilter intentFilter = new IntentFilter(
                MovieIntentService.ACTION_RETRIEVED_MOVIE_FROM_DB);

        LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(PopMovies.getAppContext());
        broadcastManager.registerReceiver(broadcastReceiver,
                        intentFilter);

        Intent intent = new Intent(PopMovies.getAppContext(), MovieIntentService.class);
        intent.setAction(MovieIntentService.ACTION_GET_MOVIE_FROM_DB);
        intent.putExtra(MovieIntentService.RESULT_MOVIE, movieId);

        MovieIntentService.enqueueWork(PopMovies.getAppContext(), MovieIntentService.class, 333, intent);
        broadcastManager.sendBroadcast(intent);
    }

    @Override
    public void saveMovie(Movie movie, int isFavorite, final SaveResourceCallback callback) {
        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                boolean result = intent.getBooleanExtra(MovieIntentService.MOVIE_SAVED, false);
                LocalBroadcastManager.getInstance(PopMovies.getAppContext()).
                        unregisterReceiver(this);
                if (result) {
                    callback.onResourceSaved();
                    return;
                }
                callback.onSaveFailed();
            }
        };

        IntentFilter intentFilter = new IntentFilter(
                MovieIntentService.RESULT_MOVIE_SAVED);

        LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(PopMovies.getAppContext());
        broadcastManager.registerReceiver(broadcastReceiver,
                intentFilter);

        Intent intent = new Intent(PopMovies.getAppContext(), MovieIntentService.class);
        intent.setAction(MovieIntentService.ACTION_SAVE_MOVIE_TO_DB);
        intent.putExtra(MovieIntentService.SAVE_MOVIE_PARCEL, movie);
        intent.putExtra(MovieIntentService.SAVE_MOVIE_FAVORITE, isFavorite);

        MovieIntentService.enqueueWork(PopMovies.getAppContext(), MovieIntentService.class, 333, intent);
        broadcastManager.sendBroadcast(intent);
    }

    @Override
    public void savePosterImage(String path, Bitmap posterImage, final SaveResourceCallback callback) {
        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                boolean result = intent.getBooleanExtra(MovieIntentService.RESULT_POSTER_SAVED, false);
                LocalBroadcastManager.getInstance(PopMovies.getAppContext()).
                        unregisterReceiver(this);
                if (result) {
                    callback.onResourceSaved();
                    return;
                }
                callback.onSaveFailed();
            }
        };

        IntentFilter intentFilter = new IntentFilter(
                MovieIntentService.ACTION_SAVED_POSTER_IMAGE);


        LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(PopMovies.getAppContext());
        broadcastManager.registerReceiver(broadcastReceiver,
                intentFilter);

        Intent intent = new Intent(PopMovies.getAppContext(), MovieIntentService.class);
        intent.setAction(MovieIntentService.ACTION_SAVE_POSTER_IMAGE);

        intent.putExtra(MovieIntentService.SAVE_POSTER_IMAGE_PARCEL, posterImage);
        intent.putExtra(MovieIntentService.SAVE_POSTER_PATH, path);

        MovieIntentService.enqueueWork(PopMovies.getAppContext(), MovieIntentService.class, 333, intent);
        broadcastManager.sendBroadcast(intent);
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
