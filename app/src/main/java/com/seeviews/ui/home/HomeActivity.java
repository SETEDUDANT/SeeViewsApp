package com.seeviews.ui.home;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.seeviews.R;
import com.seeviews.SeeviewActivity;
import com.seeviews.model.api.receive.UserResponse;
import com.seeviews.ui.gift.GiftActivity;
import com.seeviews.ui.init.InitActivity;
import com.seeviews.ui.personal_info.activity_personalinfo;
import com.seeviews.ui.review.ReviewActivity;
import com.seeviews.utils.ActivityUtils;
import com.seeviews.utils.NetworkUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import eightbitlab.com.blurview.BlurView;

public class HomeActivity extends SeeviewActivity {

    private static final String TAG = "HomeActivity";

    public static void start(Activity a) {
        if (a != null) {
            Intent i = new Intent(a, HomeActivity.class);
            a.startActivity(i);
            a.finish();
        }
    }
    Toolbar toolbar;
    TextView free_gifts;
    TextView my_details;

    @BindView(R.id.home_coordinator)
    CoordinatorLayout coordinator;

    @BindView(R.id.home_blurView)
    BlurView blurView;

    @BindView(R.id.home_review_btn)
    View reviewBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);
        //setupBlurForToolbar(coordinator, blurView);

        toolbar = (Toolbar) findViewById(R.id.toolbar_homee);
        free_gifts = (TextView) toolbar.findViewById(R.id.free_gifts);
        my_details = (TextView) toolbar.findViewById(R.id.my_details);

        free_gifts.setOnClickListener(buttonHandler);
        my_details.setOnClickListener(buttonHandler);

        //int btnPadding = getResources().getDimensionPixelSize(R.dimen.home_btn_padding);
        //reviewBtn.setPadding(btnPadding, btnPadding, btnPadding, btnPadding + ActivityUtils.getNavigationBarHeight(this, false));
    }

    View.OnClickListener buttonHandler = new View.OnClickListener()
    {
        @Override
        public void onClick(View view) {
            switch (view.getId())
            {
                case R.id.free_gifts:
                    freeGiftOnClick(view);
                    break;
                case R.id.my_details:
                    a(view);
                    break;
            }
        }
    };



    public void startReview() {
        ReviewActivity.start(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public void freeGiftOnClick(View v)
    {
        GiftActivity.start(this);
    }

    public void a(View v)
    {
        Intent Intent = new Intent(HomeActivity.this, activity_personalinfo.class);
        startActivity(Intent);
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_gifts) {
            GiftActivity.start(this);
            return true;
        } else if (item.getItemId() == R.id.action_logout) {
            new MaterialDialog.Builder(this)
                    .theme(Theme.LIGHT)
                    .title(R.string.home_logout_title)
                    .content(R.string.home_logout_message)
                    .positiveText(R.string.home_logout_pos)
                    .negativeText(R.string.home_logout_neg)
                    .positiveColor(ContextCompat.getColor(this, R.color.colorPrimaryDark))
                    .negativeColor(ContextCompat.getColor(this, R.color.dialog_btn))
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            getApp().burnData(new Runnable() {
                                @Override
                                public void run() {
                                    startActivity(new Intent(HomeActivity.this, InitActivity.class));
                                    finish();
                                }
                            });
                        }
                    })
                    .show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
