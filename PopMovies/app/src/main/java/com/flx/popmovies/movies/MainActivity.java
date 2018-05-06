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
import com.flx.popmovies.data.source.MoviesDataSource;
import com.flx.popmovies.data.source.local.MovieDatabase;
import com.flx.popmovies.data.source.local.MovieLocalDataSource;
import com.flx.popmovies.data.source.local.MoviesDao;
import com.flx.popmovies.data.source.remote.MoviesRemoteDataSource;
import com.flx.popmovies.moviedetails.DetailsActivity;
import com.flx.popmovies.data.Movie;
import com.flx.popmovies.data.source.MoviesRepository;
import com.flx.popmovies.utils.Constants;

import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements MovieAdapter.ListItemClickListener, MoviesContract.View {

    private static final int GRID_SPAN_COUNT = 2;

    private MoviesContract.Presenter mPresenter;
    private MovieAdapter mMovieAdapter;
    private MoviesRepository mMovieRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        VolleySingleton.getInstance(this);

        prepareMoviesRecyclerView();

        prepareMoviesPresenter();
    }

    private void prepareMoviesPresenter() {

        MoviesDao moviesDao = MovieDatabase.getInstance(this).moviesDao();
        MoviesDataSource remoteDataSource = MoviesRemoteDataSource.getInstance();
        MoviesDataSource localDataSource = MovieLocalDataSource.getInstance(moviesDao);

        MoviesRepository moviesRepository = MoviesRepository.getInstance(remoteDataSource,
                localDataSource);

        mPresenter = new MoviesPresenter(moviesRepository, this);
        mPresenter.start();
    }

    private void prepareMoviesRecyclerView() {
        GridLayoutManager layoutManager = new GridLayoutManager(this, GRID_SPAN_COUNT);

        mMovieAdapter = new MovieAdapter( this);

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
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.action_change_sort) {
            mPresenter.sortOrderChanged();
        }

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
    public void showMovieDetail(long movieId) {
        Intent startDetailsActivityIntent = new Intent(MainActivity.this, DetailsActivity.class);
        startDetailsActivityIntent.putExtra(Constants.COM_POP_MOVIE_DETAILS_INTENT, movieId);

        startActivity(startDetailsActivityIntent);
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
