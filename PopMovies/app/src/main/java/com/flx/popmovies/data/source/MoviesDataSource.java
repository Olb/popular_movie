package com.flx.popmovies.data.source;

import android.graphics.Bitmap;

import com.flx.popmovies.data.Movie;
import com.flx.popmovies.data.MovieResults;

public interface MoviesDataSource {

    interface LoadResourceCallback {

        void onMoviesLoaded(MovieResults movieResults);

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

    void getMovies(String sortOrder, LoadResourceCallback callback);

    void getMovie(long movieId, GetResourceCallback callback);

    void getSavedMovie(long movieId, GetResourceCallback callback);

    void getTrailers(long movieId, LoadResourceCallback callback);

    void getReviews(long movieId, LoadResourceCallback callback);

    void saveMovie(Movie movie, SaveResourceCallback callback);

    void savePosterImage(String path, Bitmap posterImage, SaveResourceCallback callback);

}
