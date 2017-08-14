package com.seeviews.ui.question;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.transition.TransitionManager;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialcamera.MaterialCamera;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.cocosw.bottomsheet.BottomSheet;
import com.seeviews.R;
import com.seeviews.SeeviewApplication;
import com.seeviews.SeeviewFragment;
import com.seeviews.model.api.receive.Preset;
import com.seeviews.model.api.receive.Question;
import com.seeviews.model.api.receive.Review;
import com.seeviews.model.internal.Answer;
import com.seeviews.model.internal.BaseModel;
import com.seeviews.utils.ActivityUtils;
import com.seeviews.utils.ImageSwitcher;
import com.seeviews.utils.NetworkUtils;
import com.seeviews.utils.PrefUtils;
import com.seeviews.utils.QuestionSliderUtils;
import com.seeviews.utils.StringUtils;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;

import java.io.File;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A placeholder fragment containing a simple view.
 */
public class QuestionActivityFragment extends SeeviewFragment {

    private static final String TAG = "QuestionActivityFrgmnt";
    private static final int REQ_MEDIA_VIDEO = 1;
    private static final int REQ_MEDIA_PHOTO = 2;
    private static final int REQ_MEDIA_GALLERY = 3;
    private static final int REQ_MEDIA_SAVE = 4;
    private static final String KEY_ANSWER = "newAnswer";

    @BindView(R.id.question_container)
    ViewGroup allContainer;
    @BindView(R.id.question_header_image)
    ImageView headerImage;
    @BindView(R.id.question_header_name)
    TextView title;
    @BindView(R.id.question_slider_container)
    LinearLayout slidersContainer;
    @BindView(R.id.question_media_card)
    View mediaCard;
    @BindView(R.id.questions_media_text)
    TextView mediaText;
    @BindView(R.id.questions_media_clear_btn)
    View mediaClearBtn;
    @BindView(R.id.question_save_btn)
    TextView saveBtn;
    @BindView(R.id.question_footer)
    View footer;

    int hotelId;
    Question question;
    Answer answer;

    SparseArray<DiscreteSeekBar> seekBars;

    String userCode;
    String contactEmail;
    private String authHeader;

    ImageSwitcher imageSwitcher;

