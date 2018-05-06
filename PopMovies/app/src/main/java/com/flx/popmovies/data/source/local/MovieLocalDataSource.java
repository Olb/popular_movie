package com.flx.popmovies.data.source.local;

import com.flx.popmovies.data.source.MoviesDataSource;

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
    public void getTrailers(long movieId, LoadResourceCallback callback) {

    }

    @Override
    public void getReviews(long movieId, LoadResourceCallback callback) {

    }
}
