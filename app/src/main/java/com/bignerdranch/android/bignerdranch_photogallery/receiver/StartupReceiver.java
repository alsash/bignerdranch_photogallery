package com.bignerdranch.android.bignerdranch_photogallery.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.bignerdranch.android.bignerdranch_photogallery.service.PollService;
import com.bignerdranch.android.bignerdranch_photogallery.utils.QueryPreferences;

public class StartupReceiver extends BroadcastReceiver {
    private static final String TAG = "StartupReceiver";


    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "onReceive: Received broadcast intent: " + intent.getAction());

        boolean isOn = QueryPreferences.isAlarmOn(context);
        PollService.setServiceAlarm(context, isOn);
    }
}
