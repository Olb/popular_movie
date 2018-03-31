package com.flx.popmovies.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MovieResults {
    private long page;
    private long totalResults;
    private long totalPages;
    private Movie[] movies;

    @JsonProperty("page")
    public long getPage() { return page; }
    @JsonProperty("page")
    public void setPage(long value) { this.page = value; }

    @JsonProperty("total_results")
    public long getTotalResults() { return totalResults; }
    @JsonProperty("total_results")
    public void setTotalResults(long value) { this.totalResults = value; }

    @JsonProperty("total_pages")
    public long getTotalPages() { return totalPages; }
    @JsonProperty("total_pages")
    public void setTotalPages(long value) { this.totalPages = value; }

    @JsonProperty("results")
    public Movie[] getMovies() { return movies; }
    @JsonProperty("results")
    public void setMovies(Movie[] value) { this.movies = value; }

}
