package com.seeviews.utils;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.seeviews.R;
import com.seeviews.model.api.receive.Preset;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;

/**
 * Created by Jan-Willem on 5-12-2016.
 */

public class QuestionSliderUtils {

    public interface onSliderChangeListener{
        void onSliderChange();
    }

    public static DiscreteSeekBar addSliderView(Context context, ViewGroup slidersContainer, Preset p, float answeredValue, final onSliderChangeListener listener) {
        final int maxSliderValue = 10;
        final int minSliderValue = 1;

        LayoutInflater inflater = LayoutInflater.from(context);
        final ViewGroup v = (ViewGroup) inflater.inflate(R.layout.listitem_question_slider, slidersContainer, false);
        ((TextView) v.findViewById(R.id.slider_title)).setText(p.getTitle());
        final TextView preview = (TextView) v.findViewById(R.id.slider_value_preview);
        final View negativeBtn = v.findViewById(R.id.slider_btn_neg);
        final View positiveBtn = v.findViewById(R.id.slider_btn_pos);
        final DiscreteSeekBar seekbar = (DiscreteSeekBar) v.findViewById(R.id.slider_value);

        seekbar.setMax(maxSliderValue);
        seekbar.setMin(minSliderValue);
        seekbar.setOnProgressChangeListener(new DiscreteSeekBar.OnProgressChangeListener() {
            @Override
            public void onProgressChanged(DiscreteSeekBar seekBar, int value, boolean fromUser) {
                adjustSeekbarForProgress(seekbar, preview);
                positiveBtn.setClickable(value < maxSliderValue);
                negativeBtn.setClickable(value > minSliderValue);

                if(listener!=null)
                    listener.onSliderChange();
            }

            @Override
            public void onStartTrackingTouch(DiscreteSeekBar seekBar) {
                TransitionManager.beginDelayedTransition(v);
                preview.setVisibility(View.GONE);
            }

            @Override
            public void onStopTrackingTouch(DiscreteSeekBar seekBar) {
                TransitionManager.beginDelayedTransition(v);
                preview.setVisibility(View.VISIBLE);
            }
        });
        positiveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                seekbar.setProgress(Math.min(maxSliderValue, seekbar.getProgress() + 1));
                adjustSeekbarForProgress(seekbar, preview);
            }
        });
        negativeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                seekbar.setProgress(Math.max(minSliderValue, seekbar.getProgress() - 1));
                adjustSeekbarForProgress(seekbar, preview);
            }
        });

        float progress = (int) p.getValue();
        if (answeredValue != 0)
            progress = answeredValue;
        if (progress == 0) //not filled in yet
            progress = maxSliderValue / 2;
        seekbar.setProgress((int) progress);
        adjustSeekbarForProgress(seekbar, preview);

        slidersContainer.addView(v);
        return seekbar;
    }

    private static void adjustSeekbarForProgress(DiscreteSeekBar seekbar, TextView preview) {
        Context context = seekbar.getContext();
        int progress = seekbar.getProgress();
        int color = RatingUtils.getRatingColor(progress);
        int c = ContextCompat.getColor(context, color);
        seekbar.setThumbColor(c, ContextCompat.getColor(context, R.color.ratingIndicator));
        seekbar.setScrubberColor(c);
        preview.setText("" + progress);
    }
}
