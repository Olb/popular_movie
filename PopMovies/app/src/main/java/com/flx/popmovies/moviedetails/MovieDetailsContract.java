package com.flx.popmovies.moviedetails;

import com.flx.popmovies.BasePresenter;
import com.flx.popmovies.BaseView;

public interface MovieDetailsContract {

    interface View extends BaseView<Presenter> {
        void setLoadingIndicator(boolean active);

        void showTitle(String title);

        void showSynopis(String synopsis);

        void showImage();

        void setFavoritesMarked(boolean isFavorite);

        void showRating(String rating);

        void showReleaseDate(String releaseDate);

        void showMovieNotAvailable();

    }

    interface Presenter extends BasePresenter {
        void markFavorite(int movieId);

        void playTrailer(int movieId);

        void readReviews(int movieId);
    }
}
