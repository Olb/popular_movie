package com.flx.popmovies.movies;

import android.content.Context;

import com.flx.popmovies.R;
import com.flx.popmovies.data.Movie;
import com.flx.popmovies.data.MovieResults;
import com.flx.popmovies.data.source.MoviesDataSource;
import com.flx.popmovies.data.source.MoviesRepository;
import com.flx.popmovies.util.ContextSingleton;

import java.util.Arrays;
import java.util.List;

public class MoviesPresenter implements MoviesContract.Presenter {

    private final MoviesContract.View mMoviesView;
    private MoviesRepository mMoviesRepository;

    private static final String SORT_POPULARITY = "popular";
    private static final String SORT_TOP_RATED = "top_rated";

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

    private void showFavorites() {
        mMoviesRepository.getFavorites(new MoviesDataSource.LoadMoviesResourceCallback() {
            @Override
            public void onMoviesLoaded(MovieResults movieResults) {
                movieList = Arrays.asList(movieResults.getMovies());
                if (movieList.size() == 0) {
                    mMoviesView.showMovies(null);
                    mMoviesView.showNoResults();
                } else {
                    mMoviesView.showMovies(movieList);
                }
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
        mMoviesView.showOffline();
    }

    @Override
    public void menuItemSelected(String menuItemTitle, String currentMenuItemSortTitle, String favoritesActionTitle) {
        Context context = ContextSingleton.getInstance(null).getContext();
        if (menuItemTitle.equals(context.getResources().getString(R.string.menu_favorites))) {
            showFavorites();
            if (currentMenuItemSortTitle.equals(context.getResources().getString(R.string.menu_top_rated_sort))) {
                mMoviesView.setTitleForFavoritesAction(R.string.menu_popular_sort);
            } else if (currentMenuItemSortTitle.equals(context.getResources().getString(R.string.menu_popular_sort))){
                mMoviesView.setTitleForFavoritesAction(R.string.menu_top_rated_sort);
            }
        } else if (menuItemTitle.equals(context.getResources().getString(R.string.menu_popular_sort))) {
            sortOrderChanged(SORT_POPULARITY);
            mMoviesView.setTitleForSortOrder(R.string.menu_top_rated_sort);
            if (!favoritesActionTitle.equals(context.getResources().getString(R.string.menu_favorites))) {
                mMoviesView.setTitleForFavoritesAction(R.string.menu_favorites);
            }

        } else if (menuItemTitle.equals(context.getResources().getString(R.string.menu_top_rated_sort))) {
            sortOrderChanged(SORT_TOP_RATED);
            mMoviesView.setTitleForSortOrder(R.string.menu_popular_sort);
            if (!favoritesActionTitle.equals(context.getResources().getString(R.string.menu_favorites))) {
                mMoviesView.setTitleForFavoritesAction(R.string.menu_favorites);
            }
        }
    }
}
