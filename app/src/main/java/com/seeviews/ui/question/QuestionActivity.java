package com.seeviews.ui.question;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.seeviews.R;
import com.seeviews.SeeviewActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import eightbitlab.com.blurview.BlurView;

public class QuestionActivity extends SeeviewActivity {
    private static final String TAG = "QuestionActivity";
    private static final String ARG_ID_QUESTION = "arg_id_question";

    public static Intent getStartIntent(Activity parent, int questionId) {
        Intent i = new Intent(parent, QuestionActivity.class);
        i.putExtra(ARG_ID_QUESTION, questionId);
        return i;
    }

    @BindView(R.id.question_coordinator)
    CoordinatorLayout coordinator;
    @BindView(R.id.question_blurView)
    BlurView blurView;
    @BindView(R.id.question_toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        setupBlurForToolbar(coordinator, blurView);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            attemptToExit(new Runnable() {
                @Override
                public void run() {
                    finish();
                }
            });
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        attemptToExit(new Runnable() {
            @Override
            public void run() {
                QuestionActivity.super.onBackPressed();
            }
        });
    }

    private void attemptToExit(@NonNull final Runnable runIfUserWantsToBackOut) {
        if (getFragment().answerHasBeenChanged()) {
            new MaterialDialog.Builder(this)
                    .theme(Theme.LIGHT)
                    .title(R.string.question_dialog_sure_title)
                    .content(R.string.question_dialog_sure_content)
                    .positiveText(R.string.question_dialog_sure_pos)
                    .negativeText(R.string.question_dialog_sure_neg)
                    .neutralText(R.string.question_dialog_sure_neu)
                    .neutralColor(ContextCompat.getColor(this, R.color.dialog_btn))
                    .positiveColor(ContextCompat.getColor(this, R.color.colorPrimaryDark))
                    .negativeColor(ContextCompat.getColor(this, R.color.dialog_btn))
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            saveReview(runIfUserWantsToBackOut);
                        }
                    })
                    .onNegative(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            runIfUserWantsToBackOut.run();
                        }
                    })
                    .show();
        } else
            runIfUserWantsToBackOut.run();
    }

    private void saveReview(@NonNull Runnable runOnFinish) {
        getFragment().saveReviewOnline(runOnFinish);
    }

    private QuestionActivityFragment getFragment() {
        return (QuestionActivityFragment) getSupportFragmentManager().findFragmentById(R.id.fragment);
    }

    public int getQuestionId() {
        return getIntent().getIntExtra(ARG_ID_QUESTION, -1);
    }

    @Override
    protected int getCoordinatorId() {
        return R.id.question_coordinator;
    }
}
