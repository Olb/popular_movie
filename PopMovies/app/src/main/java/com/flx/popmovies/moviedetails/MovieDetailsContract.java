package com.flx.popmovies.moviedetails;

import android.graphics.Bitmap;

import com.flx.popmovies.BasePresenter;
import com.flx.popmovies.BaseView;
import com.flx.popmovies.data.Movie;
import com.flx.popmovies.data.Trailer;

import java.util.List;

public interface MovieDetailsContract {

    interface View extends BaseView<Presenter> {
        void setLoadingIndicator(boolean active);

        void showTitle(String title);

        void showSynopsis(String synopsis);

        void showImage(String path);

        void setFavoritesMarked(boolean isFavorite);

        void showRating(String rating);

        void showReleaseDate(String releaseDate);

        Bitmap getPosterImage();

        void showTrailers(List<Trailer> trailers);

    }

    interface Presenter extends BasePresenter {
        void markFavorite();

        void removeFavorite();

        void playTrailer(long movieId);

        void getTrailers(long movieId);

        void readReviews(long movieId);

        void start(Movie movie);
    }
}
