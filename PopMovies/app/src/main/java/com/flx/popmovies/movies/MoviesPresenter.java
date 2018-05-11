package com.flx.popmovies.movies;

import android.content.Context;

import com.flx.popmovies.PopMovies;
import com.flx.popmovies.R;
import com.flx.popmovies.data.Movie;
import com.flx.popmovies.data.MovieResults;
import com.flx.popmovies.data.source.MoviesDataSource;
import com.flx.popmovies.data.source.MoviesRepository;

import java.util.Arrays;
import java.util.List;

public class MoviesPresenter implements MoviesContract.Presenter {

    private static final String SORT_POPULARITY = "popular";
    private static final String SORT_TOP_RATED = "top_rated";

    private final MoviesContract.View mMoviesView;
    private final MoviesRepository mMoviesRepository;
    private int mLastMenuItemSelected;
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

    private void sortOrderChanged(String sortBy) {
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
        Context context = PopMovies.getAppContext();
        if (menuItemTitle.equals(context.getResources().getString(R.string.menu_favorites))) {
            mLastMenuItemSelected = 1;
            showFavorites();
            if (currentMenuItemSortTitle.equals(context.getResources().getString(R.string.menu_top_rated_sort))) {
                mMoviesView.setTitleForFavoritesAction(R.string.menu_popular_sort);
            } else if (currentMenuItemSortTitle.equals(context.getResources().getString(R.string.menu_popular_sort))){
                mMoviesView.setTitleForFavoritesAction(R.string.menu_top_rated_sort);
            }
        } else if (menuItemTitle.equals(context.getResources().getString(R.string.menu_popular_sort))) {
            mLastMenuItemSelected = 2;
            sortOrderChanged(SORT_POPULARITY);
            mMoviesView.setTitleForSortOrder(R.string.menu_top_rated_sort);
            if (!favoritesActionTitle.equals(context.getResources().getString(R.string.menu_favorites))) {
                mMoviesView.setTitleForFavoritesAction(R.string.menu_favorites);
            }

        } else if (menuItemTitle.equals(context.getResources().getString(R.string.menu_top_rated_sort))) {
            mLastMenuItemSelected = 3;
            sortOrderChanged(SORT_TOP_RATED);
            mMoviesView.setTitleForSortOrder(R.string.menu_popular_sort);
            if (!favoritesActionTitle.equals(context.getResources().getString(R.string.menu_favorites))) {
                mMoviesView.setTitleForFavoritesAction(R.string.menu_favorites);
            }
        }
    }

    @Override
    public void refreshContent() {
        switch (mLastMenuItemSelected) {
            case 0:
                break;
            case 1:
                showFavorites();
                break;
            case 2:
                sortOrderChanged(SORT_POPULARITY);
                break;
            case 3:
                sortOrderChanged(SORT_TOP_RATED);
                break;
            default:
                throw new UnsupportedOperationException("Invalid menu selection item");
        }
    }

    @Override
    public String getLastMenuItemSelected() {
        Context context = PopMovies.getAppContext();

        switch (mLastMenuItemSelected) {
            case 0:
                return context.getResources().getString(R.string.menu_favorites);
            case 1:
                return context.getResources().getString(R.string.menu_favorites);
            case 2:
                return context.getResources().getString(R.string.menu_popular_sort);
            case 3:
                return context.getResources().getString(R.string.menu_top_rated_sort);
            default:
                throw new UnsupportedOperationException("Invalid menu selection item");
        }
    }
}
