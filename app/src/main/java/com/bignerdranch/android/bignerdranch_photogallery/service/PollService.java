package com.bignerdranch.android.bignerdranch_photogallery.service;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.bignerdranch.android.bignerdranch_photogallery.R;
import com.bignerdranch.android.bignerdranch_photogallery.data.FlickrFetchr;
import com.bignerdranch.android.bignerdranch_photogallery.data.GalleryItem;
import com.bignerdranch.android.bignerdranch_photogallery.ui.PhotoGalleryActivity;
import com.bignerdranch.android.bignerdranch_photogallery.utils.QueryPreferences;

import java.util.List;

public class PollService extends IntentService {

    static final int POLL_INTERVAL_MS = 1000 * 60;

    private static final String TAG = "PollService";

    public PollService() {
        super(TAG);
    }

    public static Intent newIntent(Context context) {
        return new Intent(context, PollService.class);
    }

    public static void setServiceAlarm(Context context, boolean isOn) {
        Intent intent = newIntent(context);
        PendingIntent pendingIntent = PendingIntent.getService(context, 0, intent, 0);

        AlarmManager alarmManager = (AlarmManager)
                context.getSystemService(ALARM_SERVICE);

        if (isOn) {
            alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME,
                    SystemClock.elapsedRealtime(), POLL_INTERVAL_MS, pendingIntent);
        } else {
            alarmManager.cancel(pendingIntent);
            pendingIntent.cancel();
        }

    }

    public static boolean isServiceAlarmOn(Context context) {
        Intent intent = newIntent(context);
        PendingIntent pendingIntent = PendingIntent.getService(context, 0, intent,
                PendingIntent.FLAG_NO_CREATE);
        return pendingIntent != null;
    }

    static synchronized void fetchPhotosAndSendNotification(Context context) {
        Log.i(TAG, "fetch Photos And Send Notification");

        String query = QueryPreferences.getSortedQuery(context);
        String lastResultId = QueryPreferences.getLastResultId(context);
        List<GalleryItem> items;

        if (query == null) {
            Log.i(TAG, "fetchPhotosAndSendNotification: Query is null");
            items = new FlickrFetchr().fetchRecentPhotos();
        } else {
            Log.i(TAG, "fetchPhotosAndSendNotification: Query = " + query);
            items = new FlickrFetchr().searchPhotos(query);
        }

        if (items.size() == 0) {
            Log.i(TAG, "fetchPhotosAndSendNotification: items.size = 0");
            return;
        }

        String resultId = items.get(0).getId();
        if (resultId.equals(lastResultId)) {
            Log.i(TAG, "Got an old result: " + resultId);
        } else {
            Log.i(TAG, "Got a new result: " + resultId);
        }

        Resources resources = context.getResources();
        Intent activityIntent = PhotoGalleryActivity.newIntent(context);
        PendingIntent activityPendingIntent = PendingIntent
                .getActivity(context, 0, activityIntent, 0);

        Notification notification = new NotificationCompat.Builder(context)
                .setTicker(resources.getString(R.string.new_pictures_title))
                .setSmallIcon(android.R.drawable.ic_menu_report_image)
                .setContentTitle(resources.getString(R.string.new_pictures_title))
                .setContentText(resources.getString(R.string.new_pictures_text))
                .setContentIntent(activityPendingIntent)
                .setAutoCancel(true)
                .build();
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(0, notification);
        Log.i(TAG, "fetchPhotosAndSendNotification: Notification send");

        QueryPreferences.setLastResultId(context, resultId);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (!isNetworkAvailableAndConnected()) return;

        Log.i(TAG, "onHandleIntent: Received an intent: " + intent);

        fetchPhotosAndSendNotification(this);
    }

    private boolean isNetworkAvailableAndConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);

        boolean isNetworkAvailable = cm.getActiveNetworkInfo() != null;
        boolean isNetworkConnected = isNetworkAvailable &&
                cm.getActiveNetworkInfo().isConnected();
        return isNetworkConnected;
    }
}
