package com.flx.popmovies.utils;

import android.net.Uri;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public class NetworkUtils {

    private static final String TAG = NetworkUtils.class.getSimpleName();

    private static final String MOVIEDB_BASE_URL = "https://api.themoviedb.org/3/discover/movie";
    public static final String IMAGES_BASE_URL = "https://image.tmdb.org/t/p/w185";

    final static String API_KEY_PARAM = "api_key";
    final static String SORT_PARAM = "sort_by";

    /**
     * Returns a URL given the sort params for a list of movies
     *
     * @param sortParam String sort order
     * @param apiKey String themoviedb.org API key
     * @return URL url built using sortParam
     */
    public static URL buildMovieListUrl(String sortParam, String apiKey) {

        Uri builtUri = Uri.parse(MOVIEDB_BASE_URL).buildUpon()
                .appendQueryParameter(API_KEY_PARAM, apiKey)
                .appendQueryParameter(SORT_PARAM, sortParam)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        Log.v(TAG, "Built URI " + url);

        return url;
    }

    /**
     * Gets the list of movies from themovieDB API
     *
     * @param url URL url for the Discover API
     * @return String response from API
     * @throws IOException
     */
    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }
}
