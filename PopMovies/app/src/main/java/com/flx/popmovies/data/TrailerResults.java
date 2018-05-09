package com.flx.popmovies.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TrailerResults {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("results")
    @Expose
    private Trailer[] results = null;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Trailer[] getResults() {
        return results;
    }

    public void setResults(Trailer[] results) {
        this.results = results;
    }

}
