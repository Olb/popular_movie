package com.flx.popmovies.movies;

import com.flx.popmovies.data.Movie;
import com.flx.popmovies.data.MovieResults;
import com.flx.popmovies.data.source.MoviesDataSource;
import com.flx.popmovies.data.source.MoviesRepository;

import java.util.Arrays;
import java.util.List;

public class MoviesPresenter implements MoviesContract.Presenter {

    private final MoviesContract.View mMoviesView;
    private MoviesRepository mMoviesRepository;

    private static final String SORT_POPULARITY = "popular";
    private static final String SORT_TOP_RATED = "top_rated";

    private String mCurrentSortOrder;

    public MoviesPresenter(MoviesRepository mMoviesRepository, MoviesContract.View mMoviesView) {
        this.mMoviesView = mMoviesView;
        this.mMoviesRepository = mMoviesRepository;
        mMoviesView.setPresenter(this);
    }

    private void loadMovies(String sortOrder) {

        mMoviesView.setLoadingIndicator(true);

        mMoviesRepository.getMovies(sortOrder, new MoviesDataSource.LoadMoviesCallback() {
            @Override
            public void onMoviesLoaded(MovieResults movieResults) {
                List<Movie> movieList = Arrays.asList(movieResults.getMovies());

                mMoviesView.showMovies(movieList);
                mMoviesView.setLoadingIndicator(false);
            }

            @Override
            public void onDataNotAvailable() {
                mMoviesView.showError();
                mMoviesView.setLoadingIndicator(false);
            }
        });
    }

    @Override
    public void start() {
        mCurrentSortOrder = SORT_POPULARITY;
        loadMovies(mCurrentSortOrder);
    }

    @Override
    public void sortOrderChanged() {
        if (mCurrentSortOrder.equals(SORT_POPULARITY)) {
            mCurrentSortOrder = SORT_TOP_RATED;
        } else {
            mCurrentSortOrder = SORT_POPULARITY;
        }

        loadMovies(mCurrentSortOrder);
    }

    @Override
    public void movieSelected(long movieId) {
        mMoviesView.showMovieDetail(movieId);
    }
}
