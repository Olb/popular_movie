package com.flx.popmovies.moviedetails;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.flx.popmovies.R;
import com.flx.popmovies.data.Review;

import java.util.List;

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ReviewsAdapterViewHolder> {

    private List<Review> mReviewList;

    @NonNull
    @Override
    public ReviewsAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.review_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(layoutIdForListItem, viewGroup, false);

        return new ReviewsAdapterViewHolder(view);
    }

    public void setNewData(List<Review> reviewList) {
        this.mReviewList = reviewList;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewsAdapterViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        if (mReviewList == null) {
            return 0;
        }
        return mReviewList.size();
    }

    class ReviewsAdapterViewHolder extends RecyclerView.ViewHolder {

        private final TextView mAuthorTextView;
        private final TextView mContentTextView;
        private final TextView mOriginalPostTextView;

        ReviewsAdapterViewHolder(View itemView) {
            super(itemView);

            mAuthorTextView = itemView.findViewById(R.id.tv_author);
            mContentTextView = itemView.findViewById(R.id.tv_review);
            mOriginalPostTextView = itemView.findViewById(R.id.tv_original_post_location);
        }

        void bind(int position) {
            mAuthorTextView.setText(mReviewList.get(position).getAuthor());
            mContentTextView.setText(mReviewList.get(position).getContent());
            mOriginalPostTextView.setText(mReviewList.get(position).getUrl());
        }
    }
}
