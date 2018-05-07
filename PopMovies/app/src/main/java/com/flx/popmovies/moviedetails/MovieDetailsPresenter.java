package com.flx.popmovies.moviedetails;

import android.graphics.Bitmap;
import android.util.Log;

import com.flx.popmovies.data.Movie;
import com.flx.popmovies.data.source.MoviesDataSource;
import com.flx.popmovies.data.source.MoviesRepository;
import com.flx.popmovies.utils.StringUtil;

public class MovieDetailsPresenter implements MovieDetailsContract.Presenter {

    private final MovieDetailsContract.View mMovieDetailsView;
    private MoviesRepository mMoviesRepository;

    private final String RATING_DENOMINATOR = "/10";

    private Movie mCurrentMovie;

    public MovieDetailsPresenter(MoviesRepository moviesRepository, MovieDetailsContract.View movieDetailsView) {
        this.mMovieDetailsView = movieDetailsView;
        this.mMoviesRepository = moviesRepository;
        this.mMovieDetailsView.setPresenter(this);
    }

    @Override
    public void markFavorite() {

        mMoviesRepository.saveMovie(mCurrentMovie, new MoviesDataSource.SaveResourceCallback() {
            @Override
            public void onResourceSaved() {
                mMovieDetailsView.setFavoritesMarked(true);
            }

            @Override
            public void onSaveFailed() {
                throw new UnsupportedOperationException("Couldn't save");
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

            }
        });
    }

        @Override
    public void playTrailer(int movieId) {

    }

    @Override
    public void readReviews(int movieId) {

    }

    private void getMovie(long movieId) {
        mMovieDetailsView.setLoadingIndicator(true);

        mMoviesRepository.getMovie(movieId, new MoviesDataSource.GetResourceCallback() {
            @Override
            public void onMovieLoaded(Movie movie) {
                mCurrentMovie = movie;
                mMovieDetailsView.showRating(StringUtil.ratingWithDenominator(movie.getVoteAverage(), RATING_DENOMINATOR));
                mMovieDetailsView.showReleaseDate(StringUtil.stringToDateReport(movie.getReleaseDate()));
                mMovieDetailsView.showSynopis(movie.getOverview());
                mMovieDetailsView.showTitle(movie.getTitle());
                mMovieDetailsView.showImage(StringUtil.getPosterPath(movie.getPosterPath()));
                mMovieDetailsView.setLoadingIndicator(false);
            }

            @Override
            public void onDataNotAvailable() {
                mMovieDetailsView.showMovieNotAvailable();
                mMovieDetailsView.setLoadingIndicator(false);
            }
        });
    }

    @Override
    public void start(long movieId) {
        getMovie(movieId);
    }

    @Override
    public void getMovie() {
            mMoviesRepository.getMovie(mCurrentMovie.getId(), new MoviesDataSource.GetResourceCallback() {
                @Override
                public void onMovieLoaded(Movie movie) {
                    mMovieDetailsView.tempShowMovie(movie);
                }

                @Override
                public void onDataNotAvailable() {
                    Log.d("Unable to get movie", "Failed tro retreive from db");
                }
            });
    }

    @Override
    public void start() {
        /*
         * Don't use this method on MovieDetail.
         */
        throw new RuntimeException();
    }
}
