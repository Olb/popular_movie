package com.flx.popmovies.moviedetails;

import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;

import com.flx.popmovies.R;
import com.flx.popmovies.data.Movie;
import com.flx.popmovies.data.ReviewResults;
import com.flx.popmovies.data.TrailerResults;
import com.flx.popmovies.data.source.MoviesDataSource;
import com.flx.popmovies.data.source.MoviesRepository;
import com.flx.popmovies.util.ContextSingleton;
import com.flx.popmovies.util.MoviesUtils;

import java.util.Arrays;

public class MovieDetailsPresenter implements MovieDetailsContract.Presenter {

    private final MovieDetailsContract.View mMovieDetailsView;
    private MoviesRepository mMoviesRepository;

    private final String RATING_DENOMINATOR = "/10";
    private final int FAVORITE = 1;

    private Movie mCurrentMovie;

    public MovieDetailsPresenter(MoviesRepository moviesRepository, MovieDetailsContract.View movieDetailsView) {
        this.mMovieDetailsView = movieDetailsView;
        this.mMoviesRepository = moviesRepository;
        this.mMovieDetailsView.setPresenter(this);
    }

    @Override
    public void markFavorite() {

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
                Log.d("Image Saved", "Image saved.");
            }

            @Override
            public void onSaveFailed() {
                Log.d("Image NOT Saved", "Image failed saved.");
                throw new RuntimeException();
            }
        });
    }

    @Override
    public void removeFavorite() {
        mMoviesRepository.removeFavorite(mCurrentMovie.getId(), new MoviesDataSource.DeleteResourceCallback() {
            @Override
            public void onResourceDeleted() {
                mMovieDetailsView.setFavoritesMarked(false);
            }

            @Override
            public void onDeleteFailed() {
                // TODO: Show delete on success or not
                Log.d("DeleteFail", "Deletion Failed");
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
        Log.d("TrailersPresenter", "Getting trailers.");

        mMoviesRepository.getTrailers(movieId, new MoviesDataSource.LoadTrailersResourceCallback() {
            @Override
            public void onTrailersLoaded(TrailerResults trailerResults) {
                Log.d("TrailersPresenter", "Call back");
                mMovieDetailsView.showTrailers(Arrays.asList(trailerResults.getResults()));
            }

            @Override
            public void onDataNotAvailable() {
                Log.d("Oops", "No Trailers");
                throw new RuntimeException();
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
                Log.d("Oops", "No reviews");
                throw new RuntimeException();
            }
        });
    }

    private void loadMovie(final Movie movie) {
        Log.d("TrailersPresenter", "Getting movis.");

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
                String min = ContextSingleton.getInstance(null).getContext().getResources().getString(R.string.min);
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
    public void start() {
        /*
         * Don't use this method on MovieDetail.
         */
        throw new RuntimeException();
    }
}
