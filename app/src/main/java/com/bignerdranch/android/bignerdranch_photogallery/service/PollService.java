package com.bignerdranch.android.bignerdranch_photogallery.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class PollService extends IntentService {
    private static final String TAG = "PollService";

    public PollService() {
        super(TAG);
    }

    public static Intent newIntent(Context context) {
        return new Intent(context, PollService.class);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.i(TAG, "onHandleIntent: Received an intent: " + intent);
    }
}
