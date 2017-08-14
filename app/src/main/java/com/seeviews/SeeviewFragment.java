package com.seeviews;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;

import com.seeviews.model.internal.BaseModel;

/**
 * Created by Jan-Willem on 3-12-2016.
 */

public abstract class SeeviewFragment extends Fragment implements SeeviewApplication.RefreshListener {

    private static final String TAG = "SeeviewFragment";

    public void registerDataListener(@NonNull SeeviewApplication.DataListener listener) {
        if (getActivity() != null && getActivity() instanceof SeeviewActivity)
            ((SeeviewActivity) getActivity()).registerDataListener(listener);
        else
            Log.w(TAG, "registerDataListener:  Not the correct parent :(");
    }

    public void unregisterDataListener(@NonNull SeeviewApplication.DataListener listener) {
        if (getActivity() != null && getActivity() instanceof SeeviewActivity)
            ((SeeviewActivity) getActivity()).unregisterDataListener(listener);
        else
            Log.w(TAG, "registerDataListener:  Not the correct parent :(");
    }


    public SeeviewApplication getApp() {
        if (getActivity() != null && getActivity() instanceof SeeviewActivity)
            return ((SeeviewActivity) getActivity()).getApp();
        else
            return null;
    }

    SeeviewApplication.DataListener dataListener;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.dataListener = defineDataListener();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        registerDataListener(dataListener);

        if (savedInstanceState != null)
            refreshData();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unregisterDataListener(dataListener);
    }

    protected void refreshData() {
        // Let subFragments implement this, if they please
    }

    public void onRefreshComplete() {
        getApp().assumeDataLoaded(getDataListener());
    }

    protected SeeviewApplication.DataListener getDataListener() {
        return dataListener;
    }

    protected SeeviewApplication.DataListener defineDataListener() {
        // Let subFragments implement this, if they please
        return new SeeviewApplication.DataListener() {
            @Override
            public void onDataLoaded(@NonNull BaseModel data) {
                Log.d(TAG, "onDataLoaded: ");
            }

            @Override
            public void onDataError(@NonNull Throwable t) {
                Log.d(TAG, "onDataError: " + t.getLocalizedMessage());
            }
        };
    }

    public void snack(int textRes) {
        snack(getString(textRes));
    }

    public void snack(String text) {
        if (getActivity() != null && getActivity() instanceof SeeviewActivity)
            ((SeeviewActivity) getActivity()).snack(text);
        else
            Log.w(TAG, "snack:  Not the correct parent :(");
    }
}
