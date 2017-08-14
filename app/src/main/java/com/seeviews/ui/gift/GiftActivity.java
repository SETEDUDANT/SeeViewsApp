package com.seeviews.ui.gift;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.seeviews.R;
import com.seeviews.SeeviewActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

public class GiftActivity extends SeeviewActivity {

    private static final String TAG = "ReviewActivity";

    public static void start(Activity a) {
        if (a != null) {
            Intent i = new Intent(a, GiftActivity.class);
            a.startActivity(i);
        }
    }

    @BindView(R.id.gift_toolbar_wrapper)
    View toolbarWrapper;
    @BindView(R.id.gift_toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gift);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        setToolbarWrapperPadding(toolbarWrapper);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
