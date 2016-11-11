package com.bignerdranch.android.bignerdranch_photogallery.ui;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.view.MenuItem;

import com.bignerdranch.android.bignerdranch_photogallery.R;

public class PhotoPageActivity extends SingleFragmentActivity {

    public static Intent newIntent(Context context, Uri photoPageUri) {
        return new Intent(context, PhotoPageActivity.class)
                .setData(photoPageUri);
    }

    @Override
    protected Fragment createFragment() {
        return PhotoPageFragment.newInstance(getIntent().getData());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        boolean goBack = true;
        OnBackPressedListener listener;
        try {
            listener = (OnBackPressedListener)
                    getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        } catch (ClassCastException cce) {
            super.onBackPressed();
            return;
        }
        if (listener != null) {
            goBack = listener.onBackPressedGoBack();
        }
        if (goBack) {
            super.onBackPressed();
        }
    }

    public interface OnBackPressedListener {
        boolean onBackPressedGoBack();
    }
}
