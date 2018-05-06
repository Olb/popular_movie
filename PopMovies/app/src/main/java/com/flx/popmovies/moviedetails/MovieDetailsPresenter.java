package com.flx.popmovies.moviedetails;

import com.flx.popmovies.data.Movie;
import com.flx.popmovies.data.source.MoviesDataSource;
import com.flx.popmovies.data.source.MoviesRepository;

public class MovieDetailsPresenter implements MovieDetailsContract.Presenter {

    private final MovieDetailsContract.View mMovieDetailsView;
    private MoviesRepository mMoviesRepository;

    public MovieDetailsPresenter(MoviesRepository moviesRepository, MovieDetailsContract.View movieDetailsView) {
        this.mMovieDetailsView = movieDetailsView;
        this.mMoviesRepository = moviesRepository;
        this.mMovieDetailsView.setPresenter(this);
    }

    @Override
    public void markFavorite(int movieId) {

    }

    @Override
    public void playTrailer(int movieId) {

    }

    @Override
    public void readReviews(int movieId) {

    }

    private void getMovie(long movieId) {
        mMovieDetailsView.setLoadingIndicator(true);

        mMoviesRepository.getMovie(movieId, new MoviesDataSource.GetMovieCallback() {
            @Override
            public void onMovieLoaded(Movie movie) {
                mMovieDetailsView.showRating(String.valueOf(movie.getPopularity()));
                mMovieDetailsView.showReleaseDate(movie.getReleaseDate());
                mMovieDetailsView.showSynopis(movie.getOverview());
                mMovieDetailsView.showTitle(movie.getTitle());
                mMovieDetailsView.showImage();
                mMovieDetailsView.setLoadingIndicator(false);
            }

            @Override
            public void onDataNotAvailable() {
                mMovieDetailsView.showMovieNotAvailable();
                mMovieDetailsView.setLoadingIndicator(false);
            }
        });
    }

    public void start(long movieId) {
        getMovie(movieId);
    }

    @Override
    public void start() {
        /*
         * Don't use this method on MovieDetail.
         */
        throw new RuntimeException();
    }
}
