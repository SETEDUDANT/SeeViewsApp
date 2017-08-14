package com.seeviews.ui.home;

import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.seeviews.R;
import com.seeviews.model.api.receive.Review;
import com.seeviews.utils.NetworkUtils;
import com.seeviews.utils.StringUtils;

import java.util.List;

/**
 * Created by Jan-Willem on 1-12-2016.
 */

class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {

    private static final String TAG = "ReviewAdapter";
    private Fragment f;
    private List<Review> reviewList;
    private String authHeader;

    ReviewAdapter(Fragment f, List<Review> reviewList, String authString) {
        this.f = f;
        this.reviewList = reviewList;
        this.authHeader = authString;
    }

    @Override
    public int getItemCount() {
        return reviewList.size();
    }

    @Override
    public void onBindViewHolder(final ReviewViewHolder h, int i) {
        final Review r = reviewList.get(i);
        h.title.setText(r.getUser());
        h.subtitle.setText(r.getTime_ago());
        h.rating.setVisibility(r.getSentiment() == 0 ? View.GONE : View.VISIBLE);
        h.rating.setImageResource(r.getRatingEmoticon());
        h.rating.setColorFilter(ContextCompat.getColor(f.getContext(), r.getRatingColor()));

        GlideUrl glideUrl = null;
        if (StringUtils.isNotEmpty(r.getImage())) {
            glideUrl = NetworkUtils.getAuthenticatedGlideUrl(f.getContext(), authHeader, NetworkUtils.ImageType.REVIEW_IMAGE, r.getImage());
        } else if (StringUtils.isNotEmpty(r.getVideo()))
            glideUrl = NetworkUtils.getAuthenticatedGlideUrl(f.getContext(), authHeader, NetworkUtils.ImageType.REVIEW_VIDEO, r.getVideo());

        if (glideUrl == null) {
            h.image.setImageResource(R.drawable.bg_review_fallback);
        } else {
//        Log.d(TAG, "url: " + glideUrl);
            Glide.with(f)
                    .load(glideUrl)
                    .placeholder(R.drawable.bg_review_placeholder)
                    .error(R.drawable.bg_review_fallback)
                    .crossFade()
                    .into(h.image);
        }

    }

    @Override
    public ReviewViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater
                .from(viewGroup.getContext())
                .inflate(R.layout.listitem_home_review, viewGroup, false);
        return new ReviewViewHolder(itemView);
    }

    static class ReviewViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView title;
        TextView subtitle;
        ImageView rating;

        ReviewViewHolder(View v) {
            super(v);
            image = (ImageView) v.findViewById(R.id.review_image);
            title = (TextView) v.findViewById(R.id.review_card_title);
            subtitle = (TextView) v.findViewById(R.id.review_card_subtitle);
            rating = (ImageView) v.findViewById(R.id.review_card_rating);
        }
    }
}
