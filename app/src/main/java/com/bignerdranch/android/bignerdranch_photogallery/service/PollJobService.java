package com.bignerdranch.android.bignerdranch_photogallery.service;

import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class PollJobService extends JobService {
    private static final String TAG = "PollJobService";

    private static final int POLL_JOB_ID = 1;

    private PollJobTask mPollTask;

    public static boolean isServiceScheduled(Context context) {
        boolean isScheduled = false;
        JobScheduler scheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        for (JobInfo info : scheduler.getAllPendingJobs()) {
            if (info.getId() == POLL_JOB_ID) {
                isScheduled = true;
                break;
            }
        }
        return isScheduled;
    }

    public static void scheduleService(Context context, boolean isOn) {
        JobScheduler scheduler = (JobScheduler) context.getSystemService(JOB_SCHEDULER_SERVICE);

        if (isOn) {
            JobInfo job = new JobInfo.Builder(
                    POLL_JOB_ID, new ComponentName(context, PollJobService.class))
                    .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                    .setPeriodic(PollService.POLL_INTERVAL_MS)
                    .setPersisted(true)
                    .build();
            int result = scheduler.schedule(job);
            if (result == JobScheduler.RESULT_SUCCESS) {
                Log.i(TAG, "scheduleService: success");
            } else {
                Log.i(TAG, "scheduleService: failure");
            }
        } else {
            scheduler.cancel(POLL_JOB_ID);
        }
    }

    @Override
    public boolean onStartJob(JobParameters params) {
        mPollTask = new PollJobTask();
        mPollTask.execute(params);
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        if (mPollTask != null) {
            mPollTask.cancel(true);
        }
        return true;
    }

    private class PollJobTask extends AsyncTask<JobParameters, Void, Void> {
        @Override
        protected Void doInBackground(JobParameters... params) {
            PollService.fetchPhotosAndSendNotification(PollJobService.this);
            jobFinished(params[0], false);
            return null;
        }
    }
}
