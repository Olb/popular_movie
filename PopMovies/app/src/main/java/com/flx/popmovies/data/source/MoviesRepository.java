package com.flx.popmovies.data.source;

import android.graphics.Bitmap;

import com.flx.popmovies.data.Movie;
import com.flx.popmovies.data.MovieResults;

public class MoviesRepository implements MoviesDataSource {

    private static MoviesRepository INSTANCE = null;

    private final MoviesDataSource mMoviesRemoteDataSource;
    private final MoviesDataSource mMoviesLocalDataSource;

    private MoviesRepository(MoviesDataSource moviesRemoteDataSource, MoviesDataSource moviesLocalDataSource) {
        mMoviesRemoteDataSource = moviesRemoteDataSource;
        mMoviesLocalDataSource = moviesLocalDataSource;
    }

    public static MoviesRepository getInstance(MoviesDataSource moviesRemoteDataSource, MoviesDataSource moviesLocalDataSource) {
        if (INSTANCE == null) {
            INSTANCE = new MoviesRepository(moviesRemoteDataSource,
                    moviesLocalDataSource);
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
    public void getSavedMovie(long movieId, final GetResourceCallback callback) {
        mMoviesLocalDataSource.getSavedMovie(movieId, new GetResourceCallback() {
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

    @Override
    public void saveMovie(Movie movie,  final SaveResourceCallback callback) {
        mMoviesLocalDataSource.saveMovie(movie, new SaveResourceCallback() {
            @Override
            public void onResourceSaved() {
                callback.onResourceSaved();
            }

            @Override
            public void onSaveFailed() {
                callback.onSaveFailed();
            }
        });
    }

    @Override
    public void savePosterImage(String path, Bitmap posterImage, final SaveResourceCallback callback) {
        mMoviesLocalDataSource.savePosterImage(path, posterImage, new SaveResourceCallback() {
            @Override
            public void onResourceSaved() {
                callback.onResourceSaved();
            }

            @Override
            public void onSaveFailed() {
                callback.onSaveFailed();
            }
        });
    }

}
