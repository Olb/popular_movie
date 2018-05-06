package com.flx.popmovies.data.source.remote;

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
    public void getMovies(String sortOrder, final LoadMoviesCallback callback) {
        URL url = NetworkUtils.buildMovieListUrl(sortOrder);

        String urlAsString = url.toString();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, urlAsString,
                null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                MovieResults movieResults = getMoviesFromJson(response);
                callback.onMoviesLoaded(movieResults);
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
    public void getMovie(long movieId, GetMovieCallback callback) {
        for (Movie movie : mMovieResults.getMovies()) {
            if (movie.getId() == movieId) {
                callback.onMovieLoaded(movie);
                return;
            }
            callback.onDataNotAvailable();
        }
    }

    private MovieResults getMoviesFromJson(JSONObject jsonObject) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.setPrettyPrinting().create();
        MovieResults results = gson.fromJson(jsonObject.toString(), MovieResults.class);

        return results;
    }
}
