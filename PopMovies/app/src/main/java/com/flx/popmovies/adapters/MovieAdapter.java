package com.flx.popmovies.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.flx.popmovies.R;
import com.flx.popmovies.models.Movie;
import com.flx.popmovies.utils.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieAdapterViewHolder> {

    private List<Movie> mMovieList;

    final private ListItemClickListener mOnClickListener;

    public MovieAdapter(List<Movie> movieList, ListItemClickListener onClickListener) {
        this.mMovieList = movieList;
        this.mOnClickListener = onClickListener;
    }

    public interface ListItemClickListener {
        void onListItemClick(Movie clickedMovie);
    }

    @NonNull
    @Override
    public MovieAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.movie_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);

        return new MovieAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieAdapterViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return mMovieList.size();
    }

    class MovieAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView mMovieImageView;

        public MovieAdapterViewHolder(final View itemView) {
            super(itemView);

            mMovieImageView = (ImageView) itemView.findViewById(R.id.iv_movie_image_view);

            itemView.setOnClickListener(this);
        }

        public void bind(int position) {
            Movie movie = mMovieList.get(position);
            String moviePath = NetworkUtils.IMAGES_BASE_URL + movie.getPosterPath();
            Log.d("List Item URL", moviePath);
            Picasso.get().load(moviePath).into(mMovieImageView);
        }

        @Override
        public void onClick(View view) {
            int clickedPosition = getAdapterPosition();
            mOnClickListener.onListItemClick(mMovieList.get(clickedPosition));
        }
    }
}
