package com.flx.popmovies.data.source.local;

public class DatabaseTasks {

    public static final String ACTION_GET_MOVIE_FROM_DB = "get-movie";
    public static final String ACTION_GET_FAVORITES_FROM_DB = "get-movies";
    public static final String ACTION_SAVE_MOVIE_TO_DB = "save-movie";
    public static final String ACTION_SAVE_IMAGE_TO_DB = "save-image";
    public static final String ACTION_REMOVE_FAVORITE = "remove-favorite";

    public static void execute(String action) {
        if (ACTION_GET_MOVIE_FROM_DB.equals(action)) {

        } else if (ACTION_GET_FAVORITES_FROM_DB.equals(action)) {

        } else if (ACTION_SAVE_MOVIE_TO_DB.equals(action)) {

        } else if (ACTION_SAVE_IMAGE_TO_DB.equals(action)) {

        } else if (ACTION_REMOVE_FAVORITE.equals(action)) {

        }
    }


}
