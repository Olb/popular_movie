package com.flx.popmovies;

import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.test.InstrumentationRegistry;

import com.flx.popmovies.data.source.local.MovieContentProvider;
import com.flx.popmovies.data.source.local.MovieDbHelper;
import com.flx.popmovies.data.source.local.MoviesContract;

import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;

public class TestMovieContentProvider {

    private final Context mContext = InstrumentationRegistry.getTargetContext();

    @Before
    public void setUp() {
        MovieDbHelper dbHelper = new MovieDbHelper(mContext);
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        database.delete(MoviesContract.MovieEntry.TABLE_NAME, null, null);
        dbHelper.close();
    }

    @Test
    public void testProviderRegistry() {

        String packageName = mContext.getPackageName();
        String taskProviderClassName = MovieContentProvider.class.getName();
        ComponentName componentName = new ComponentName(packageName, taskProviderClassName);

        try {

            PackageManager pm = mContext.getPackageManager();

            ProviderInfo providerInfo = pm.getProviderInfo(componentName, 0);
            String actualAuthority = providerInfo.authority;

            String errorMessage =
                    "Error: MovieContentProvider registered with authority: " + actualAuthority +
                            " instead of expected authority: " + packageName;
            assertEquals(errorMessage,
                    actualAuthority,
                    packageName);

        } catch (PackageManager.NameNotFoundException e) {
            String providerNotRegistered =
                    "Error: MovieContentProvider not registered at " + mContext.getPackageName();
            fail(providerNotRegistered);
        }
    }

    private static final Uri TEST_MOVIES = MoviesContract.MovieEntry.CONTENT_URI;

    private static final Uri MOVIE_WITH_ID = TEST_MOVIES.buildUpon().appendPath("1").build();

    @Test
    public void testUriMatcher() {

        UriMatcher testMatcher = MovieContentProvider.buildUriMatcher();

        /* Test that code returned from matcher matches the expected MOVIES int */
        String errorMessage = "Error: The Movie URI was not matched correctly";
        int actualMovieMatchCode = testMatcher.match(TEST_MOVIES);
        int expectedMovieMatchCode = MovieContentProvider.MOVIES;
        assertEquals(errorMessage, actualMovieMatchCode, expectedMovieMatchCode);

        /* Test that code returned from matcher matches expected MOVIE_WITH_ID */
        String errorMessageWithId = "Error: The MOVIE_WITH_ID URI was matched incorrectly";
        int actualMovieWithIdCode = testMatcher.match(MOVIE_WITH_ID);
        int expectedMovieWithIdCode = MovieContentProvider.MOVIE_WITH_ID;
        assertEquals(errorMessageWithId, actualMovieWithIdCode, expectedMovieWithIdCode);
    }

    @Test
    public void testInsert() {

        ContentValues contentValues = buildContentValues();
        ContentResolver contentResolver = mContext.getContentResolver();

        String errorMessage = "Unable to insert item through Provider";
        Uri actualUri = contentResolver.insert(MoviesContract.MovieEntry.CONTENT_URI, contentValues);
        assert actualUri != null;
        String id = actualUri.getPathSegments().get(1);

        assertTrue(errorMessage, Integer.valueOf(id) != -1);
    }

    @Test
    public void testQuery() {

        MovieDbHelper dbHelper = new MovieDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues contentValues = buildContentValues();

        long movieRowId = db.insert(MoviesContract.MovieEntry.TABLE_NAME,
                null,
                contentValues);

        String errorMessage = "Unable to insert into db";
        assertTrue(errorMessage, movieRowId != -1);

        db.close();

        Cursor cursor = mContext.getContentResolver().query(MoviesContract.MovieEntry.CONTENT_URI,
                null,
                null,
                null,
                null);

        String queryFailed = "Query failed to return a valid cursor";
        assertTrue(queryFailed, cursor != null);
        cursor.close();
    }

    @Test
    public void testDelete() {

        MovieDbHelper dbHelper = new MovieDbHelper(InstrumentationRegistry.getTargetContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues contentValues = buildContentValues();

        long movieRowIdActual = db.insert(MoviesContract.MovieEntry.TABLE_NAME,
                null,
                contentValues);

        db.close();

        String errorMessage = "Failed to insert into db";
        assertTrue(errorMessage, movieRowIdActual != -1);

        ContentResolver contentResolver = mContext.getContentResolver();

        Uri uriToDelete = MoviesContract.MovieEntry.CONTENT_URI.buildUpon().appendPath("1").build();
        int moviesDeleted = contentResolver.delete(uriToDelete,
                null,
                null);

        String deleteError = "Failed to delete the item in the db";
        assertTrue(deleteError, moviesDeleted != 0);

    }

    private ContentValues buildContentValues() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MoviesContract.MovieEntry.COLUMN_SYNOPSIS, "Test synopsis");
        contentValues.put(MoviesContract.MovieEntry.COLUMN_POSTER_PATH, "/path");
        contentValues.put(MoviesContract.MovieEntry.COLUMN_TITLE, "Title");
        contentValues.put(MoviesContract.MovieEntry.COLUMN_RELEASE_DATE, "1/1/2011");
        contentValues.put(MoviesContract.MovieEntry.COLUMN_RATING ,"5");
        contentValues.put(MoviesContract.MovieEntry.COLUMN_ID, 1);

        return contentValues;
    }
}
