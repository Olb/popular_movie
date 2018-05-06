package com.flx.popmovies.data.source;

import com.flx.popmovies.data.MovieResults;
import com.flx.popmovies.data.Movie;

public interface MoviesDataSource {

    interface LoadMoviesCallback {

        void onMoviesLoaded(MovieResults movieResults);

        void onDataNotAvailable();
    }

    interface GetMovieCallback {

        void onMovieLoaded(Movie movie);

        void onDataNotAvailable();
    }

    void getMovies(String sortOrder, LoadMoviesCallback callback);

    void getMovie(long movieId, GetMovieCallback callback);

}
