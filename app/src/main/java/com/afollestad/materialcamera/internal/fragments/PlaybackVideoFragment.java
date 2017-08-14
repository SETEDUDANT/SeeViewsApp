package com.afollestad.materialcamera.internal.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.afollestad.easyvideoplayer.EasyVideoCallback;
import com.afollestad.easyvideoplayer.EasyVideoPlayer;
import com.afollestad.materialcamera.internal.CameraIntentKey;
import com.afollestad.materialcamera.internal.interfaces.BaseCaptureInterface;
import com.afollestad.materialcamera.internal.interfaces.CameraUriInterface;
import com.afollestad.materialcamera.util.CameraUtil;
import com.afollestad.materialdialogs.MaterialDialog;
import com.seeviews.R;
import com.seeviews.utils.ActivityUtils;
import com.seeviews.utils.StringUtils;

import java.io.File;

import static android.content.ContentValues.TAG;

/**
 * @author Aidan Follestad (afollestad)
 */
public class PlaybackVideoFragment extends Fragment implements CameraUriInterface, EasyVideoCallback, View.OnClickListener {

    private EasyVideoPlayer mPlayer;
    private String mOutputUri;
    private BaseCaptureInterface mInterface;
    private EditText comment;
    private Button mRetry;
    private Button mConfirm;
    private TextView duration;
    private View playBtn;

