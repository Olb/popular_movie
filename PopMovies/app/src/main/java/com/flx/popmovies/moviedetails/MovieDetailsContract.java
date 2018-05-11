package com.flx.popmovies.moviedetails;

import android.graphics.Bitmap;
import android.net.Uri;

import com.flx.popmovies.BasePresenter;
import com.flx.popmovies.BaseView;
import com.flx.popmovies.data.Movie;
import com.flx.popmovies.data.Review;
import com.flx.popmovies.data.Trailer;

import java.util.List;

interface MovieDetailsContract {

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

        void showReviews(List<Review> reviews);

        void playMedia(Uri appUri, Uri webUir);

        void setRuntime(String runtime);

        void setRuntimeUnavailable();

        void setOffline();

        void setOnline();
    }

    interface Presenter extends BasePresenter {
        void markFavorite();

        void removeFavorite();

        void playTrailer(String trailerId);

        void getTrailers(long movieId);

        void getReviews(long movieId);

        void start(Movie movie);

        void setConnectionStatus(boolean connectionStatus);

        Movie getCurrentMovie();
    }
}
