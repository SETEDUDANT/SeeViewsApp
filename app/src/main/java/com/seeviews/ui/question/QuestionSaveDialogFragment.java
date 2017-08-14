package com.seeviews.ui.question;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.util.Log;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.seeviews.R;

/**
 * Created by Jan-Willem on 16-12-2016.
 */

public class QuestionSaveDialogFragment extends DialogFragment {

    private static final String TAG = "QuestionSaveDialog";

    public static QuestionSaveDialogFragment newInstance() {
        return new QuestionSaveDialogFragment();
    }

    public QuestionSaveDialogFragment() {
        // Empty constructor required for DialogFragment
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        MaterialDialog d = new MaterialDialog.Builder(getContext())
                .theme(Theme.LIGHT)
//                .title(R.string.question_dialog_save_title)
                .customView(R.layout.dialog_question_save, false)
                .cancelable(false)
//                .progress(true, 0)
                .build();
        int width = getResources().getDimensionPixelSize(R.dimen.question_dialog_save_width);
        int height = getResources().getDimensionPixelSize(R.dimen.question_dialog_save_height);
        try {
            d.getWindow().setLayout(width, height);
        } catch (Exception e) {
            Log.e(TAG, "onCreateDialog: " + e.getLocalizedMessage());
        }
        return d;
    }
}
