package com.flx.popmovies;
/* Followed Google MVC tutorial in creating this class */

public interface BaseView<T> {
    void setPresenter(T presenter);
}
