package com.flx.popmovies.movies;

import com.flx.popmovies.BasePresenter;
import com.flx.popmovies.BaseView;
import com.flx.popmovies.data.Movie;

import java.util.List;

interface MoviesContract {

    interface View extends BaseView<Presenter> {

        void setLoadingIndicator(boolean active);

        void showMovies(List<Movie> movieResults);

        void showError();

        void showOffline();

        void showNoResults();

        void showMovieDetail(Movie movie);

        void setTitleForSortOrder(int resourceTitle);

        void setTitleForFavoritesAction(int resourceTitle);

    }

    interface Presenter extends BasePresenter {

        void movieSelected(long movieId);

        void setOffline();

        void menuItemSelected(String menuItemTitle, String currentMenuItemSortTitle, String favoritesActionTitle);

        void refreshContent();

        String getLastMenuItemSelected();
    }
}
