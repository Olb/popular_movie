package com.flx.popmovies.utils;

import android.os.AsyncTask;

import com.flx.popmovies.interfaces.AsyncTaskCompleteListener;
import com.flx.popmovies.models.MovieResults;

import java.io.IOException;
import java.net.URL;

public class FetchMoviesTask extends AsyncTask<String, Void, MovieResults> {

    private AsyncTaskCompleteListener<MovieResults> listener;

    public FetchMoviesTask(AsyncTaskCompleteListener<MovieResults> listener) {
        this.listener = listener;
    }

    @Override
    protected MovieResults doInBackground(String... params) {

        String sortParam = params[0];

        URL movieUrl = NetworkUtils.buildMovieListUrl(sortParam);

        try {
            String jsonMovieResponse = NetworkUtils.getResponseFromHttpUrl(movieUrl);
            return Converter.fromJsonString(jsonMovieResponse);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(MovieResults movieResults) {
        listener.onTaskComplete(movieResults);
    }
}
