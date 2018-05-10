package com.flx.popmovies.data;


import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class MovieResults implements Parcelable {

    private long page;
    @SerializedName("total_results")
    private long totalResults;
    @SerializedName("total_page")
    private long totalPages;
    @SerializedName("results")
    private Movie[] movies;

    public MovieResults(Parcel in) {
        page = in.readLong();
        totalResults = in.readLong();
        totalPages = in.readLong();
        movies = in.createTypedArray(Movie.CREATOR);
    }

    public static final Creator<MovieResults> CREATOR = new Creator<MovieResults>() {
        @Override
        public MovieResults createFromParcel(Parcel in) {
            return new MovieResults(in);
        }

        @Override
        public MovieResults[] newArray(int size) {
            return new MovieResults[size];
        }
    };

    public MovieResults() {

    }

    public long getPage() { return page; }
    public void setPage(long value) { this.page = value; }

    public long getTotalResults() { return totalResults; }
    public void setTotalResults(long value) { this.totalResults = value; }

    public long getTotalPages() { return totalPages; }
    public void setTotalPages(long value) { this.totalPages = value; }

    public Movie[] getMovies() { return movies; }
    public void setMovies(Movie[] value) { this.movies = value; }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(page);
        parcel.writeLong(totalResults);
        parcel.writeLong(totalPages);
        parcel.writeTypedArray(movies, i);
    }
}
