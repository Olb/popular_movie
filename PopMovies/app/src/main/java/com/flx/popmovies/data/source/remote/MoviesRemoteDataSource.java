package com.flx.popmovies.data.source.remote;

import android.graphics.Bitmap;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.flx.popmovies.VolleySingleton;
import com.flx.popmovies.data.Movie;
import com.flx.popmovies.data.MovieResults;
import com.flx.popmovies.data.TrailerResults;
import com.flx.popmovies.data.source.MoviesDataSource;
import com.flx.popmovies.util.NetworkUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONObject;

import java.net.URL;

public class MoviesRemoteDataSource implements MoviesDataSource {

    private static MoviesRemoteDataSource INSTANCE;

    private MovieResults mMovieResults;

    public static MoviesRemoteDataSource getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new MoviesRemoteDataSource();
        }

        return INSTANCE;
    }

    private MoviesRemoteDataSource() {}


    @Override
    public void getMovies(final String sortOrder, final LoadMoviesResourceCallback callback) {

        URL url = NetworkUtils.buildMovieListUrl(sortOrder);

        String urlAsString = url.toString();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, urlAsString,
                null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                mMovieResults = getMoviesFromJson(response);
                callback.onMoviesLoaded(mMovieResults);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                callback.onDataNotAvailable();
            }
        });

        VolleySingleton.getInstance().addtoRequestQueue(jsonObjectRequest);


    }

    @Override
    public void getFavorites(LoadMoviesResourceCallback callback) {
        throw new UnsupportedOperationException("Remote resource cannot load from db");
    }

    @Override
    public void removeFavorite(long movieId, DeleteResourceCallback callback) {

    }

    @Override
    public void getMovie(long movieId, GetResourceCallback callback) {
        for (Movie movie : mMovieResults.getMovies()) {
            if (movie.getId() == movieId) {
                callback.onMovieLoaded(movie);
                return;
            }
            callback.onDataNotAvailable();
        }
    }

    @Override
    public void getSavedMovie(long movieId, GetResourceCallback callback) {
        throw new UnsupportedOperationException("Remote resource cannot load from db");
    }

    @Override
    public void getTrailers(long movieId, final LoadTrailersResourceCallback callback) {
        URL url = NetworkUtils.buildTrailerListUrl(String.valueOf(movieId));

        String urlAsString = url.toString();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, urlAsString,
                null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                callback.onTrailersLoaded(getTrailersFromJson(response));
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                callback.onDataNotAvailable();
            }
        });

        VolleySingleton.getInstance().addtoRequestQueue(jsonObjectRequest);
    }

    @Override
    public void getReviews(long movieId, LoadMoviesResourceCallback callback) {

    }

    @Override
    public void saveMovie(Movie movie, int isFavorite, SaveResourceCallback callback) {
        throw new UnsupportedOperationException("Remote resource cannot load from db");
    }

    @Override
    public void savePosterImage(String path, Bitmap posterImage, SaveResourceCallback callback) {
        throw new UnsupportedOperationException("Remote resource cannot load from db");
    }

    private MovieResults getMoviesFromJson(JSONObject jsonObject) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.setPrettyPrinting().create();

        return gson.fromJson(jsonObject.toString(), MovieResults.class);
    }

    private TrailerResults getTrailersFromJson(JSONObject jsonObject) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.setPrettyPrinting().create();

        return gson.fromJson(jsonObject.toString(), TrailerResults.class);
    }
}
