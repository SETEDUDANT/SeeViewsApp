package com.afollestad.materialcamera.internal.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.afollestad.materialcamera.internal.CameraIntentKey;
import com.afollestad.materialcamera.internal.interfaces.BaseCaptureInterface;
import com.afollestad.materialcamera.internal.interfaces.CameraUriInterface;
import com.afollestad.materialcamera.util.CameraUtil;
import com.afollestad.materialdialogs.MaterialDialog;
import com.seeviews.R;
import com.seeviews.utils.ActivityUtils;

import static android.content.ContentValues.TAG;

public abstract class BaseGalleryFragment extends Fragment implements CameraUriInterface, View.OnClickListener {

    BaseCaptureInterface mInterface;
    int mPrimaryColor;
    String mOutputUri;
    View mControlsFrame;
    Button mRetry;
    Button mConfirm;

    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mInterface = (BaseCaptureInterface) activity;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() != null)
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mOutputUri = getArguments().getString("output_uri");
        mControlsFrame = view.findViewById(R.id.controlsFrame);
        mRetry = (Button) view.findViewById(R.id.retry);
        mConfirm = (Button) view.findViewById(R.id.confirm);

        mPrimaryColor = getArguments().getInt(CameraIntentKey.PRIMARY_COLOR);
//        if (CameraUtil.isColorDark(mPrimaryColor)) {
//            mPrimaryColor = CameraUtil.darkenColor(mPrimaryColor);
//            final int textColor = ContextCompat.getColor(view.getContext(), R.color.mcam_color_light);
//            mRetry.setTextColor(textColor);
//            mConfirm.setTextColor(textColor);
//        } else {
//            final int textColor = ContextCompat.getColor(view.getContext(), R.color.mcam_color_dark);
//            mRetry.setTextColor(textColor);
//            mConfirm.setTextColor(textColor);
//        }
//        mControlsFrame.setBackgroundColor(mPrimaryColor);
//        mRetry.setVisibility(getArguments().getBoolean(CameraIntentKey.ALLOW_RETRY, true) ? View.VISIBLE : View.GONE);
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.preview_toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_left_white);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mInterface.onRetry(mOutputUri);
            }
        });
        view.findViewById(R.id.preview_toolbar_wrapper).setPadding(0, ActivityUtils.getStatusBarHeight(getActivity()),0,0);

        ((TextView)view.findViewById(R.id.preview_question)).setText(mInterface.question());
        Log.d(TAG, "question: " + mInterface.question());
    }

    @Override
    public String getOutputUri() {
        return getArguments().getString("output_uri");
    }

    void showDialog(String title, String errorMsg) {
        new MaterialDialog.Builder(getActivity())
                .title(title)
                .content(errorMsg)
                .positiveText(android.R.string.ok)
                .show();
    }
}