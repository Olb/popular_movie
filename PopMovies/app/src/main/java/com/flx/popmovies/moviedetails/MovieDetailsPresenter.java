package com.flx.popmovies.moviedetails;

import com.flx.popmovies.data.Movie;
import com.flx.popmovies.data.source.MoviesDataSource;
import com.flx.popmovies.data.source.MoviesRepository;
import com.flx.popmovies.utils.StringUtil;

public class MovieDetailsPresenter implements MovieDetailsContract.Presenter {

    private final MovieDetailsContract.View mMovieDetailsView;
    private MoviesRepository mMoviesRepository;

    private final String RATING_DENOMINATOR = "/10";

    private long mCurrentMovieId;

    public MovieDetailsPresenter(MoviesRepository moviesRepository, MovieDetailsContract.View movieDetailsView) {
        this.mMovieDetailsView = movieDetailsView;
        this.mMoviesRepository = moviesRepository;
        this.mMovieDetailsView.setPresenter(this);
    }

    @Override
    public void markFavorite() {

        // TODO: Add methods to MoviesDataSource to save
        // this movie id.
        // 1) Need to retrieve the movie from id
        // 2) Add save method to data source
        // 3) Save method
        // 4) Call SaveResourceCallback if success
        // 4) Error if not
        mMoviesRepository.setMovie(mCurrentMovieId, new MoviesDataSource.SaveResourceDataSource);
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
                mCurrentMovieId = movie.getId();
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
    public void start() {
        /*
         * Don't use this method on MovieDetail.
         */
        throw new RuntimeException();
    }
}
