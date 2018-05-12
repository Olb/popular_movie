package com.flx.popmovies.moviedetails;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.NavUtils;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.flx.popmovies.R;
import com.flx.popmovies.data.Movie;
import com.flx.popmovies.data.Review;
import com.flx.popmovies.data.Trailer;
import com.flx.popmovies.data.source.MoviesDataSource;
import com.flx.popmovies.data.source.MoviesRepository;
import com.flx.popmovies.data.source.local.MovieLocalDataSource;
import com.flx.popmovies.data.source.remote.MoviesRemoteDataSource;
import com.flx.popmovies.util.Constants;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Objects;

public class DetailsActivity extends AppCompatActivity implements MovieDetailsContract.View, TrailersAdapter.ListItemClickListener {

    private static final String CURRENT_MOVIE = "current-movie";

    private MovieDetailsContract.Presenter mPresenter;
    private TextView mMovieTitleTextView;
    private ImageView mMoviePosterImageView;
    private TextView mMovieReleaseDateTextView;
    private TextView mMovieRatingTextView;
    private TextView mMovieSynopsisTextView;
    private TextView mRuntime;
    private ProgressBar mProgressBar;
    private Button mFavoritesButton;
    private TrailersAdapter mTrailerAdapter;
    private ReviewsAdapter mReviewsAdapter;
    private RecyclerView mMovieRecyclerView;
    private RecyclerView mReviewRecyclerView;
    private Parcelable mMovieState;
    private Parcelable mReviewState;
    private NestedScrollView mScrollView;
    Bundle mSaveInstanceState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        ActionBar actionBar = this.getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        mScrollView = findViewById(R.id.sv_detail_scroll_view);
        mMovieTitleTextView = findViewById(R.id.tv_movie_title);
        mMoviePosterImageView = findViewById(R.id.iv_movie_poster);
        mMovieReleaseDateTextView = findViewById(R.id.tv_movie_release_date);
        mMovieRatingTextView = findViewById(R.id.tv_movie_rating);
        mMovieSynopsisTextView = findViewById(R.id.tv_movie_synopsis);
        mProgressBar = findViewById(R.id.pb_loading_movie_details);
        mFavoritesButton = findViewById(R.id.bt_favorites_button);
        mRuntime = findViewById(R.id.tv_runtime);
        mRuntime.setVisibility(View.VISIBLE);

        Intent intent = getIntent();

        Movie movie;
        if (intent.hasExtra(Constants.COM_POP_MOVIE_DETAILS_INTENT)) {
            if (savedInstanceState != null && savedInstanceState.containsKey(CURRENT_MOVIE)) {
                movie = savedInstanceState.getParcelable(CURRENT_MOVIE);
                mMovieState = savedInstanceState.getParcelable("STATE1");
                mReviewState = savedInstanceState.getParcelable("STATE2");

            } else {
                movie = intent.getParcelableExtra(Constants.COM_POP_MOVIE_DETAILS_INTENT);
            }
            setupAdapters();
        } else {
            throw new RuntimeException();
        }

