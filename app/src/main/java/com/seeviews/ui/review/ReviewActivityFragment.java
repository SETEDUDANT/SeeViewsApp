package com.seeviews.ui.review;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.seeviews.R;
import com.seeviews.SeeviewApplication;
import com.seeviews.SeeviewFragment;
import com.seeviews.model.api.receive.Question;
import com.seeviews.model.internal.BaseModel;
import com.seeviews.ui.question.QuestionActivity;
import com.seeviews.utils.ActivityUtils;
import com.seeviews.utils.NetworkUtils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ReviewActivityFragment extends SeeviewFragment implements ReviewAdapter.QuestionSelectedListener {

    private static final String TAG = "ReviewActivityFragment";
    private static final int REQ_QUESTION = 1;

    private ViewGroup sceneRoot;
    @BindView(R.id.review_list)
    RecyclerView list;
    @BindView(R.id.review_empty)
    View empty;

    ReviewAdapter adapter;

    public ReviewActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        sceneRoot = (ViewGroup) inflater.inflate(R.layout.fragment_review, container, false);
        ButterKnife.bind(this, sceneRoot);
        return sceneRoot;
    }

    @Override
    public void onQuestionSelected(Question q) {
        startActivityForResult(QuestionActivity.getStartIntent(getActivity(), q.getId()), REQ_QUESTION);
    }

    @Override
    protected void refreshData() {
        super.refreshData();
        getApp().refreshQuestions(this);
    }

    @Override
    protected SeeviewApplication.DataListener defineDataListener() {
        return new SeeviewApplication.DataListener() {
            @Override
            public void onDataLoaded(@NonNull BaseModel data) {
                Log.d(TAG, "onDataLoaded");
                ArrayList<Question> questions = data.getQuestions();
                if (questions == null || questions.size() == 0)
                    onDataError(new Exception("There are no questions in the data :("));
                else {
                    empty.setVisibility(View.GONE);
                    list.setVisibility(View.VISIBLE);

                    if (adapter == null) {
                        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
                        list.setLayoutManager(mLayoutManager);
                        // list.setItemAnimator(new DefaultItemAnimator());
                        list.setPadding(0, 0, 0, ActivityUtils.getNavigationBarHeight(getContext(), false));
                        adapter = new ReviewAdapter(getContext(), data.getAuthHeader(), questions, ReviewActivityFragment.this);
                        list.setAdapter(adapter);
                    } else {
                        adapter.updateQuestions(questions);
                    }
                }
            }

            @Override
            public void onDataError(@NonNull Throwable t) {
                Log.e(TAG, "onDataError: " + t.getLocalizedMessage());
                TransitionManager.beginDelayedTransition(sceneRoot);
                list.setVisibility(View.GONE);
                empty.setVisibility(View.VISIBLE);
            }
        };
    }

    @Override
    public void onQuestionResetClicked(final Question q, final NetworkUtils.DeleteAnswerCallback callback) {
        final int reviewId = q.getReview() == null ? -1 : q.getReview().getId();
        if (reviewId == -1) {
            if (callback != null)
                callback.onFailure(new Exception("No review to delete"));
        } else
            new MaterialDialog.Builder(getContext())
                    .theme(Theme.LIGHT)
                    .title(R.string.review_dialog_reset_title)
                    .content(R.string.review_dialog_reset_content)
                    .positiveText(R.string.review_dialog_reset_pos)
                    .negativeText(R.string.review_dialog_reset_neg)
                    .positiveColor(ContextCompat.getColor(getContext(), R.color.colorPrimaryDark))
                    .negativeColor(ContextCompat.getColor(getContext(), R.color.dialog_btn))
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            //adapter is the callback, so it updates itself
                            getApp().deleteReview(reviewId, callback);
                        }
                    })
                    .show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: " + requestCode);
        onRefreshComplete();
    }
}
