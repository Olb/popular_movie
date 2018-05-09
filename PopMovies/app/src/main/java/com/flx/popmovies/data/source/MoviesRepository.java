package com.flx.popmovies.data.source;

import android.graphics.Bitmap;

import com.flx.popmovies.data.Movie;
import com.flx.popmovies.data.MovieResults;
import com.flx.popmovies.data.TrailerResults;

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
    public void getMovies(String sortOrder, final LoadMoviesResourceCallback callback) {

        mMoviesRemoteDataSource.getMovies(sortOrder, new LoadMoviesResourceCallback() {
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
    public void getFavorites(final LoadMoviesResourceCallback callback) {
        mMoviesLocalDataSource.getFavorites(new LoadMoviesResourceCallback() {
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
    public void removeFavorite(long movieId, final DeleteResourceCallback callback) {
        mMoviesLocalDataSource.removeFavorite(movieId, new DeleteResourceCallback() {
            @Override
            public void onResourceDeleted() {
                callback.onResourceDeleted();
            }

            @Override
            public void onDeleteFailed() {
                callback.onDeleteFailed();
            }
        });
    }

    @Override
    public void getMovie(final long movieId, final GetResourceCallback callback) {
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
    public void getTrailers(long movieId, final LoadTrailersResourceCallback callback) {
        mMoviesRemoteDataSource.getTrailers(movieId, new LoadTrailersResourceCallback() {
            @Override
            public void onTrailersLoaded(TrailerResults trailerResults) {
                callback.onTrailersLoaded(trailerResults);
            }

            @Override
            public void onDataNotAvailable() {
                callback.onDataNotAvailable();
            }
        });
    }

    @Override
    public void getReviews(long movieId, LoadMoviesResourceCallback callback) {

    }

    @Override
    public void saveMovie(Movie movie, int isFavorite, final SaveResourceCallback callback) {
        mMoviesLocalDataSource.saveMovie(movie, isFavorite, new SaveResourceCallback() {
            @Override
            public void onResourceSaved() {
                callback.onResourceSaved();
            }

            @Override
            public void onSaveFailed() {
                callback.onSaveFailed();
            }
        } );
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
