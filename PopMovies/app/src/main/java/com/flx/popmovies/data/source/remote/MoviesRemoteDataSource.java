package com.flx.popmovies.data.source.remote;

import android.graphics.Bitmap;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.flx.popmovies.VolleySingleton;
import com.flx.popmovies.data.Movie;
import com.flx.popmovies.data.MovieResults;
import com.flx.popmovies.data.source.MoviesDataSource;
import com.flx.popmovies.utils.NetworkUtils;
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
    public void getMovies(String sortOrder, final LoadResourceCallback callback) {
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

    }

    @Override
    public void getTrailers(long movieId, LoadResourceCallback callback) {

    }

    @Override
    public void getReviews(long movieId, LoadResourceCallback callback) {

    }

    @Override
    public void saveMovie(Movie movie, SaveResourceCallback callback) {

    }

    @Override
    public void savePosterImage(String path, Bitmap posterImage, SaveResourceCallback callback) {

    }

    private MovieResults getMoviesFromJson(JSONObject jsonObject) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.setPrettyPrinting().create();

        return gson.fromJson(jsonObject.toString(), MovieResults.class);
    }
}
