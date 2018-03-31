package com.flx.popmovies.activities;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.ParseException;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.flx.popmovies.R;
import com.flx.popmovies.models.Movie;
import com.flx.popmovies.utils.Constants;
import com.flx.popmovies.utils.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class DetailsActivity extends AppCompatActivity {

    private static final String TAG = DetailsActivity.class.getSimpleName();

    private TextView mMovieTitleTextView;
    private ImageView mMoviePosterImageView;
    private TextView mMovieReleaseDateTextView;
    private TextView mMovieRatingTextView;
    private TextView mMovieSynopsisTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        mMovieTitleTextView = (TextView) findViewById(R.id.tv_movie_title);
        mMoviePosterImageView = (ImageView) findViewById(R.id.iv_movie_poster);
        mMovieReleaseDateTextView = (TextView) findViewById(R.id.tv_movie_release_date);
        mMovieRatingTextView = (TextView) findViewById(R.id.tv_movie_rating);
        mMovieSynopsisTextView = (TextView) findViewById(R.id.tv_movie_synopsis);

        if (!checkConnectivity()) {
            return;
        }

        Intent intent = getIntent();

        if (intent.hasExtra(Constants.COM_POPMOVIE_DETAILS_INTENT)) {
            Movie movie = (Movie) intent.getSerializableExtra(Constants.COM_POPMOVIE_DETAILS_INTENT);
            setLayoutData(movie);
        }
    }

    private boolean checkConnectivity() {
        if (!isOnline()) {
            Toast.makeText(DetailsActivity.this, R.string.no_online_connection, Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    private void setLayoutData(Movie movie) {
        Log.d(TAG, "Movie Title: " + movie.getOriginalTitle());
        mMovieTitleTextView.setText(movie.getTitle());

        String posterPath = NetworkUtils.IMAGES_BASE_URL + movie.getPosterPath();
        Picasso.get().load(posterPath).into(mMoviePosterImageView);

        String releaseDateAsString = String.valueOf(stringToDateReport(movie.getReleaseDate()));
        mMovieReleaseDateTextView.setText(releaseDateAsString);
        mMovieRatingTextView.setText(movie.getVoteAverage() + "/10");
        mMovieSynopsisTextView.setText(movie.getOverview());
    }

    private String stringToDateReport(String s){
        SimpleDateFormat format = new SimpleDateFormat("yyyy");
        Date date = null;

        try {
            date = format.parse(s);
        } catch (ParseException e) {
            //you should do a real logging here
            e.printStackTrace();
            return "";
        } catch (java.text.ParseException e) {
            e.printStackTrace();
            return "";
        }

        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        return String.valueOf(calendar.get(Calendar.YEAR));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Toast.makeText(DetailsActivity.this, R.string.not_implemented_toast, Toast.LENGTH_SHORT).show();
        return super.onOptionsItemSelected(item);
    }

    private boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
}
