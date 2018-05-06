package com.flx.popmovies.utils;

import android.net.Uri;
import android.util.Log;

import com.flx.popmovies.BuildConfig;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public class NetworkUtils {

    private static final String TAG = NetworkUtils.class.getSimpleName();

    private static final String API_KEY = BuildConfig.API_KEY;

    private static final String MOVIEDB_BASE_URL = "https://api.themoviedb.org/3/movie";
    public static final String IMAGES_BASE_URL = "https://image.tmdb.org/t/p/w185";

    private final static String API_KEY_PARAM = "api_key";
    private final static String SORT_PARAM = "sort_by";

    /**
     * Returns a URL given the sort params for a list of movies
     *
     * @param sortParam String sort order
     * @return URL url built using sortParam
     */
    public static URL buildMovieListUrl(String sortParam) {
        String tempSortParam = "popular";
        Uri builtUri = Uri.parse(MOVIEDB_BASE_URL).buildUpon()
                .appendPath(sortParam)
                .appendQueryParameter(API_KEY_PARAM, API_KEY)
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
     * @throws IOException throws on failed attempt to get input stream
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
