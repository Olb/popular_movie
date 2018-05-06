package com.flx.popmovies.data;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

@Entity(tableName = "movies")
public class Movie {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "id")
    @SerializedName("id")
    private long mId;

    @NonNull
    @ColumnInfo(name = "vote_count")
    @SerializedName("vote_count")
    private long voteCount;

    @NonNull
    @ColumnInfo(name = "video")
    @SerializedName("video")
    private boolean video;

    @NonNull
    @ColumnInfo(name = "vote_average")
    @SerializedName("vote_average")
    private double voteAverage;

    @NonNull
    @ColumnInfo(name = "title")
    private String title;

    @NonNull
    @ColumnInfo(name = "popularity")
    private double popularity;

    @NonNull
    @ColumnInfo(name = "poster_path")
    @SerializedName("poster_path")
    private String posterPath;

    @NonNull
    @ColumnInfo(name = "original_title")
    @SerializedName("original_title")
    private String originalTitle;

    @NonNull
    @ColumnInfo(name = "backdrop_path")
    @SerializedName("backdrop_path")
    private String backdropPath;

    @NonNull
    @ColumnInfo(name = "adult")
    private boolean adult;

    @NonNull
    @ColumnInfo(name = "overview")
    private String overview;

    @NonNull
    @ColumnInfo(name = "release_date")
    @SerializedName("release_date")
    private String releaseDate;

    public Movie() { }

    @NonNull
    public long getId() {
        return mId;
    }

    public void setId(@NonNull long mId) {
        this.mId = mId;
    }

    public long getVoteCount() { return voteCount; }
    public void setVoteCount(long value) { this.voteCount = value; }
    
    public boolean getVideo() { return video; }
    public void setVideo(boolean value) { this.video = value; }

    public double getVoteAverage() { return voteAverage; }
    public void setVoteAverage(double value) { this.voteAverage = value; }

    public String getTitle() { return title; }
    public void setTitle(String value) { this.title = value; }

    public double getPopularity() { return popularity; }
    public void setPopularity(double value) { this.popularity = value; }

    public String getPosterPath() { return posterPath; }
    public void setPosterPath(String value) { this.posterPath = value; }

    public String getOriginalTitle() { return originalTitle; }
    public void setOriginalTitle(String value) { this.originalTitle = value; }

    public String getBackdropPath() { return backdropPath; }
    public void setBackdropPath(String value) { this.backdropPath = value; }

    public boolean getAdult() { return adult; }
    public void setAdult(boolean value) { this.adult = value; }

    public String getOverview() { return overview; }
    public void setOverview(String value) { this.overview = value; }

    public String getReleaseDate() { return releaseDate; }
    public void setReleaseDate(String value) { this.releaseDate = value; }
}
