package com.seeviews.ui.review;

import android.content.Context;
import android.graphics.Color;
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
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.chauthai.swipereveallayout.SwipeRevealLayout;
import com.chauthai.swipereveallayout.ViewBinderHelper;
import com.seeviews.R;
import com.seeviews.model.api.receive.Question;
import com.seeviews.model.api.receive.Review;
import com.seeviews.utils.NetworkUtils;
import com.seeviews.utils.StringUtils;

import java.util.ArrayList;

/**
 * Created by Jan-Willem on 1-12-2016.
 */

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.QuestionViewHolder> {
    private static final String TAG = "ReviewAdapter";
    private Context c;
    private ArrayList<Question> questions;
    private QuestionSelectedListener listener;
    String auth;

    private final ViewBinderHelper revealHelper = new ViewBinderHelper();


    public interface QuestionSelectedListener {
        void onQuestionSelected(Question q);

        void onQuestionResetClicked(Question q, NetworkUtils.DeleteAnswerCallback callback);
    }

    public ReviewAdapter(Context c, String auth, ArrayList<Question> questions, QuestionSelectedListener listener) {
        this.c = c;
        this.auth = auth;
        this.questions = questions;
        this.listener = listener;
        revealHelper.setOpenOnlyOne(true);
    }

    public void updateQuestions(ArrayList<Question> questions) {
        this.questions = questions;
        notifyDataSetChanged();
    }

    public String getAuthHeader() {
        return auth;
    }

    @Override
    public QuestionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.listitem_review_question, parent, false);
        return new QuestionViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final QuestionViewHolder holder, int position) {
        final Question q = questions.get(position);
        Review r = q.getReview();
        final String questionIdString = String.valueOf(q.getId());

        revealHelper.bind(holder.swipeRevealLayout, questionIdString);

        boolean isCompleted = q.isComplete();
        boolean hasSomeInput = q.hasSavedUserInput();

        Log.d(TAG, "onBindViewHolder: hasSomeInput " + hasSomeInput + q);
        if (hasSomeInput) {
            revealHelper.unlockSwipe(questionIdString);
        } else {
            revealHelper.closeLayout(questionIdString);
            revealHelper.lockSwipe(questionIdString);
        }

        //TODO also get current answers
        holder.check.setImageResource(isCompleted ? R.drawable.ic_checked : R.drawable.ic_unchecked);
        int index = position + 1;
        holder.number.setText((index < 10 ? "0" : "") + index);

        holder.title.setText(q.getTitle());
        String subtitle = q.getDescription();
        if (q.getReview() != null && !StringUtils.hasEmpty(q.getReview().getComment()))
            subtitle = q.getReview().getComment();
        holder.subtitle.setText(subtitle);
        setTextColor(holder.title, holder.subtitle, false);

        if (r == null) {
            holder.imageContainer.setVisibility(View.GONE);
            holder.separator.setVisibility(View.VISIBLE);
            holder.image.setImageResource(0);
            setTextColor(holder.title, holder.subtitle, false);
        } else {
            GlideUrl glideUrl = null;
            if (StringUtils.isNotEmpty(r.getImage())) {
                glideUrl = NetworkUtils.getAuthenticatedGlideUrl(c, auth, NetworkUtils.ImageType.REVIEW_IMAGE, r.getImage());
            } else if (StringUtils.isNotEmpty(r.getVideo()))
                glideUrl = NetworkUtils.getAuthenticatedGlideUrl(c, auth, NetworkUtils.ImageType.REVIEW_VIDEO, r.getVideo());

            Log.d(TAG, "glideUrl: " + (glideUrl == null ? "NULL" : glideUrl.toStringUrl()));

            if (glideUrl == null) {
                holder.imageContainer.setVisibility(View.GONE);
                holder.separator.setVisibility(View.VISIBLE);
                holder.image.setImageResource(0);
                setTextColor(holder.title, holder.subtitle, false);
            } else {
                Log.d(TAG, "gliding into " + holder.image);
                holder.imageContainer.setVisibility(View.VISIBLE);
                holder.separator.setVisibility(View.GONE);
                setTextColor(holder.title, holder.subtitle, true);
                Glide.with(c)
                        .load(glideUrl)
//                        .placeholder(R.drawable.ic_emo_excited)
//                        .error(R.drawable.bg_header_fallback)
                        .listener(new RequestListener<GlideUrl, GlideDrawable>() {
                            @Override
                            public boolean onException(Exception e, GlideUrl model, Target<GlideDrawable> target, boolean isFirstResource) {
                                Log.e(TAG, "onException: " + (e == null ? "NuLL" : e.getLocalizedMessage())
                                        + "\n" + model.toStringUrl());
                                holder.imageContainer.setVisibility(View.GONE);
                                holder.separator.setVisibility(View.VISIBLE);
                                setTextColor(holder.title, holder.subtitle, false);
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(GlideDrawable resource, GlideUrl model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                Log.w(TAG, "onResourceReady: " + model.toStringUrl());
                                return false;
                            }
                        })
                        .into(holder.image);
            }
        }

        holder.clicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null)
                    listener.onQuestionSelected(q);
            }
        });
        holder.resetClicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null)
                    listener.onQuestionResetClicked(q, new NetworkUtils.DeleteAnswerCallback() {
                        @Override
                        public void onSuccess(ArrayList<Question> q) {
                            revealHelper.closeLayout(questionIdString);
                            //TODO maybe delay this some ms?
                            updateQuestions(q);
                        }

                        @Override
                        public void onFailure(Throwable t) {
                        }
                    });
            }
        });
    }

    private void setTextColor(TextView title, TextView subTitle, boolean imageLoaded) {
        if (title != null)
            title.setTextColor(ContextCompat.getColor(c, imageLoaded ? R.color.questionTitleLight1 : R.color.questionTitleDark1));
        if (subTitle != null)
            subTitle.setTextColor(ContextCompat.getColor(c, imageLoaded ? R.color.questionCommentLight1 : R.color.questionCommentDark1));



    }

    @Override
    public int getItemCount() {
        return questions.size();
    }

    static class QuestionViewHolder extends RecyclerView.ViewHolder {
        SwipeRevealLayout swipeRevealLayout;
        View clicker;
        View resetClicker;
        View imageContainer;
        TextView number;
        ImageView image;
        TextView title;
        TextView subtitle;
        ImageView check;
        View separator;

        QuestionViewHolder(View v) {
            super(v);
            swipeRevealLayout = (SwipeRevealLayout) v.findViewById(R.id.question_swipe_reveal_layout);
            clicker = v.findViewById(R.id.question_clicker);
            resetClicker = v.findViewById(R.id.question_reset);
            imageContainer = v.findViewById(R.id.question_image_container);
            number = (TextView) v.findViewById(R.id.question_number);
            number.bringToFront();


            image = (ImageView) v.findViewById(R.id.question_image);
            title = (TextView) v.findViewById(R.id.question_title);
            subtitle = (TextView) v.findViewById(R.id.question_comment);
            check = (ImageView) v.findViewById(R.id.question_check);
            separator = v.findViewById(R.id.question_separator);
        }
    }
}
