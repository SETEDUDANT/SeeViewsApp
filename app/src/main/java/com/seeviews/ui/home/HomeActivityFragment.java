package com.seeviews.ui.home;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.seeviews.R;
import com.seeviews.SeeviewApplication;
import com.seeviews.SeeviewFragment;
import com.seeviews.model.api.receive.Hotel;
import com.seeviews.model.api.receive.Question;
import com.seeviews.model.api.receive.Review;
import com.seeviews.model.internal.BaseModel;
import com.seeviews.utils.ImageSwitcher;
import com.seeviews.utils.NetworkUtils;
import com.seeviews.utils.PrefUtils;
import com.seeviews.utils.StringUtils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A placeholder fragment containing a simple view.
 */
public class HomeActivityFragment extends SeeviewFragment {

    private static final String TAG = "HomeActivityFragment";

    //@BindView(R.id.home_header_image)
    //ImageView headerImage;
    @BindView(R.id.home_header_name)
    TextView headerName;
    @BindView(R.id.home_list)
    RecyclerView list;
    @BindView(R.id.home_review_btn)
    TextView reviewBtn;

    @BindView(R.id.home_reviews)
    View reviews;
    @BindView(R.id.home_empty)
    View empty;

    ImageSwitcher imageSwitcher;

    public HomeActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        ButterKnife.bind(this, rootView);

        list.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        llm.setOrientation(LinearLayoutManager.HORIZONTAL);
        list.setLayoutManager(llm);

        reviewBtn.setVisibility(View.GONE);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final View header = view.findViewById(R.id.home_header);
        header.post(new Runnable() {
            @Override
            public void run() {
                PrefUtils.setHeaderHeight(getContext(), header.getHeight());
            }
        });
    }

    @OnClick(R.id.home_review_btn)
    public void onReviewBtnClicked() {
        if (getActivity() != null && getActivity() instanceof HomeActivity)
            ((HomeActivity) getActivity()).startReview();
    }

    @Override
    protected void refreshData() {
        super.refreshData();
        Log.d(TAG, "refreshData: ");
        getApp().refreshUser(this);
    }

    @Override
    protected SeeviewApplication.DataListener defineDataListener() {
        if (getDataListener() != null)
            return getDataListener();
        else
            return new SeeviewApplication.DataListener() {
                @Override
                public void onDataLoaded(@NonNull BaseModel data) {
                    Log.d(TAG, "onDataLoaded");
                    Hotel h = data.getHotel();

                    //header name
                    String hotelName = h.getName();
                    if (StringUtils.hasEmpty(hotelName))
                        hotelName = getString(R.string.home_header_fallback_name);
                    headerName.setText(hotelName);

                    //header image
                    /*if (h.getImages().size() > 0) {
                        if (imageSwitcher == null) {
                            imageSwitcher = new ImageSwitcher(HomeActivityFragment.this,
                                    headerImage,
                                    data.getAuthHeader(),
                                    NetworkUtils.ImageType.DASHBOARD_IMAGE,
                                    R.drawable.bg_header_fallback,
                                    h.getImages());
                            imageSwitcher.start();
                        }
                    } else {
                        headerImage.setImageResource(R.drawable.bg_header_fallback);
                    }*/

                    //reviews
                    ArrayList<Review> rs = h.getReviews();
                    if (rs.size() == 0) {
                        empty.setVisibility(View.VISIBLE);
                        reviews.setVisibility(View.GONE);
                    } else {
                        list.setAdapter(new ReviewAdapter(HomeActivityFragment.this, rs, data.getAuthHeader()));
                        empty.setVisibility(View.GONE);
                        reviews.setVisibility(View.VISIBLE);
                    }

                    //questions
                    boolean hasSavedAnsweredBefore = false;
                    ArrayList<Question> questions = data.getQuestions();
                    if (questions == null || questions.size() == 0) {
                        //TODO obviously something has gone wrong, logout as well?
                    } else {
                        for (Question q : questions) {
                            if (q.hasSavedUserInput()) {
                                hasSavedAnsweredBefore = true;
                                break;
                            }
                        }
                    }

                    boolean isComplete = data.allQuestionsAreAnswered();

                    reviewBtn.setText(isComplete ? R.string.home_review_btn_completed : hasSavedAnsweredBefore ? R.string.home_review_btn_continue : R.string.home_review_btn_new);
                    reviewBtn.setVisibility(View.VISIBLE);
                }

                @Override
                public void onDataError(@NonNull Throwable t) {
                    Log.e(TAG, "onDataError: " + t.getLocalizedMessage());
                    empty.setVisibility(View.VISIBLE);
                    reviews.setVisibility(View.GONE);
                    //TODO logout, basically,
                }
            };
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (imageSwitcher != null)
            imageSwitcher.stop();
    }
}
