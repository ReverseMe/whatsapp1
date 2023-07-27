package com.whatsapp;

import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import java.util.concurrent.Flow;

public class MyJobService extends JobService {
    private static final String TAG = "taskjob";
    private static final int JOBID = 100;
    private static final long InterValTime = 10000;

    private static JobScheduler jobScheduler;
    private static JobInfo jobInfo = null;


    @Override
    public boolean onStartJob(@NonNull JobParameters params) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                    Log.i(TAG, "jobTest is start");
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();

        startScheduler(this);
        return false;
    }

    @Override
    public boolean onStopJob(@NonNull JobParameters params) {
        Log.i(TAG, "jobTest is over");
        return false;
    }

    public static void startScheduler(Context context) {
        jobScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        cancelScheduler();
        if (jobInfo == null) {
            jobInfo = new JobInfo.Builder(JOBID, new ComponentName(context, MyJobService.class))
                    .setMinimumLatency(InterValTime) // 最小为10秒
                    .build();
        }
       jobScheduler.schedule(jobInfo);
    }


    public static void cancelScheduler() {
        if (jobScheduler != null) {
            jobScheduler.cancel(JOBID);
        }
    }
}