    public QuestionActivityFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null)
            answer = savedInstanceState.getParcelable(KEY_ANSWER);
        else
            answer = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_question, container, false);
        ButterKnife.bind(this, rootView);
        allContainer.setPadding(0, 0, 0, ActivityUtils.getNavigationBarHeight(getContext(), false));
        mediaCard.setVisibility(View.GONE);
        saveBtn.setVisibility(View.GONE);
        footer.setVisibility(View.GONE);

        rootView.findViewById(R.id.question_header)
                .setLayoutParams(new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        PrefUtils.getHeaderHeight(getContext())));

        return rootView;
    }

    @Override
    protected SeeviewApplication.DataListener defineDataListener() {
        return new SeeviewApplication.DataListener() {
            @Override
            public void onDataLoaded(@NonNull BaseModel data) {
                hotelId = data.getHotel().getId();
                question = data.getQuestion(getQuestionId());
                authHeader = data.getAuthHeader();

                if (answer == null)
                    answer = new Answer(question);

                Log.d(TAG, "onDataLoaded: \n- Question: "
                        + question
                        + "\n Answer:"
                        + answer);

                if (question == null) {
                    onDataError(new Exception("No question found :("));
                } else {
                    //title
                    title.setText(question.getTitle());

                    //header image
                    onHeaderResourcesChange();

                    //mediaBtn
                    setMediaClearState();
                    mediaClearBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            new MaterialDialog.Builder(getContext())
                                    .theme(Theme.LIGHT)
                                    .title(R.string.question_media_clear_title)
                                    .content(R.string.question_media_clear_content)
                                    .positiveText(R.string.question_media_clear_pos)
                                    .negativeText(R.string.question_media_clear_neg)
                                    .positiveColor(ContextCompat.getColor(getContext(), R.color.colorPrimaryDark))
                                    .negativeColor(ContextCompat.getColor(getContext(), R.color.dialog_btn))
                                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                                        @Override
                                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                            if (answer != null)
                                                answer.clearMedia();
                                            onHeaderResourcesChange();
                                        }
                                    })
                                    .show();
                        }
                    });

                    //photoBtn
                    mediaCard.setVisibility(hotelId == 0 ? View.GONE : View.VISIBLE);

                    //sliders
                    slidersContainer.removeAllViews();
                    seekBars = new SparseArray<>();
                    for (Preset p : question.getPresets()) {
                        float answeredValue = answer.getSliderValue(p.getId());
                        DiscreteSeekBar s = QuestionSliderUtils.addSliderView(getContext(), slidersContainer, p, answeredValue, new QuestionSliderUtils.onSliderChangeListener() {
                            @Override
                            public void onSliderChange() {
                                collectChangedAnswer();
                                setSaveBtnState();
                            }
                        });
                        seekBars.put(p.getId(), s);
                    }

                    //savebtn
                    saveBtn.setVisibility(View.VISIBLE);
                    setSaveBtnState();

                    //footer
                    userCode = data.getCode();
                    contactEmail = question.getContactEmail();
                    footer.setVisibility(StringUtils.hasEmpty(contactEmail) ? View.GONE : View.VISIBLE);
                }
            }

            @Override
            public void onDataError(@NonNull Throwable t) {
                Log.e(TAG, "onDataError: " + t.getLocalizedMessage());
                //Should quit, go back, I think. Kawai..
            }
        };
    }

    public int getQuestionId() {
        if (getActivity() != null && getActivity() instanceof QuestionActivity)
            return ((QuestionActivity) getActivity()).getQuestionId();
        else {
            Log.d(TAG, "getQuestionId Wrong Activity");
            return -1;
        }
    }

    private BottomSheet b;

    @OnClick(R.id.question_media_clicker)
    public void onMediaClicked() {
        final Intent galleryIntent = getGalleryIntent();
        int appsForGallery = ActivityUtils.getNumActivitiesForIntent(getContext(), galleryIntent);

        b = new BottomSheet.Builder(getActivity())
                .title(R.string.question_bottom_media)
                .sheet(appsForGallery == 0
                        ? R.menu.bottom_question
                        : R.menu.bottom_question_with_gallery)
                .disableAutoDismiss(true)
                .listener(new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case R.id.action_media_video:
                                startMediaCapture(REQ_MEDIA_VIDEO);
                                break;
                            case R.id.action_media_photo:
                                startMediaCapture(REQ_MEDIA_PHOTO);
                                break;
                            case R.id.action_media_gallery:
                                startActivityForResult(galleryIntent, REQ_MEDIA_GALLERY);
                                break;
                        }
                    }
                }).show();
    }

    private Intent getGalleryIntent() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/* video/*");
        return intent;
    }

    private boolean permissionsAreInOrder(int requestCode, String... permissions) {
        if (permissions == null || permissions.length == 0)
            return true;
        else {
            ArrayList<String> failedPermissions = new ArrayList<>();
            for (String p : permissions) {
                if (ContextCompat.checkSelfPermission(getContext(), p) != PackageManager.PERMISSION_GRANTED)
                    failedPermissions.add(p);
            }
            Log.d(TAG, "failedPermissions: " + failedPermissions);
            if (failedPermissions.size() == 0)
                return true;
            else {
                requestPermissions(StringUtils.toArray(failedPermissions), requestCode);
                return false;
            }
        }
    }

    private void startMediaCapture(int req) {
        File saveFolder;
        if (req == REQ_MEDIA_PHOTO) {
            if (permissionsAreInOrder(req,
                    Manifest.permission.CAMERA,
//                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                Log.d(TAG, "startMediaCapture: PHOTO");
                saveFolder = new File(Environment.getExternalStorageDirectory(), "Seeviews Photos");
                new MaterialCamera(this)
                        .saveDir(saveFolder)
                        .iconStillshot(R.drawable.ic_media_capture_white)
                        .labelRetry(R.string.question_media_label_retry)
                        .labelConfirm(R.string.question_media_label_confirm_photo)
                        /** all the previous methods can be called, but video ones would be ignored */
                        .stillShot() // launches the Camera in stillshot mode
                        .additionalCommentQuestion(question.getTitle())
                        .start(req);
            }
        } else if (req == REQ_MEDIA_VIDEO) {
            if (permissionsAreInOrder(req,
                    Manifest.permission.CAMERA,
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                Log.d(TAG, "startMediaCapture: VIDEO");
                saveFolder = new File(Environment.getExternalStorageDirectory(), "Seeviews Videos");
                new MaterialCamera(this)                                   // Constructor takes an Activity
//                        .allowRetry(true)                                  // Whether or not 'Retry' is visible during playback
//                        .autoSubmit(false)                                 // Whether or not user is allowed to playback videos after recording. This can affect other things, discussed in the next section.
                        .saveDir(saveFolder)                               // The folder recorded videos are saved to
                        .primaryColorAttr(R.attr.colorPrimary)             // The theme color used for the camera, defaults to colorPrimary of Activity in the constructor
                        .showPortraitWarning(true)                         // Whether or not a warning is displayed if the user presses record in portrait orientation
//                        .defaultToFrontFacing(false)                       // Whether or not the camera will initially show the front facing camera
//                        .allowChangeCamera(true)                           // Allows the user to change cameras.
//                        .retryExits(false)                                 // If true, the 'Retry' button in the playback screen will exit the camera instead of going back to the recorder
//                        .restartTimerOnRetry(false)                        // If true, the countdown timer is reset to 0 when the user taps 'Retry' in playback
//                        .continueTimerInPlayback(false)                    // If true, the countdown timer will continue to go down during playback, rather than pausing.
//                        .videoEncodingBitRate(1024000)                     // Sets a custom bit rate for video recording.
//                        .audioEncodingBitRate(50000)                       // Sets a custom bit rate for audio recording.
                        .videoFrameRate(24)                                // Sets a custom frame rate (FPS) for video recording.
                        .qualityProfile(MaterialCamera.QUALITY_480P)       // Sets a quality profile, manually setting bit rates or frame rates with other settings will overwrite individual quality profile settings
//                        .videoPreferredHeight(480)                         // Sets a preferred height for the recorded video output.
                        .videoPreferredAspect(16f / 9f)                     // Sets a preferred aspect ratio for the recorded video output.
//                        .maxAllowedFileSize(1024 * 1024 * 5)               // Sets a max file size of 5MB, recording will stop if file reaches this limit. Keep in mind, the FAT file system has a file size limit of 4GB.
                        .iconRecord(R.drawable.ic_media_capture_red)        // Sets a custom icon for the button used to start recording
                        .iconStop(R.drawable.ic_media_stop)             // Sets a custom icon for the button used to stop recording
                        .iconFrontCamera(R.drawable.mcam_camera_front)     // Sets a custom icon for the button used to switch to the front camera
                        .iconRearCamera(R.drawable.mcam_camera_rear)       // Sets a custom icon for the button used to switch to the rear camera
//                        .iconPlay(R.drawable.evp_action_play)              // Sets a custom icon used to start playback
//                        .iconPause(R.drawable.evp_action_pause)            // Sets a custom icon used to pause playback
//                        .iconRestart(R.drawable.evp_action_restart)        // Sets a custom icon used to restart playback
                        .labelRetry(R.string.question_media_label_retry)                   // Sets a custom button label for the button used to retry recording, when available
                        .labelConfirm(R.string.question_media_label_confirm_video)             // Sets a custom button label for the button used to confirm/submit a recording
//                        .autoRecordWithDelaySec(5)                         // The video camera will start recording automatically after a 5 second countdown. This disables switching between the front and back camera initially.
//                        .autoRecordWithDelayMs(5000)                       // Same as the above, expressed with milliseconds instead of seconds.
//                        .audioDisabled(false)                              // Set to true to record video without any audio.
//                        .countdownMinutes(2.5f)
                        .additionalCommentQuestion(question.getTitle())
                        .start(req);                                 // Starts the camera activity, the result will be sent back to the current Activity
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d(TAG, "onRequestPermissionsResult: ");
        boolean allGranted = true;
        for (int i = 0; i < permissions.length; i++) {
            Log.d(TAG, "- [" + grantResults[i] + "] " + permissions[i]);
            if (grantResults[i] != PackageManager.PERMISSION_GRANTED)
                allGranted = false;
        }
        if (allGranted)
            if (requestCode == REQ_MEDIA_SAVE)
                saveReviewOnline(new Runnable() {
                    @Override
                    public void run() {
                    }
                });
            else
                startMediaCapture(requestCode);
        else
            Log.d(TAG, "onRequestPermissionsResult: NOT ALL PERMISSIONS GRANTED");
    }

    @OnClick(R.id.question_footer_link)
    public void onFooterLinkClicked() {
        String body = "\n\n\n" + getString(R.string.question_contact_email_footer) + userCode;
        String chooser = getString(R.string.question_contact_email_chooser);

        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", contactEmail, null));
        emailIntent.putExtra(Intent.EXTRA_TEXT, body);
        startActivity(Intent.createChooser(emailIntent, chooser));
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        Log.d(TAG, "onSaveInstanceState: ");
        super.onSaveInstanceState(outState);
        collectChangedAnswer();
        outState.putParcelable(KEY_ANSWER, answer);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: " + requestCode);

        if (resultCode == Activity.RESULT_OK) {
            Log.d(TAG, "onActivityResult: RESULT_OK");
            if (b != null)
                b.dismiss();

            if (requestCode == REQ_MEDIA_GALLERY) {
                //TODO maybe allow the addition of a comment?

                Uri selectedMediaUri = data.getData();
                if (selectedMediaUri.toString().contains("images")) {
                    //handle image
                    Log.d(TAG, "onActivityResult: IMAGE");
                } else if (selectedMediaUri.toString().contains("video")) {
                    //handle video
                    Log.d(TAG, "onActivityResult: VIDEO");
                }
                String[] filePathColumn = {MediaStore.Images.Media.DATA};

                Cursor cursor = getContext().getContentResolver().query(
                        selectedMediaUri, filePathColumn, null, null, null);
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String filePath = cursor.getString(columnIndex);
                cursor.close();
                File file = new File(filePath);

//                Toast.makeText(getContext(), String.format("Got Gallery image: %s, size: %s",
//                        file.getAbsolutePath(), StringUtils.fileSize(file)), Toast.LENGTH_LONG).show();

                answer.setComment(data.getStringExtra(MaterialCamera.EXTRA_COMMENT));
                answer.setImageLoc(file.getAbsolutePath());
                answer.setVideoLoc(null);
                onHeaderResourcesChange();

            } else if (requestCode == REQ_MEDIA_VIDEO) {
                final File file = new File(data.getData().getPath());
//                Toast.makeText(getContext(), String.format("Saved video to: %s, size: %s",
//                        file.getAbsolutePath(), StringUtils.fileSize(file)), Toast.LENGTH_LONG).show();

                answer.setComment(data.getStringExtra(MaterialCamera.EXTRA_COMMENT));
                answer.setImageLoc(null);
                answer.setVideoLoc(file.getAbsolutePath());
                onHeaderResourcesChange();

            } else if (requestCode == REQ_MEDIA_PHOTO) {
                final File file = new File(data.getData().getPath());
//                Toast.makeText(getContext(), String.format("Saved photo to: %s, size: %s",
//                        file.getAbsolutePath(), StringUtils.fileSize(file)), Toast.LENGTH_LONG).show();

                answer.setComment(data.getStringExtra(MaterialCamera.EXTRA_COMMENT));
                answer.setImageLoc(file.getAbsolutePath());
                answer.setVideoLoc(null);
                onHeaderResourcesChange();
            }
        } else if (data != null) {
            Exception e = (Exception) data.getSerializableExtra(MaterialCamera.ERROR_EXTRA);
            if (e != null) {
                e.printStackTrace();
                //TODO handle more elegant
                Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        } else {
            //probably back pressed
        }
    }

    private void setSaveBtnState() {
        TransitionManager.beginDelayedTransition(allContainer);
        boolean changesToSave = answerHasBeenChanged();
        Log.d(TAG, "setSaveBtnState: changesToSave: " + changesToSave);
        saveBtn.setEnabled(changesToSave);
        saveBtn.setClickable(changesToSave);
        saveBtn.setText(changesToSave
                ? R.string.question_action_save
                : R.string.question_action_save_disabled);
        saveBtn.setTextColor(ContextCompat.getColor(getContext(), changesToSave
                ? R.color.colorPrimary
                : R.color.colorBtnDisabledText));
    }

    private void onHeaderResourcesChange() {
        if (!setHeaderImageFromInput() && !setHeaderImageFromReview()) {
            if (imageSwitcher == null)
                imageSwitcher = new ImageSwitcher(this,
                        headerImage,
                        authHeader,
                        NetworkUtils.ImageType.QUESTION_IMAGE,
                        R.drawable.bg_header_fallback,
                        question.getImages());

            if (imageSwitcher.canStart())
                imageSwitcher.start();
            else
                headerImage.setImageResource(R.drawable.bg_header_fallback);
        }
        setMediaClearState();
        setSaveBtnState();
    }

    private boolean setHeaderImageFromInput() {
        if (answer != null) {
            File imageFile = null;
            if (StringUtils.isNotEmpty(answer.getVideoLoc())) {
                imageFile = new File(answer.getVideoLoc());
            } else if (StringUtils.isNotEmpty(answer.getImageLoc()))
                imageFile = new File(answer.getImageLoc());

            if (imageFile != null && imageFile.exists()) {
                if (imageSwitcher != null)
                    imageSwitcher.stop();

                Glide.with(this)
                        .load(imageFile)
                        .error(R.drawable.bg_header_fallback)
                        .into(headerImage);
                return true;
            }
        }
        return false;
    }

    private boolean setHeaderImageFromReview() {
        if (question != null && question.getReview() != null) {
            Review r = question.getReview();
            GlideUrl url = null;
            if (StringUtils.isNotEmpty(r.getImage())) {
                url = NetworkUtils.getAuthenticatedGlideUrl(getContext(), authHeader, NetworkUtils.ImageType.REVIEW_IMAGE, r.getImage());
            } else if (StringUtils.isNotEmpty(r.getVideo())) {
                url = NetworkUtils.getAuthenticatedGlideUrl(getContext(), authHeader, NetworkUtils.ImageType.REVIEW_VIDEO, r.getVideo());
            }

            if (url != null) {
                Glide.with(this)
                        .load(url)
                        .error(R.drawable.bg_header_fallback)
                        .into(headerImage);
                return true;
            }
        }
        return false;
    }

    private void setMediaClearState() {
        boolean hasMedia = question.getReview() != null && question.getReview().hasMedia();
        boolean hasAnswerMedia = answer != null && !answer.hasNoMedia();
        if (hasAnswerMedia) {
            mediaText.setText(R.string.question_media_label_edit);
            mediaClearBtn.setVisibility(View.VISIBLE);
        } else {
            mediaText.setText(R.string.question_media_label_new);
            mediaClearBtn.setVisibility(View.GONE);
        }
    }

    @OnClick(R.id.question_save_btn)
    public void onSaveReviewClicked() {
        saveReviewOnline(new Runnable() {
            @Override
            public void run() {
                snack(R.string.question_snack_save_completed);
            }
        });
    }

    public void saveReviewOnline(@NonNull final Runnable runOnFinish) {
        showSavingDialog();
        collectChangedAnswer();
        if (answer.hasNoMedia() || permissionsAreInOrder(REQ_MEDIA_SAVE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            getApp().postSentiment(answer, new NetworkUtils.SentimentListener() {
                @Override
                public void onSentimentSuccess(Question q) {
                    Log.d(TAG, "onSentimentSuccess: ");
                    question = q;
                    answer = new Answer(q);
                    onHeaderResourcesChange();
                    setSaveBtnState();
                    hideSavingDialog();
                    runOnFinish.run();
                }

                @Override
                public void onSentimentFail(Throwable t) {
                    Log.e(TAG, "onSentimentFail: " + t.getLocalizedMessage());
                    //TODO snack 'Error saving'
                    hideSavingDialog();
                    snack(R.string.question_snack_save_failed);
                }
            });
        }
    }

    public boolean answerHasBeenChanged() {
        collectChangedAnswer();
        boolean changed = question != null && answer != null && answer.hasBeenChangedFrom(question);

//        Log.d(TAG, "hasBeenChangedFrom: " + changed
//                + "\n- question       : " + question
//                + "\n- answer         : " + answer);

        return changed;
    }

    protected void collectChangedAnswer() {
        if (answer != null && seekBars != null) {
            //sliders
            for (int i = 0; i < seekBars.size(); i++) {
                int key = seekBars.keyAt(i);
                DiscreteSeekBar s = seekBars.get(key);
                int value = s.getProgress();
                answer.setSliderValue(key, value);
            }
        }
    }

    private static final String TAG_SAVING_FRAGMENT = "fragment_save";

    private void showSavingDialog() {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        QuestionSaveDialogFragment editNameDialogFragment = QuestionSaveDialogFragment.newInstance();
        editNameDialogFragment.show(fm, TAG_SAVING_FRAGMENT);
    }

    private void hideSavingDialog() {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        QuestionSaveDialogFragment f = (QuestionSaveDialogFragment) fm.findFragmentByTag(TAG_SAVING_FRAGMENT);
        if (f != null)
            f.getDialog().dismiss();

    }
}
