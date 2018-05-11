package com.flx.popmovies.moviedetails;

import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;

import com.flx.popmovies.PopMovies;
import com.flx.popmovies.R;
import com.flx.popmovies.data.Movie;
import com.flx.popmovies.data.ReviewResults;
import com.flx.popmovies.data.TrailerResults;
import com.flx.popmovies.data.source.MoviesDataSource;
import com.flx.popmovies.data.source.MoviesRepository;
import com.flx.popmovies.util.MoviesUtils;

import java.util.Arrays;

public class MovieDetailsPresenter implements MovieDetailsContract.Presenter {

    private final static String TAG = MovieDetailsPresenter.class.getSimpleName();
    private final MovieDetailsContract.View mMovieDetailsView;
    private final MoviesRepository mMoviesRepository;

    private Movie mCurrentMovie;

    MovieDetailsPresenter(MoviesRepository moviesRepository, MovieDetailsContract.View movieDetailsView) {
        this.mMovieDetailsView = movieDetailsView;
        this.mMoviesRepository = moviesRepository;
        this.mMovieDetailsView.setPresenter(this);
    }

    @Override
    public void markFavorite() {

        int FAVORITE = 1;
        mMoviesRepository.saveMovie(mCurrentMovie, FAVORITE, new MoviesDataSource.SaveResourceCallback() {
            @Override
            public void onResourceSaved() {
                mMovieDetailsView.setFavoritesMarked(true);
            }

            @Override
            public void onSaveFailed() {
                throw new RuntimeException();
            }
        });

        Bitmap posterImageBitmap = mMovieDetailsView.getPosterImage();
        mMoviesRepository.savePosterImage(mCurrentMovie.getPosterPath(), posterImageBitmap, new MoviesDataSource.SaveResourceCallback() {
            @Override
            public void onResourceSaved() {
                Log.d(TAG, "Image saved.");
            }

            @Override
            public void onSaveFailed() {
                throw new RuntimeException();
            }
        });
    }

    @Override
    public void removeFavorite() {
        mMoviesRepository.removeFavorite(mCurrentMovie.getId(), new MoviesDataSource.DeleteResourceCallback() {
            @Override
            public void onResourceDeleted() {
                mCurrentMovie.setIsFavorite(0);
                mMovieDetailsView.setFavoritesMarked(false);
            }

            @Override
            public void onDeleteFailed() {
                throw new RuntimeException();
            }
        });
    }

    @Override
    public void playTrailer(String trailerId) {
        Uri webUri = Uri.parse("http://www.youtube.com/watch?v=" + trailerId);
        Uri appUri = Uri.parse("vnd.youtube://" + trailerId);
        mMovieDetailsView.playMedia(appUri, webUri);
    }

    @Override
    public void getTrailers(long movieId) {
        mMoviesRepository.getTrailers(movieId, new MoviesDataSource.LoadTrailersResourceCallback() {
            @Override
            public void onTrailersLoaded(TrailerResults trailerResults) {
                mMovieDetailsView.showTrailers(Arrays.asList(trailerResults.getResults()));
            }

            @Override
            public void onDataNotAvailable() {
                Log.d(TAG, "No Trailers");
            }
        });
    }

    @Override
    public void getReviews(long movieId) {
        mMoviesRepository.getReviews(movieId, new MoviesDataSource.LoadReviewsResourceCallback() {
            @Override
            public void onReviewsLoaded(ReviewResults reviewResults) {
                mMovieDetailsView.showReviews(Arrays.asList(reviewResults.getResults()));
            }

            @Override
            public void onDataNotAvailable() {
                Log.d(TAG, "No reviews");
            }
        });
    }

    private void loadMovie(final Movie movie) {
        mMoviesRepository.getMovie(movie.getId(), new MoviesDataSource.GetResourceCallback() {
            @Override
            public void onMovieLoaded(Movie movie) {
                mCurrentMovie = movie;
                getRuntime(movie);
                setMovieUI(movie);
            }

            @Override
            public void onDataNotAvailable() {
                getRuntime(movie);
                setMovieUI(mCurrentMovie);
            }
        });
    }

    private void setMovieUI(Movie movie) {
        mCurrentMovie = movie;
        String RATING_DENOMINATOR = "/10";
        mMovieDetailsView.showRating(MoviesUtils.ratingWithDenominator(movie.getVoteAverage(), RATING_DENOMINATOR));
        mMovieDetailsView.showReleaseDate(MoviesUtils.stringToDateReport(movie.getReleaseDate()));
        mMovieDetailsView.showSynopsis(movie.getOverview());
        mMovieDetailsView.showTitle(movie.getTitle());
        mMovieDetailsView.showImage(MoviesUtils.getPosterPath(movie.getPosterPath()));
        if (movie.getIsFavorite() != 0) {
            mMovieDetailsView.setFavoritesMarked(true);
        } else {
            mMovieDetailsView.setFavoritesMarked(false);
        }

        mMovieDetailsView.setLoadingIndicator(false);
    }

    private void getRuntime(Movie movie) {
        if ((mCurrentMovie.getRunTime() != null) && !mCurrentMovie.getRunTime().isEmpty()) {
            mMovieDetailsView.setRuntime(mCurrentMovie.getRunTime());
            mMovieDetailsView.setLoadingIndicator(false);
            return;
        }
        mMoviesRepository.getMovieRuntime(movie, new MoviesDataSource.GetResourceRunTimeCallback() {
            @Override
            public void onResourceRetrieved(String runtime) {
                String min = PopMovies.getAppContext().getResources().getString(R.string.min);
                mCurrentMovie.setRunTime(runtime + min);
                mMovieDetailsView.setRuntime(mCurrentMovie.getRunTime());
                mMovieDetailsView.setLoadingIndicator(false);
            }

            @Override
            public void onDataNotAvailable() {
                mMovieDetailsView.setRuntimeUnavailable();
                mMovieDetailsView.setLoadingIndicator(false);
            }
        });
    }

    @Override
    public void start(Movie movie) {
        mMovieDetailsView.setLoadingIndicator(true);
        mCurrentMovie = movie;
        loadMovie(movie);
        getTrailers(movie.getId());
        getReviews(movie.getId());
    }

    @Override
    public void setConnectionStatus(boolean connectionStatus) {
        if (connectionStatus) {
            mMovieDetailsView.setOnline();
        } else {
            mMovieDetailsView.setOffline();
        }
    }

    @Override
    public Movie getCurrentMovie() {
        return mCurrentMovie;
    }

    @Override
    public void start() {
        /*
         * Don't call start on MovieDetail.
         */
        throw new RuntimeException();
    }
}
