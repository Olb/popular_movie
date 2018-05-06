package com.flx.popmovies.data.source;

import android.util.Log;

import com.flx.popmovies.data.MovieResults;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONObject;

public class MoviesRepository implements MoviesDataSource {

    private static MoviesRepository INSTANCE = null;

    private final MoviesDataSource mMoviesRemoteDataSource;
    private final MoviesDataSource mMoviesLocaDataSource;

    private MoviesRepository(MoviesDataSource moviesRemoteDataSource, MoviesDataSource moviesLocaDataSource) {
        mMoviesRemoteDataSource = moviesRemoteDataSource;
        mMoviesLocaDataSource = moviesLocaDataSource;
    }

    public static MoviesRepository getInstance(MoviesDataSource moviesRemoteDataSource, MoviesDataSource moviesLocaDataSource) {
        if (INSTANCE == null) {
            INSTANCE = new MoviesRepository(moviesRemoteDataSource,
                    moviesLocaDataSource);
        }

        return INSTANCE;
    }

    @Override
    public void getMovies(String sortOrder, final LoadMoviesCallback callback) {

        mMoviesRemoteDataSource.getMovies(sortOrder, new LoadMoviesCallback() {
            @Override
            public void onMoviesLoaded(MovieResults movieResults) {
                callback.onMoviesLoaded(movieResults);
            }

            @Override
            public void onDataNotAvailable() {
                callback.onDataNotAvailable();
            }
        });
    }

    @Override
    public void getMovie(long movieId, GetMovieCallback callback) {
        //callback()
    }


    private MovieResults getMoviesFromJson(JSONObject jsonObject) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.setPrettyPrinting().create();
        MovieResults results = gson.fromJson(jsonObject.toString(), MovieResults.class);
        Log.d("GSON Pretty Print", gson.toJson(jsonObject).toString());
        return results;
    }
}
