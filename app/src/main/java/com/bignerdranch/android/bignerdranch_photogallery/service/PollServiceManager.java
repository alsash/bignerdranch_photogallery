package com.bignerdranch.android.bignerdranch_photogallery.service;

import android.content.Context;
import android.os.Build;

public class PollServiceManager {

    public static boolean isServiceOn(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return PollJobService.isServiceScheduled(context);
        } else {
            return PollService.isServiceAlarmOn(context);
        }
    }

    public static void setupService(Context context, boolean isOn) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            PollJobService.scheduleService(context, isOn);
        } else {
            PollService.setServiceAlarm(context, isOn);
        }
    }

}
