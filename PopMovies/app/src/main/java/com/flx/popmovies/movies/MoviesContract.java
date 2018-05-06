package com.flx.popmovies.movies;

import com.flx.popmovies.BasePresenter;
import com.flx.popmovies.BaseView;
import com.flx.popmovies.data.Movie;

import java.util.List;

public interface MoviesContract {

    interface View extends BaseView<Presenter> {

        void setLoadingIndicator(boolean active);

        void showMovies(List<Movie> movieResults);

        void showError();

        void showMovieDetail(long movieId);

    }

    interface Presenter extends BasePresenter {

        void sortOrderChanged();

        void movieSelected(long movieId);

    }
}