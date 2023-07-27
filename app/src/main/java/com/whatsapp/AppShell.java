package com.whatsapp;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.PowerManager;
import android.util.Log;

import androidx.core.content.ContextCompat;

public class AppShell extends Application {
    public static final String TAG = "AppShell";
    private PowerManager.WakeLock wlLock;
    @Override
    public void onCreate() {
        try {
            PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
            wlLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK|PowerManager.ON_AFTER_RELEASE, "com.whatsapp:WhatsAppAcc");
            wlLock.acquire();
            Log.d(TAG,"ALIBABA onCreate");
        }catch (Exception e){
            e.printStackTrace();
        }

        ContextCompat.startForegroundService(this,new Intent(getApplicationContext(), testService.class));
        super.onCreate();
    }

    @Override
    public void onTerminate() {
        wlLock.release();
        Log.d(TAG,"ALIBABA onTerminate");
        super.onTerminate();
    }
}
