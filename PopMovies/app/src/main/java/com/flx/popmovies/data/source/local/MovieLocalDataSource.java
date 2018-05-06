package com.flx.popmovies.data.source.local;

import com.flx.popmovies.data.source.MoviesDataSource;

public class MovieLocalDataSource implements MoviesDataSource {

    private static MovieLocalDataSource INSTANCE;

    private MoviesDao mMovieDao;

    public static MovieLocalDataSource getInstance(MoviesDao moviesDao) {
        if (INSTANCE == null) {
            INSTANCE = new MovieLocalDataSource(moviesDao);
        }

        return INSTANCE;
    }

    private MovieLocalDataSource(MoviesDao moviesDao) {
        mMovieDao = moviesDao;
    }

    @Override
    public void getMovies(String sortOrder, LoadMoviesCallback callback) {

    }

    @Override
    public void getMovie(long movieId, GetMovieCallback callback) {

    }
}
