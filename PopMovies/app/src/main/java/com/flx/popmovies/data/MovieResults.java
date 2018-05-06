package com.flx.popmovies.data;


import com.google.gson.annotations.SerializedName;

public class MovieResults {

    private long page;
    @SerializedName("total_results")
    private long totalResults;
    @SerializedName("total_page")
    private long totalPages;
    @SerializedName("results")
    private Movie[] movies;

    public long getPage() { return page; }
    public void setPage(long value) { this.page = value; }

    public long getTotalResults() { return totalResults; }
    public void setTotalResults(long value) { this.totalResults = value; }

    public long getTotalPages() { return totalPages; }
    public void setTotalPages(long value) { this.totalPages = value; }

    public Movie[] getMovies() { return movies; }
    public void setMovies(Movie[] value) { this.movies = value; }
}
