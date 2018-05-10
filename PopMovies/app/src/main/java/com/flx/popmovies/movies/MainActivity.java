package com.flx.popmovies.movies;

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
import android.widget.TextView;

import com.flx.popmovies.R;
import com.flx.popmovies.VolleySingleton;
import com.flx.popmovies.data.Movie;
import com.flx.popmovies.data.source.MoviesDataSource;
import com.flx.popmovies.data.source.MoviesRepository;
import com.flx.popmovies.data.source.local.MovieLocalDataSource;
import com.flx.popmovies.data.source.remote.MoviesRemoteDataSource;
import com.flx.popmovies.moviedetails.DetailsActivity;
import com.flx.popmovies.util.Constants;
import com.flx.popmovies.util.ContextSingleton;

import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements MovieAdapter.ListItemClickListener, MoviesContract.View {

    private static final int GRID_SPAN_COUNT = 2;

    private MoviesContract.Presenter mPresenter;
    private MovieAdapter mMovieAdapter;
    private Menu mMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        VolleySingleton.getInstance(this);
        ContextSingleton.getInstance(this);

        prepareMoviesPresenter();

        prepareMoviesRecyclerView();

    }

    private void prepareMoviesPresenter() {

        MoviesDataSource remoteDataSource = MoviesRemoteDataSource.getInstance();
        MoviesDataSource localDataSource = MovieLocalDataSource.getInstance();

        MoviesRepository moviesRepository = MoviesRepository.getInstance(remoteDataSource,
                localDataSource);

        mPresenter = new MoviesPresenter(moviesRepository, this);

        mPresenter.start();
    }

    private void prepareMoviesRecyclerView() {
        GridLayoutManager layoutManager = new GridLayoutManager(this, GRID_SPAN_COUNT);

        mMovieAdapter = new MovieAdapter(this);

        RecyclerView mMovieRecyclerView = findViewById(R.id.rv_movie_tiles);
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
        mMovieAdapter.setNewData(movieList);
    }

    @Override
    public void showError() {
        TextView errorMessageTextView = this.findViewById(R.id.tv_no_results_or_error_message);
        errorMessageTextView.setText(R.string.error_message);
        errorMessageTextView.setVisibility(View.VISIBLE);
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
