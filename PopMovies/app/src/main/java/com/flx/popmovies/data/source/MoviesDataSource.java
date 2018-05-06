package com.flx.popmovies.data.source;

import com.flx.popmovies.data.MovieResults;
import com.flx.popmovies.data.Movie;

public interface MoviesDataSource {

    interface LoadResourceCallback {

        void onMoviesLoaded(MovieResults movieResults);

        void onDataNotAvailable();
    }

    interface GetResourceCallback {

        void onMovieLoaded(Movie movie);

        void onDataNotAvailable();
    }

    void getMovies(String sortOrder, LoadResourceCallback callback);

    void getMovie(long movieId, GetResourceCallback callback);

    void getTrailers(long movieId, LoadResourceCallback callback);

    void getReviews(long movieId, LoadResourceCallback callback);

}
