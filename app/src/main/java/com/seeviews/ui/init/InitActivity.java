package com.seeviews.ui.init;

import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;

import com.seeviews.R;
import com.seeviews.SeeviewActivity;
import com.seeviews.utils.AndroidBug5497Workaround;

import butterknife.BindView;
import butterknife.ButterKnife;
import eightbitlab.com.blurview.BlurView;

public class InitActivity extends SeeviewActivity {

    private static final String TAG = "InitActivity";

    @BindView(R.id.init_coordinator)
    CoordinatorLayout coordinator;
    @BindView(R.id.init_toolbar)
    Toolbar toolbar;
    @BindView(R.id.init_toolbar_logo)
    View toolbarLogo;
    @BindView(R.id.init_blurView)
    BlurView blurView;
    @BindView(R.id.init_bg)
    ImageView background;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init);
        AndroidBug5497Workaround.assistActivity(this);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        setTitle("");
        setupBlurForToolbar(coordinator, blurView);
    }

    public void setLogoVisible(boolean logoVisible) {
        if (blurView != null)
            blurView.setVisibility(logoVisible ? View.VISIBLE : View.INVISIBLE);
    }
}
