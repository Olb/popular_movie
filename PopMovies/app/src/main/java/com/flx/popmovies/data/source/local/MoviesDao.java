package com.flx.popmovies.data.source.local;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.flx.popmovies.data.Movie;

import java.util.List;

@Dao
public interface MoviesDao {

    @Query("SELECT * FROM Movies")
    List<Movie> getMovies();

    @Query("SELECT * FROM Movies WHERE id = :movieId")
    Movie getMovieById(String movieId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(Movie... movies);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertMovie(Movie movie);

    @Query("DELETE FROM Movies")
    void deleteMovies();
}
