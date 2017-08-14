package com.seeviews;

import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.seeviews.utils.ActivityUtils;

import eightbitlab.com.blurview.BlurView;
import eightbitlab.com.blurview.RenderScriptBlur;

/**
 * Created by Jan-Willem on 3-12-2016.
 */

public abstract class SeeviewActivity extends AppCompatActivity {

    private static final String TAG = "SeeviewActivity";

    public SeeviewApplication getApp() {
        return (SeeviewApplication) getApplication();
    }

    public void registerDataListener(@NonNull SeeviewApplication.DataListener listener) {
        getApp().registerDataListener(listener);
    }

    public void unregisterDataListener(@NonNull SeeviewApplication.DataListener listener) {
        getApp().unregisterDataListener(listener);
    }

    protected void setupBlurForToolbar(ViewGroup sceneRoot, BlurView blurView) {
        final int radius = 4;
        blurView.setupWith(sceneRoot)
                .windowBackground(getWindow().getDecorView().getBackground())
                .blurAlgorithm(new RenderScriptBlur(this, true)) //Optional, enabled by default. User can have custom implementation
                .blurRadius(radius);
        setToolbarWrapperPadding(blurView);
    }

    protected void setToolbarWrapperPadding(View toolbarWrapper) {
        toolbarWrapper.setPadding(0, ActivityUtils.getStatusBarHeight(this), 0, 0);
    }

    @Override
    protected void onDestroy() {
        getApp().writeData();
        super.onDestroy();
    }

    public void snack(int textRes) {
        snack(getString(textRes));
    }

    public void snack(String text) {
        int coordinatorId = getCoordinatorId();
        if (coordinatorId != 0) {
            try {
                CoordinatorLayout coordinator = (CoordinatorLayout) findViewById(coordinatorId);
                Snackbar snackbar = Snackbar.make(coordinator, text, Snackbar.LENGTH_SHORT);
                snackbar.setActionTextColor(ContextCompat.getColor(this, R.color.colorPrimary));
                snackbar.show();
            } catch (Exception e) {
                Log.e(TAG, "snack: " + e.getLocalizedMessage());
            }
        } else {
            Log.w(TAG, "snack: Activity does not return a coordinator layout id");
        }
    }

    protected int getCoordinatorId() {
        return 0;
    }
}
