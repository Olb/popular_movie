package com.flx.popmovies.activities;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.flx.popmovies.R;
import com.flx.popmovies.adapters.MovieAdapter;
import com.flx.popmovies.interfaces.AsyncTaskCompleteListener;
import com.flx.popmovies.models.Movie;
import com.flx.popmovies.models.MovieResults;
import com.flx.popmovies.utils.Constants;
import com.flx.popmovies.utils.FetchMoviesTask;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements MovieAdapter.ListItemClickListener, AsyncTaskCompleteListener {

    private static final int GRID_SPAN_COUNT = 2;
    private static final String SORT_POPULARITY = "popular";
    private static final String SORT_TOP_RATED = "top_rated";

    private RecyclerView mMovieRecyclerView;
    private ProgressBar mLoadingIndicator;
    private TextView mErrorMessageTextView;
    private String mSortParam;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mMovieRecyclerView = findViewById(R.id.rv_movie_tiles);
        mLoadingIndicator = findViewById(R.id.pb_loading_movies);
        mErrorMessageTextView = findViewById(R.id.tv_no_results_or_error_message);

        GridLayoutManager layoutManager = new GridLayoutManager(this, GRID_SPAN_COUNT);

        mMovieRecyclerView.setLayoutManager(layoutManager);
        mMovieRecyclerView.setHasFixedSize(true);

        mSortParam = SORT_POPULARITY;
        loadMovies(mSortParam);
    }

    private void loadMovies(String sortParam) {
        if (!isOnline()) {
            mErrorMessageTextView.setText(R.string.no_online_connection);
            mErrorMessageTextView.setVisibility(View.VISIBLE);
            mMovieRecyclerView.setAdapter(null);
            return;
        }

        mErrorMessageTextView.setText(R.string.no_results_message);
        mErrorMessageTextView.setVisibility(View.INVISIBLE);
        mLoadingIndicator.setVisibility(View.VISIBLE);

        new FetchMoviesTask(this).execute(sortParam);
    }

    @Override
    public void onTaskComplete(Object result) {
        MovieResults results = (MovieResults) result;

        this.findViewById(R.id.pb_loading_movies).setVisibility(View.INVISIBLE);

        TextView errorMessageTextView = this.findViewById(R.id.tv_no_results_or_error_message);
        if (results == null) {
            errorMessageTextView.setText(R.string.error_message);
            errorMessageTextView.setVisibility(View.VISIBLE);
            return;
        } else if (results.getMovies().length == 0) {
            errorMessageTextView.setVisibility(View.VISIBLE);
        }

        List<Movie> movieList = Arrays.asList(results.getMovies());
        MovieAdapter mMovieAdapter = new MovieAdapter(movieList, this);
        ((RecyclerView) this.findViewById(R.id.rv_movie_tiles)).setAdapter(mMovieAdapter);
    }

    @Override
    public void onListItemClick(Movie clickedMovie) {
        Context context = MainActivity.this;
        Class detailsActivity = DetailsActivity.class;

        Intent startDetailsActivityIntent = new Intent(context, detailsActivity);
        startDetailsActivityIntent.putExtra(Constants.COM_POPMOVIE_DETAILS_INTENT, clickedMovie);

        startActivity(startDetailsActivityIntent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_change_sort) {
            if (mSortParam.equals(SORT_POPULARITY)) {
                mSortParam = SORT_TOP_RATED;
                item.setTitle(R.string.menu_popular_sort);
            } else {
                mSortParam = SORT_POPULARITY;
                item.setTitle(R.string.menu_top_rated_sort);
            }

            loadMovies(mSortParam);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = Objects.requireNonNull(cm).getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
}
