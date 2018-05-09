package com.flx.popmovies.moviedetails;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.flx.popmovies.R;
import com.flx.popmovies.data.Trailer;

import java.util.List;

public class TrailersAdapter extends RecyclerView.Adapter<TrailersAdapter.TrailersAdapterViewHolder> {

    private List<Trailer> mTrailerList;

    final private ListItemClickListener mOnClickListener;

    TrailersAdapter(ListItemClickListener mOnClickListener) {
        this.mOnClickListener = mOnClickListener;
    }

    public interface ListItemClickListener {
        void onListItemClick(long movieId);
    }

    @NonNull
    @Override
    public TrailersAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.trailier_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(layoutIdForListItem, viewGroup, false);

        return new TrailersAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TrailersAdapterViewHolder holder, int position) {
        holder.bind(position);
    }

    public void setNewData(List<Trailer> trailerList) {
        this.mTrailerList = trailerList;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (mTrailerList == null) {
            return 0;
        }
        return mTrailerList.size();
    }

    class TrailersAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final TextView mTrailerTitleTextView;

        TrailersAdapterViewHolder(final View itemView) {
            super(itemView);

            mTrailerTitleTextView = itemView.findViewById(R.id.tv_trailer_title);

            itemView.setOnClickListener(this);
        }

        void bind(int position) {
            Trailer trailer = mTrailerList.get(position);
            mTrailerTitleTextView.setText(trailer.getName());
        }

        @Override
        public void onClick(View view) {
            mOnClickListener.onListItemClick(Integer.valueOf(mTrailerList.get(getAdapterPosition()).getId()));
        }
    }
}