package com.seeviews.ui.gift;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.seeviews.R;
import com.seeviews.SeeviewApplication;
import com.seeviews.SeeviewFragment;
import com.seeviews.model.api.receive.Incentive;
import com.seeviews.model.internal.BaseModel;
import com.seeviews.utils.ActivityUtils;
import com.seeviews.utils.NetworkUtils;
import com.seeviews.utils.PrefUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A placeholder fragment containing a simple view.
 */
public class GiftActivityFragment extends SeeviewFragment {

    private static final String TAG = "GiftActivityFragment";
    @BindView(R.id.gift_empty)
    View empty;
    @BindView(R.id.gift_empty_description)
    TextView emptyDescription;
    @BindView(R.id.gift_content)
    View content;
    @BindView(R.id.gift_header_image)
    ImageView headerImage;
    @BindView(R.id.gift_title)
    TextView title;
    @BindView(R.id.gift_description)
    TextView description;

    public GiftActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_gift, container, false);
        ButterKnife.bind(this, rootView);

        headerImage.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                PrefUtils.getHeaderHeight(getContext())));
        empty.setVisibility(View.GONE);
        content.setVisibility(View.GONE);
        empty.setPadding(0, 0, 0, ActivityUtils.getNavigationBarHeight(getContext(), false));
        content.setPadding(0, 0, 0, ActivityUtils.getNavigationBarHeight(getContext(), false));
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if(savedInstanceState == null)
            refreshData();
    }

    @Override
    protected void refreshData() {
        super.refreshData();
        Log.d(TAG, "refreshData: ");
        getApp().refreshIncentive(this);
    }

    @Override
    protected SeeviewApplication.DataListener defineDataListener() {
        return new SeeviewApplication.DataListener() {
            @Override
            public void onDataLoaded(@NonNull BaseModel data) {
                Incentive i = data.getIncentive();
                boolean allQuestionsAnswered = data.allQuestionsAreAnswered();
                boolean giftEarned = allQuestionsAnswered && i != null && i.hasContentToShow();

                empty.setVisibility(giftEarned ? View.GONE : View.VISIBLE);
                content.setVisibility(giftEarned ? View.VISIBLE : View.GONE);

                if (giftEarned) {
                    title.setText(i.getTitle());
                    description.setText(i.getDescription());

                    GlideUrl glideUrl = NetworkUtils.getAuthenticatedGlideUrl(getContext(), data.getAuthHeader(), NetworkUtils.ImageType.INCENTIVE_IMAGE, i.getImage());
                    Glide.with(GiftActivityFragment.this)
                            .load(glideUrl)
                            .into(headerImage);
                } else {
                    emptyDescription.setText(allQuestionsAnswered
                            ? R.string.gift_empty_subtitle_empty
                            : R.string.gift_empty_subtitle);
                }
            }

            @Override
            public void onDataError(@NonNull Throwable t) {
                empty.setVisibility(View.VISIBLE);
                content.setVisibility(View.GONE);
            }
        };
    }
}
