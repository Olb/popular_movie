package com.flx.popmovies.data.source;

import android.graphics.Bitmap;

import com.flx.popmovies.data.Movie;
import com.flx.popmovies.data.MovieResults;
import com.flx.popmovies.data.TrailerResults;

public interface MoviesDataSource {

    interface LoadMoviesResourceCallback {

        void onMoviesLoaded(MovieResults movieResults);

        void onDataNotAvailable();
    }

    interface LoadTrailersResourceCallback {

        void onTrailersLoaded(TrailerResults trailerResults);

        void onDataNotAvailable();
    }

    interface GetResourceCallback {

        void onMovieLoaded(Movie movie);

        void onDataNotAvailable();
    }

    interface SaveResourceCallback {

        void onResourceSaved();

        void onSaveFailed();
    }

    interface DeleteResourceCallback {

        void onResourceDeleted();

        void onDeleteFailed();
    }

    void getMovies(String sortOrder, LoadMoviesResourceCallback callback);

    void getFavorites(LoadMoviesResourceCallback callback);

    void removeFavorite(long movieId, DeleteResourceCallback callback);

    void getMovie(long movieId, GetResourceCallback callback);

    void getSavedMovie(long movieId, GetResourceCallback callback);

    void getTrailers(long movieId, LoadTrailersResourceCallback callback);

    void getReviews(long movieId, LoadMoviesResourceCallback callback);

    void saveMovie(Movie movie, int isFavorite, SaveResourceCallback callback);

    void savePosterImage(String path, Bitmap posterImage, SaveResourceCallback callback);

}
