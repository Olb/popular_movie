package com.flx.popmovies.data.source;

import com.flx.popmovies.data.Movie;
import com.flx.popmovies.data.MovieResults;

public class MoviesRepository implements MoviesDataSource {

    private static MoviesRepository INSTANCE = null;

    private final MoviesDataSource mMoviesRemoteDataSource;
    private final MoviesDataSource mMoviesLocaDataSource;

    private MoviesRepository(MoviesDataSource moviesRemoteDataSource, MoviesDataSource moviesLocaDataSource) {
        mMoviesRemoteDataSource = moviesRemoteDataSource;
        mMoviesLocaDataSource = moviesLocaDataSource;
    }

    public static MoviesRepository getInstance(MoviesDataSource moviesRemoteDataSource, MoviesDataSource moviesLocaDataSource) {
        if (INSTANCE == null) {
            INSTANCE = new MoviesRepository(moviesRemoteDataSource,
                    moviesLocaDataSource);
        }

        return INSTANCE;
    }

    @Override
    public void getMovies(String sortOrder, final LoadResourceCallback callback) {

        mMoviesRemoteDataSource.getMovies(sortOrder, new LoadResourceCallback() {
            @Override
            public void onMoviesLoaded(MovieResults movieResults) {
                callback.onMoviesLoaded(movieResults);
            }

            @Override
            public void onDataNotAvailable() {
                callback.onDataNotAvailable();
            }
        });
    }

    @Override
    public void getMovie(long movieId, final GetResourceCallback callback) {
        mMoviesRemoteDataSource.getMovie(movieId, new GetResourceCallback() {
            @Override
            public void onMovieLoaded(Movie movie) {
                callback.onMovieLoaded(movie);
            }

            @Override
            public void onDataNotAvailable() {
                callback.onDataNotAvailable();
            }
        });
    }

    @Override
    public void getTrailers(long movieId, LoadResourceCallback callback) {

    }

    @Override
    public void getReviews(long movieId, LoadResourceCallback callback) {

    }

}