    private Handler mCountdownHandler;
    private final Runnable mCountdownRunnable = new Runnable() {
        @Override
        public void run() {
            if (mPlayer != null) {
                long diff = mInterface.getRecordingEnd() - System.currentTimeMillis();
                if (diff <= 0) {
                    useVideo();
                    return;
                }
                mPlayer.setBottomLabelText(String.format("-%s", CameraUtil.getDurationString(diff)));
                if (mCountdownHandler != null)
                    mCountdownHandler.postDelayed(mCountdownRunnable, 200);
            }
        }
    };

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

    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mInterface = (BaseCaptureInterface) activity;
    }

    public static PlaybackVideoFragment newInstance(String outputUri, boolean allowRetry, int primaryColor) {
        PlaybackVideoFragment fragment = new PlaybackVideoFragment();
        fragment.setRetainInstance(true);
        Bundle args = new Bundle();
        args.putString("output_uri", outputUri);
        args.putBoolean(CameraIntentKey.ALLOW_RETRY, allowRetry);
        args.putInt(CameraIntentKey.PRIMARY_COLOR, primaryColor);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() != null)
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mPlayer != null) {
            mPlayer.release();
            mPlayer.reset();
            mPlayer = null;
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.mcam_fragment_videoplayback, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mPlayer = (EasyVideoPlayer) view.findViewById(R.id.playbackView);
        mPlayer.setCallback(this);

        mPlayer.setSubmitTextRes(mInterface.labelConfirm());
        mPlayer.setRetryTextRes(mInterface.labelRetry());
        mPlayer.setPlayDrawableRes(mInterface.iconPlay());
        mPlayer.setPauseDrawableRes(mInterface.iconPause());
        mPlayer.disableControls();

//        if (getArguments().getBoolean(CameraIntentKey.ALLOW_RETRY, true))
//            mPlayer.setLeftAction(EasyVideoPlayer.LEFT_ACTION_RETRY);
//        mPlayer.setRightAction(EasyVideoPlayer.RIGHT_ACTION_SUBMIT);

        mPlayer.setThemeColor(getArguments().getInt(CameraIntentKey.PRIMARY_COLOR));
        mOutputUri = getArguments().getString("output_uri");

        if (mInterface.hasLengthLimit() && mInterface.shouldAutoSubmit() && mInterface.continueTimerInPlayback()) {
            final long diff = mInterface.getRecordingEnd() - System.currentTimeMillis();
            mPlayer.setBottomLabelText(String.format("-%s", CameraUtil.getDurationString(diff)));
            startCountdownTimer();
        }

        mPlayer.setSource(Uri.parse(mOutputUri));

        playBtn = view.findViewById(R.id.preview_play_btn);
        playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mPlayer.isPlaying())
                    mPlayer.pause();
                else
                    mPlayer.start();
            }
        });
        duration = (TextView) view.findViewById(R.id.preview_duration);

        mPlayer.setCallback(new EasyVideoCallback() {
            @Override
            public void onStarted(EasyVideoPlayer player) {
                playBtn.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onPaused(EasyVideoPlayer player) {
                playBtn.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPreparing(EasyVideoPlayer player) {
            }

            @Override
            public void onPrepared(EasyVideoPlayer player) {
                duration.setText(StringUtils.toDuration(mPlayer.getDuration()));
            }

            @Override
            public void onBuffering(int percent) {
            }

            @Override
            public void onError(EasyVideoPlayer player, Exception e) {
            }

            @Override
            public void onCompletion(EasyVideoPlayer player) {
                playBtn.setVisibility(View.VISIBLE);
            }

            @Override
            public void onRetry(EasyVideoPlayer player, Uri source) {
            }

            @Override
            public void onSubmit(EasyVideoPlayer player, Uri source) {
            }
        });

        comment = (EditText) view.findViewById(R.id.preview_comment);
        mRetry = (Button) view.findViewById(R.id.retry);
        mConfirm = (Button) view.findViewById(R.id.confirm);

        mConfirm.setText(mInterface.labelConfirm());
        mRetry.setText(mInterface.labelRetry());

        mRetry.setOnClickListener(this);
        mConfirm.setOnClickListener(this);

        Toolbar toolbar = (Toolbar) view.findViewById(R.id.preview_toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_left_white);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mInterface.onRetry(mOutputUri);
            }
        });
        view.findViewById(R.id.preview_toolbar_wrapper).setPadding(0, ActivityUtils.getStatusBarHeight(getActivity()), 0, 0);

        ((TextView) view.findViewById(R.id.preview_question)).setText(mInterface.question());
        Log.d(TAG, "question: " + mInterface.question());
    }

    private void startCountdownTimer() {
        if (mCountdownHandler == null)
            mCountdownHandler = new Handler();
        else mCountdownHandler.removeCallbacks(mCountdownRunnable);
        mCountdownHandler.post(mCountdownRunnable);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mCountdownHandler != null) {
            mCountdownHandler.removeCallbacks(mCountdownRunnable);
            mCountdownHandler = null;
        }
        if (mPlayer != null) {
            mPlayer.release();
            mPlayer = null;
        }
    }

    private void useVideo() {
        if (mPlayer != null) {
            mPlayer.release();
            mPlayer = null;
        }
        if (mInterface != null)
            mInterface.useMedia(mOutputUri, comment.getText().toString());
    }

    @Override
    public String getOutputUri() {
        return getArguments().getString("output_uri");
    }

    @Override
    public void onStarted(EasyVideoPlayer player) {
    }

    @Override
    public void onPaused(EasyVideoPlayer player) {
    }

    @Override
    public void onPreparing(EasyVideoPlayer player) {
    }

    @Override
    public void onPrepared(EasyVideoPlayer player) {
    }

    @Override
    public void onBuffering(int percent) {
    }

    @Override
    public void onError(EasyVideoPlayer player, Exception e) {
        new MaterialDialog.Builder(getActivity())
                .title(R.string.mcam_error)
                .content(e.getMessage())
                .positiveText(android.R.string.ok)
                .show();
    }

    @Override
    public void onCompletion(EasyVideoPlayer player) {
    }

    @Override
    public void onRetry(EasyVideoPlayer player, Uri source) {
        if (mInterface != null)
            mInterface.onRetry(mOutputUri);
    }

    @Override
    public void onSubmit(EasyVideoPlayer player, Uri source) {
        useVideo();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.retry)
            mInterface.onRetry(mOutputUri);
        else if (v.getId() == R.id.confirm)
            mInterface.useMedia(mOutputUri, comment.getText().toString());
    }
}