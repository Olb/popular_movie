package com.flx.popmovies.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Movie implements Serializable{
    private long voteCount;
    private long id;
    private boolean video;
    private double voteAverage;
    private String title;
    private double popularity;
    private String posterPath;
    private String originalTitle;
    private long[] genreIDS;
    private String backdropPath;
    private boolean adult;
    private String overview;
    private String releaseDate;

    @JsonProperty("vote_count")
    public long getVoteCount() { return voteCount; }
    @JsonProperty("vote_count")
    public void setVoteCount(long value) { this.voteCount = value; }

    @JsonProperty("id")
    public long getID() { return id; }
    @JsonProperty("id")
    public void setID(long value) { this.id = value; }

    @JsonProperty("video")
    public boolean getVideo() { return video; }
    @JsonProperty("video")
    public void setVideo(boolean value) { this.video = value; }

    @JsonProperty("vote_average")
    public double getVoteAverage() { return voteAverage; }
    @JsonProperty("vote_average")
    public void setVoteAverage(double value) { this.voteAverage = value; }

    @JsonProperty("title")
    public String getTitle() { return title; }
    @JsonProperty("title")
    public void setTitle(String value) { this.title = value; }

    @JsonProperty("popularity")
    public double getPopularity() { return popularity; }
    @JsonProperty("popularity")
    public void setPopularity(double value) { this.popularity = value; }

    @JsonProperty("poster_path")
    public String getPosterPath() { return posterPath; }
    @JsonProperty("poster_path")
    public void setPosterPath(String value) { this.posterPath = value; }

    @JsonProperty("original_title")
    public String getOriginalTitle() { return originalTitle; }
    @JsonProperty("original_title")
    public void setOriginalTitle(String value) { this.originalTitle = value; }

    @JsonProperty("genre_ids")
    public long[] getGenreIDS() { return genreIDS; }
    @JsonProperty("genre_ids")
    public void setGenreIDS(long[] value) { this.genreIDS = value; }

    @JsonProperty("backdrop_path")
    public String getBackdropPath() { return backdropPath; }
    @JsonProperty("backdrop_path")
    public void setBackdropPath(String value) { this.backdropPath = value; }

    @JsonProperty("adult")
    public boolean getAdult() { return adult; }
    @JsonProperty("adult")
    public void setAdult(boolean value) { this.adult = value; }

    @JsonProperty("overview")
    public String getOverview() { return overview; }
    @JsonProperty("overview")
    public void setOverview(String value) { this.overview = value; }

    @JsonProperty("release_date")
    public String getReleaseDate() { return releaseDate; }
    @JsonProperty("release_date")
    public void setReleaseDate(String value) { this.releaseDate = value; }
}
