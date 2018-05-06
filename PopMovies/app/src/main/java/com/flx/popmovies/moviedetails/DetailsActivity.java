package com.flx.popmovies.moviedetails;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.flx.popmovies.R;
import com.flx.popmovies.data.source.MoviesDataSource;
import com.flx.popmovies.data.source.MoviesRepository;
import com.flx.popmovies.data.source.local.MovieLocalDataSource;
import com.flx.popmovies.data.source.remote.MoviesRemoteDataSource;
import com.flx.popmovies.utils.Constants;
import com.squareup.picasso.Picasso;

import java.util.Objects;

public class DetailsActivity extends AppCompatActivity implements MovieDetailsContract.View {

    private static final String TAG = DetailsActivity.class.getSimpleName();

    private MovieDetailsContract.Presenter mPresenter;

    private TextView mMovieTitleTextView;
    private ImageView mMoviePosterImageView;
    private TextView mMovieReleaseDateTextView;
    private TextView mMovieRatingTextView;
    private TextView mMovieSynopsisTextView;
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        mMovieTitleTextView = findViewById(R.id.tv_movie_title);
        mMoviePosterImageView = findViewById(R.id.iv_movie_poster);
        mMovieReleaseDateTextView = findViewById(R.id.tv_movie_release_date);
        mMovieRatingTextView = findViewById(R.id.tv_movie_rating);
        mMovieSynopsisTextView = findViewById(R.id.tv_movie_synopsis);
        mProgressBar = findViewById(R.id.pb_loading_movie_details);

        if (!checkConnectivity()) {
            return;
        }

        Intent intent = getIntent();

        if (intent.hasExtra(Constants.COM_POP_MOVIE_DETAILS_INTENT)) {
           prepareMovieDetailsPresenter(intent.getLongExtra(Constants.COM_POP_MOVIE_DETAILS_INTENT, 0));
        } else {
            throw new RuntimeException();
        }
    }

    private void prepareMovieDetailsPresenter(long movieId) {

        MoviesDataSource remoteDataSource = MoviesRemoteDataSource.getInstance();
        MoviesDataSource localDataSource = MovieLocalDataSource.getInstance();

        MoviesRepository moviesRepository = MoviesRepository.getInstance(remoteDataSource,
                localDataSource);

        mPresenter = new MovieDetailsPresenter(moviesRepository, this);
        mPresenter.start(movieId);
    }

    private boolean checkConnectivity() {
        if (!isOnline()) {
            Toast.makeText(DetailsActivity.this, R.string.no_online_connection, Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
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
        NetworkInfo netInfo = Objects.requireNonNull(cm).getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    @Override
    public void setLoadingIndicator(boolean active) {
        if (active) {
            mProgressBar.setVisibility(View.VISIBLE);
        } else {
            mProgressBar.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void showTitle(String title) {
        mMovieTitleTextView.setText(title);
    }

    @Override
    public void showSynopis(String synopsis) {
        mMovieSynopsisTextView.setText(synopsis);
    }

    @Override
    public void showImage(String path) {
        Picasso.get().load(path).into(mMoviePosterImageView);
    }

    @Override
    public void setFavoritesMarked(boolean isFavorite) {

    }

    @Override
    public void showRating(String rating) {
        mMovieRatingTextView.setText(rating);
    }

    @Override
    public void showReleaseDate(String releaseDate) {
        mMovieReleaseDateTextView.setText(releaseDate);
    }

    @Override
    public void showMovieNotAvailable() {

    }

    @Override
    public void setPresenter(MovieDetailsContract.Presenter presenter) {

    }
}
