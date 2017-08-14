package com.afollestad.materialcamera.internal.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.ImageView;

import com.afollestad.materialcamera.internal.CameraIntentKey;
import com.afollestad.materialcamera.util.ImageUtil;
import com.seeviews.R;

import java.io.File;

public class StillshotPreviewFragment extends BaseGalleryFragment {

    private static final String TAG = "StillshotPrviewFragment";
    private ImageView mImageView;
    private EditText comment;

    /**
     * Reference to the bitmap, in case 'onConfigurationChange' event comes, so we do not recreate the bitmap
     */
    private static Bitmap mBitmap;

    public static StillshotPreviewFragment newInstance(String outputUri, boolean allowRetry, int primaryColor) {
        final StillshotPreviewFragment fragment = new StillshotPreviewFragment();
        fragment.setRetainInstance(true);
        Bundle args = new Bundle();
        args.putString("output_uri", outputUri);
        args.putBoolean(CameraIntentKey.ALLOW_RETRY, allowRetry);
        args.putInt(CameraIntentKey.PRIMARY_COLOR, primaryColor);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null)
            try {
                if (getArguments() != null && getArguments().containsKey("output_uri")) {
                    String path = getArguments().getString("output_uri", "");
                    File f = new File(path);
                    getActivity().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(f)));
                }
            } catch (Exception e) {
                Log.e(TAG, "onCreate: Broadcast error: " + e.getLocalizedMessage());
            }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.mcam_fragment_stillshot, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mImageView = (ImageView) view.findViewById(R.id.stillshot_imageview);
        comment = (EditText) view.findViewById(R.id.preview_comment);

        mConfirm.setText(mInterface.labelConfirm());
        mRetry.setText(mInterface.labelRetry());

        mRetry.setOnClickListener(this);
        mConfirm.setOnClickListener(this);

        mImageView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                setImageBitmap();
                mImageView.getViewTreeObserver().removeOnPreDrawListener(this);

                return true;
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mBitmap != null && !mBitmap.isRecycled()) {
            try {
                mBitmap.recycle();
                mBitmap = null;
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
    }


    /**
     * Sets bitmap to ImageView widget
     */
    private void setImageBitmap() {
        final int width = mImageView.getMeasuredWidth();
        final int height = mImageView.getMeasuredHeight();

        // TODO IMPROVE MEMORY USAGE HERE, ESPECIALLY ON LOW-END DEVICES.
        if (mBitmap == null)
            mBitmap = ImageUtil.getRotatedBitmap(Uri.parse(mOutputUri).getPath(), width, height);

        if (mBitmap == null)
            showDialog(getString(R.string.mcam_image_preview_error_title), getString(R.string.mcam_image_preview_error_message));
        else
            mImageView.setImageBitmap(mBitmap);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.retry)
            mInterface.onRetry(mOutputUri);
        else if (v.getId() == R.id.confirm)
            mInterface.useMedia(mOutputUri, comment.getText().toString());
    }
}