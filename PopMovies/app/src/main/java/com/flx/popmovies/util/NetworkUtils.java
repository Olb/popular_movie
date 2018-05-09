package com.flx.popmovies.util;

import android.net.Uri;
import android.support.annotation.Nullable;

import com.flx.popmovies.BuildConfig;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public class NetworkUtils {

    private static final String API_KEY = BuildConfig.API_KEY;
    private static final String MOVIE_DB_BASE_URL = "https://api.themoviedb.org/3/movie";
    private static final String TRAILER_PATH = "videos";
    private static final String REVIEW_PATH = "reviews";
    private final static String API_KEY_PARAM = "api_key";

    public static final String IMAGES_BASE_URL = "https://image.tmdb.org/t/p/w185";

    /**
     * Returns a URL given the sort params for a list of movies
     *
     * @param sortParam String sort order
     * @return URL url built using sortParam
     */
    public static URL buildMovieListUrl(String sortParam) {
        Uri builtUri = Uri.parse(MOVIE_DB_BASE_URL).buildUpon()
                .appendPath(sortParam)
                .appendQueryParameter(API_KEY_PARAM, API_KEY)
                .build();

        return getUrlFromUri(builtUri);
    }

    public static URL buildTrailerListUrl(String movieId) {
        Uri builtUri = Uri.parse(MOVIE_DB_BASE_URL).buildUpon()
                .appendPath(movieId)
                .appendPath(TRAILER_PATH)
                .appendQueryParameter(API_KEY_PARAM, API_KEY)
                .build();

        return getUrlFromUri(builtUri);
    }

    public static URL buildReviewListUrl(String movieId) {
        Uri builtUri = Uri.parse(MOVIE_DB_BASE_URL).buildUpon()
                .appendPath(movieId)
                .appendPath(REVIEW_PATH)
                .build();

        return getUrlFromUri(builtUri);
    }

    @Nullable
    private static URL getUrlFromUri(Uri builtUri) {
        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
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
