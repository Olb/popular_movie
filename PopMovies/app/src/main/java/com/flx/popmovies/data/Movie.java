package com.flx.popmovies.data;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class Movie implements Parcelable {

    @SerializedName("id")
    private long mId;

    @SerializedName("vote_count")
    private long voteCount;

    @SerializedName("video")
    private boolean video;

    @SerializedName("vote_average")
    private double voteAverage;

    private String title;

    @SerializedName("popularity")
    private double popularity;

    @SerializedName("poster_path")
    private String posterPath;

    @SerializedName("original_title")
    private String originalTitle;

    @SerializedName("backdrop_path")
    private String backdropPath;

    private boolean adult;

    private String overview;

    @SerializedName("release_date")
    private String releaseDate;

    private String runTime;

    private int isFavorite = 0;

    Movie(Parcel in) {
        mId = in.readLong();
        voteCount = in.readLong();
        video = in.readByte() != 0;
        voteAverage = in.readDouble();
        title = in.readString();
        popularity = in.readDouble();
        posterPath = in.readString();
        originalTitle = in.readString();
        backdropPath = in.readString();
        adult = in.readByte() != 0;
        overview = in.readString();
        releaseDate = in.readString();
        isFavorite = in.readInt();
    }

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    public Movie() {

    }

    public long getId() {
        return mId;
    }

    public void setId(long mId) {
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

    public int getIsFavorite() {
        return isFavorite;
    }

    public void setIsFavorite(int favorite) {
        isFavorite = favorite;
    }

    public String getRunTime() {
        if (runTime == null) {
            return "";
        }
        return runTime;
    }

    public void setRunTime(String runTime) {
        this.runTime = runTime;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(mId);
        parcel.writeLong(voteCount);
        parcel.writeByte((byte) (video ? 1 : 0));
        parcel.writeDouble(voteAverage);
        parcel.writeString(title);
        parcel.writeDouble(popularity);
        parcel.writeString(posterPath);
        parcel.writeString(originalTitle);
        parcel.writeString(backdropPath);
        parcel.writeByte((byte) (adult ? 1 : 0));
        parcel.writeString(overview);
        parcel.writeString(releaseDate);
        parcel.writeInt(isFavorite);
    }
}