        prepareMovieDetailsPresenter(movie);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d("CAlling me", "Calling me");
        outState.putParcelable(CURRENT_MOVIE, mPresenter.getCurrentMovie());
        outState.putParcelable("STATE1", mMovieRecyclerView.getLayoutManager().onSaveInstanceState());
        outState.putParcelable("STATE2", mReviewRecyclerView.getLayoutManager().onSaveInstanceState());
        outState.putIntArray("ARTICLE_SCROLL_POSITION",
                new int[]{ mScrollView.getScrollX(), mScrollView.getScrollY()});
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        mSaveInstanceState = savedInstanceState;
    }

    private void setupAdapters() {

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mTrailerAdapter = new TrailersAdapter(this);

        mMovieRecyclerView = findViewById(R.id.rv_trailers);
        mMovieRecyclerView.setLayoutManager(layoutManager);
        mMovieRecyclerView.setFocusable(false);
        mMovieRecyclerView.setHasFixedSize(true);
        mMovieRecyclerView.setNestedScrollingEnabled(false);
        mMovieRecyclerView.setAdapter(mTrailerAdapter);

        LinearLayoutManager reviewLayoutManager = new LinearLayoutManager(this);
        mReviewsAdapter = new ReviewsAdapter();

        mReviewRecyclerView = findViewById(R.id.rv_reviews);
        mReviewRecyclerView.setLayoutManager(reviewLayoutManager);
        mReviewRecyclerView.setFocusable(false);
        mReviewRecyclerView.setHasFixedSize(true);
        mReviewRecyclerView.setNestedScrollingEnabled(false);
        mReviewRecyclerView.setAdapter(mReviewsAdapter);
    }

    private void prepareMovieDetailsPresenter(Movie movie) {

        MoviesDataSource remoteDataSource = MoviesRemoteDataSource.getInstance();
        MoviesDataSource localDataSource = MovieLocalDataSource.getInstance();

        MoviesRepository moviesRepository = MoviesRepository.getInstance(remoteDataSource,
                localDataSource);

        mPresenter = new MovieDetailsPresenter(moviesRepository, this);

        if (!isOnline()) {
            mPresenter.setConnectionStatus(false);
        } else {
            mPresenter.setConnectionStatus(true);
        }

        mPresenter.start(movie);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
        }
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
            if (mSaveInstanceState != null) {
                final int[] position = mSaveInstanceState.getIntArray("ARTICLE_SCROLL_POSITION");
                if(position != null)
                    mScrollView.post(new Runnable() {
                        public void run() {
                            mScrollView.scrollTo(position[0], position[1]);
                        }
                    });
            }
        }
    }

    @Override
    public void showTitle(String title) {
        mMovieTitleTextView.setText(title);
    }

    @Override
    public void showSynopsis(String synopsis) {
        mMovieSynopsisTextView.setText(synopsis);
    }

    @Override
    public void showImage(String path) {
        Picasso.get().load(path).into(mMoviePosterImageView);
    }

    @Override
    public void setFavoritesMarked(boolean isFavorite) {
        if (isFavorite) {
            mFavoritesButton.setText(getResources().getText(R.string.unmark_as_favorite));
            mFavoritesButton.setBackgroundColor(getResources().getColor(R.color.colorDetailsFavoriteSetBackground));
        } else {
            mFavoritesButton.setText(getResources().getText(R.string.mark_as_favorite));
            mFavoritesButton.setBackgroundColor(getResources().getColor(R.color.colorDetailsFavoriteBackground));
        }
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
    public Bitmap getPosterImage() {
        return ((BitmapDrawable)mMoviePosterImageView.getDrawable()).getBitmap();
    }

    @Override
    public void showTrailers(List<Trailer> trailers) {
        if (mMovieState != null) {
            mMovieRecyclerView.getLayoutManager().onRestoreInstanceState(mMovieState);
        }
        mTrailerAdapter.setNewData(trailers);
    }

    @Override
    public void showReviews(List<Review> reviews) {
        if (mReviewState != null) {
            mMovieRecyclerView.getLayoutManager().onRestoreInstanceState(mMovieState);
        }
        mReviewsAdapter.setNewData(reviews);
    }

    @Override
    public void setPresenter(MovieDetailsContract.Presenter presenter) {
        mPresenter = presenter;
    }

    public void favoritePressed(View view) {
        String favoriteButtonTitle = mFavoritesButton.getText().toString();
        if (favoriteButtonTitle.equals(getResources().getString(R.string.mark_as_favorite))) {
            mPresenter.markFavorite();
        } else {
            mPresenter.removeFavorite();
        }
    }

    @Override
    public void onListItemClick(String trailerId) {
        mPresenter.playTrailer(trailerId);
    }

    @Override
    public void playMedia(Uri appUri, Uri webUir) {
        Intent appIntent = new Intent(Intent.ACTION_VIEW, appUri);
        Intent webIntent = new Intent(Intent.ACTION_VIEW, webUir);

        try {
            startActivity(appIntent);
        } catch (ActivityNotFoundException e) {
            startActivity(webIntent);
        }
    }

    @Override
    public void setRuntime(String runtime) {
        mRuntime.setText(runtime);
        mRuntime.setVisibility(View.VISIBLE);
    }

    @Override
    public void setRuntimeUnavailable() {
        mRuntime.setVisibility(View.GONE);
    }

    @Override
    public void setOffline() {
        findViewById(R.id.fl_online_contents).setVisibility(View.GONE);
    }

    @Override
    public void setOnline() {
        findViewById(R.id.fl_offline_contents).setVisibility(View.GONE);
    }
}
