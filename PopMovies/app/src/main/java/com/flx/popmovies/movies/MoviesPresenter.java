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

    private List<Movie> movieList;

    MoviesPresenter(MoviesRepository mMoviesRepository, MoviesContract.View mMoviesView) {
        this.mMoviesView = mMoviesView;
        this.mMoviesRepository = mMoviesRepository;
        mMoviesView.setPresenter(this);
    }

    private void loadMovies(final String sortOrder) {

        mMoviesView.setLoadingIndicator(true);

        mMoviesRepository.getMovies(sortOrder, new MoviesDataSource.LoadMoviesResourceCallback() {
            @Override
            public void onMoviesLoaded(MovieResults movieResults) {
                movieList = Arrays.asList(movieResults.getMovies());
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
        loadMovies(SORT_POPULARITY);
    }

    @Override
    public void sortOrderChanged(String sortBy) {
        loadMovies(sortBy);
    }

    @Override
    public void movieSelected(long movieId) {
        for (Movie movie : movieList) {
            if (movie.getId() == movieId) {
                mMoviesView.showMovieDetail(movie);
                return;
            }
        }
    }

    @Override
    public void showFavorites() {
        mMoviesRepository.getFavorites(new MoviesDataSource.LoadMoviesResourceCallback() {
            @Override
            public void onMoviesLoaded(MovieResults movieResults) {
                movieList = Arrays.asList(movieResults.getMovies());
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
    public void setOffline() {
        mMoviesView.showError();
    }
}
