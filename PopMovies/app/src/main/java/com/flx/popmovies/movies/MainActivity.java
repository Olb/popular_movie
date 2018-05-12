package com.flx.popmovies.movies;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.flx.popmovies.R;
import com.flx.popmovies.data.Movie;
import com.flx.popmovies.data.source.MoviesDataSource;
import com.flx.popmovies.data.source.MoviesRepository;
import com.flx.popmovies.data.source.local.MovieLocalDataSource;
import com.flx.popmovies.data.source.remote.MoviesRemoteDataSource;
import com.flx.popmovies.moviedetails.DetailsActivity;
import com.flx.popmovies.util.Constants;

import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements MovieAdapter.ListItemClickListener, MoviesContract.View {

    private static final int GRID_SPAN_COUNT = 2;
    private static final String LAST_SELECTED = "last-selected";
    private static final String LAST_ACTION_ITEM = "last-action";
    private static final String LAST_FAVORITE_ITEM = "last-favorite";
    private static final String LAST_RECYCLER_STATE = "RECYCLERSTATE";
    private MoviesContract.Presenter mPresenter;
    private MovieAdapter mMovieAdapter;
    private Menu mMenu;
    private String mLastSelected;
    private String mLastAction;
    private String mLastFavorite;
    RecyclerView mMovieRecyclerView;
    private Parcelable mState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        prepareMoviesPresenter();

        prepareMoviesRecyclerView();

        if (!isOnline()) {
            mPresenter.setOffline();
        } else {
            if (savedInstanceState != null) {
                if (savedInstanceState.containsKey(LAST_SELECTED)) {
                     mLastSelected = savedInstanceState.getString(LAST_SELECTED);
                     mLastAction = savedInstanceState.getString(LAST_ACTION_ITEM);
                     mLastFavorite = savedInstanceState.getString(LAST_FAVORITE_ITEM);
                     mState = savedInstanceState.getParcelable(LAST_RECYCLER_STATE);
                }
            } else {
                mPresenter.start();
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        String lastMenuItem = mPresenter.getLastMenuItemSelected();
        String actionItem = mMenu.findItem(R.id.action_change_sort).getTitle().toString();
        String popularItem = mMenu.findItem(R.id.action_favorites).getTitle().toString();
        outState.putString(LAST_SELECTED, lastMenuItem);
        outState.putString(LAST_ACTION_ITEM, actionItem);
        outState.putString(LAST_FAVORITE_ITEM, popularItem);
        outState.putParcelable(LAST_RECYCLER_STATE, mMovieRecyclerView.getLayoutManager().onSaveInstanceState());
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPresenter.refreshContent();
    }

    private void prepareMoviesPresenter() {
        MoviesDataSource remoteDataSource = MoviesRemoteDataSource.getInstance();
        MoviesDataSource localDataSource = MovieLocalDataSource.getInstance();
        MoviesRepository moviesRepository = MoviesRepository.getInstance(remoteDataSource,
                localDataSource);

        mPresenter = new MoviesPresenter(moviesRepository, this);
    }

    private void prepareMoviesRecyclerView() {
        mMovieAdapter = new MovieAdapter(this);

        GridLayoutManager layoutManager = new GridLayoutManager(this, GRID_SPAN_COUNT);
        mMovieRecyclerView = findViewById(R.id.rv_movie_tiles);

        mMovieRecyclerView.setLayoutManager(layoutManager);
        mMovieRecyclerView.setHasFixedSize(true);
        mMovieRecyclerView.setAdapter(mMovieAdapter);
    }

    @Override
    public void onListItemClick(long movieId) {
        mPresenter.movieSelected(movieId);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        mMenu = menu;
        if (mLastSelected != null) {
            mPresenter.menuItemSelected(mLastSelected, mLastFavorite, mLastAction);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        mPresenter.menuItemSelected(item.getTitle().toString(),
                mMenu.findItem(R.id.action_change_sort).getTitle().toString(), mMenu.findItem(R.id.action_favorites).getTitle().toString());

        return true;
    }

    @Override
    public void setLoadingIndicator(boolean active) {
        if (active) {
            this.findViewById(R.id.pb_loading_movies).setVisibility(View.VISIBLE);
        } else {
            this.findViewById(R.id.pb_loading_movies).setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void showMovies(List<Movie> movieList) {
        findViewById(R.id.tv_no_results_or_error_message).setVisibility(View.INVISIBLE);
        mMovieAdapter.setNewData(movieList);
        if (mState != null) {
            mMovieRecyclerView.getLayoutManager().onRestoreInstanceState(mState);
        }
    }

    @Override
    public void showError() {
        TextView errorMessageTextView = this.findViewById(R.id.tv_no_results_or_error_message);
        errorMessageTextView.setText(R.string.error_message);
        errorMessageTextView.setVisibility(View.VISIBLE);
    }

    @Override
    public void showOffline() {
        TextView errorMessageTextView = this.findViewById(R.id.tv_no_results_or_error_message);
        errorMessageTextView.setText(R.string.no_online_connection);
        errorMessageTextView.setVisibility(View.VISIBLE);
        Toast.makeText(this, R.string.see_favorites_prompt, Toast.LENGTH_LONG).show();
    }

    @Override
    public void showNoResults() {
        ((TextView)findViewById(R.id.tv_no_results_or_error_message)).setText(getText(R.string.no_results_message));
        findViewById(R.id.tv_no_results_or_error_message).setVisibility(View.VISIBLE);
    }

    @Override
    public void showMovieDetail(Movie movie) {
        Intent startDetailsActivityIntent = new Intent(MainActivity.this, DetailsActivity.class);
        startDetailsActivityIntent.putExtra(Constants.COM_POP_MOVIE_DETAILS_INTENT, movie);

        startActivity(startDetailsActivityIntent);
    }

    @Override
    public void setTitleForSortOrder(int resourceTitle) {
        mMenu.findItem(R.id.action_change_sort).setTitle(getResources().getString(resourceTitle));
    }

    @Override
    public void setTitleForFavoritesAction(int resourceTitle) {
        mMenu.findItem(R.id.action_favorites).setTitle(getResources().getString(resourceTitle));
    }

    @Override
    public void setPresenter(MoviesContract.Presenter presenter) {
        mPresenter = presenter;
    }

    private boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = Objects.requireNonNull(cm).getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
}
