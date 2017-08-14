package com.seeviews.utils;

import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.seeviews.R;
import com.seeviews.model.api.receive.ImageName;

import java.util.ArrayList;

/**
 * Created by Jan-Willem on 15-12-2016.
 */

public class ImageSwitcher {

    private static final String TAG = "ImageSwitcher";
    private Fragment f;

    private ImageView imageView;
    private String authHeader;
    private NetworkUtils.ImageType imageType;
    private int fallbackResource;
    private ArrayList<String> imageNames;

    private int imageIndex;
    private boolean stopped;

    private Runnable runnable;

    public ImageSwitcher(Fragment f, ImageView imageView, String authHeader, NetworkUtils.ImageType imageType, int fallbackResource, ArrayList<ImageName> imageNames) {
        this.f = f;
        this.imageView = imageView;
        this.authHeader = authHeader;
        this.imageType = imageType;
        this.fallbackResource = fallbackResource;
        this.imageNames = new ArrayList<>();

        if (imageNames != null)
            for (ImageName n : imageNames)
                if (n != null && StringUtils.isNotEmpty(n.getName()))
                    this.imageNames.add(n.getName());
    }

    public void reset() {
        imageIndex = 0;
    }

    public boolean canStart() {
        return f != null && imageView != null && imageNames != null && imageNames.size() > 0;
    }

    public void start() {
        if (canStart()) {
            stopped = false;
            showNextHeaderImage();
        } else
            Log.w(TAG, "start called, but I can not start");
    }

    public void stop() {
        runnable = null;
        stopped = true;
    }

    private void showNextHeaderImage() {
        if (f != null) {
            final Handler handler = new Handler();
            final int delay = f.getResources().getInteger(R.integer.headerDelayMs);
            runnable = new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, "running");
                    if (!stopped) {
                        Log.d(TAG, "non stop");
                        String imageFile = imageNames.get(imageIndex);
                        try {
                            GlideUrl url = NetworkUtils.getAuthenticatedGlideUrl(f.getContext(), authHeader, imageType, imageFile);
                            Log.d(TAG, "url: " + url.toStringUrl());
                            Glide.with(f)
                                    .load(url)
                                    .error(fallbackResource)
                                    .crossFade()
                                    .listener(new RequestListener<GlideUrl, GlideDrawable>() {
                                        @Override
                                        public boolean onException(Exception e, GlideUrl model, Target<GlideDrawable> target, boolean isFirstResource) {
                                            Log.e(TAG, "onException: " + (e == null ? "NULL" : e.getLocalizedMessage())
                                                    + "\n" + (model == null ? "NULL" : model.toStringUrl()));
                                            if (!stopped && f != null && !f.isDetached()) {
                                                int oldIndex = imageIndex;
                                                imageIndex++;
                                                imageIndex %= imageNames.size();
                                                if (oldIndex != imageIndex)
                                                    runnable.run();
                                            }
                                            return false;
                                        }

                                        @Override
                                        public boolean onResourceReady(GlideDrawable resource, GlideUrl model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                            Log.d(TAG, "onResourceReady: stopping: " + stopped);
                                            if (!stopped && f != null && !f.isDetached()) {
                                                imageIndex++;
                                                imageIndex %= imageNames.size();
                                                handler.postDelayed(runnable, delay);
                                            }
                                            return false;
                                        }
                                    })
                                    .into(imageView);
                        } catch (Exception ignored) {
                        }
                    }
                }
            };
            runnable.run();
        }
//        if (!stopped) {
//            String imageFile = imageNames.get(imageIndex);
//            try {
//                GlideUrl url = NetworkUtils.getAuthenticatedGlideUrl(f.getContext(), authHeader, NetworkUtils.ImageType.DASHBOARD_IMAGE, imageFile);
//                Glide.with(f)
//                        .load(url)
//                        .error(R.drawable.bg_header_fallback)
//                        .crossFade()
//                        .listener(new RequestListener<GlideUrl, GlideDrawable>() {
//                            @Override
//                            public boolean onException(Exception e, GlideUrl model, Target<GlideDrawable> target, boolean isFirstResource) {
//                                return false;
//                            }
//
//                            @Override
//                            public boolean onResourceReady(GlideDrawable resource, GlideUrl model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
//                                return false;
//                            }
//                        })
//                        .into(imageView);
//
////                new Handler().postDelayed(new Runnable() {
////                    @Override
////                    public void run() {
////                        if (!isDetached()) {
////                            imageIndex++;
////                            imageIndex %= imageNames.size();
////                            showNextHeaderImage();
////                        }
////                    }
////                }, getResources().getInteger(R.integer.headerDelayMs));
//            } catch (Exception ignored) {
//            }
//        }
    }
}
