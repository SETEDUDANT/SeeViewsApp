package com.seeviews.ui.review;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.seeviews.R;
import com.seeviews.SeeviewActivity;
import com.seeviews.model.api.receive.Question;
import com.seeviews.ui.gift.GiftActivity;
import com.seeviews.utils.NetworkUtils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ReviewActivity extends SeeviewActivity {

    private static final String TAG = "ReviewActivity";

    public static void start(Activity a) {
        if (a != null) {
            Intent i = new Intent(a, ReviewActivity.class);
            a.startActivity(i);
        }
    }

    @BindView(R.id.questions_toolbar_wrapper)
    View toolbarWrapper;
    @BindView(R.id.questions_toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        setToolbarWrapperPadding(toolbarWrapper);
        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle("Back");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_review, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        } else if (item.getItemId() == R.id.action_gifts) {
            GiftActivity.start(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
