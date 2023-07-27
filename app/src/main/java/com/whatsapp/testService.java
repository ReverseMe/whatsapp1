package com.whatsapp;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationCompat;

public class testService extends Service {
    public testService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        try {
            if (Build.VERSION.SDK_INT >= 26) {
                startForeground(8866, createNotification(this, getPackageName() + 8866));
            }
        } catch (Throwable e) {
            e.printStackTrace();
            if (Build.VERSION.SDK_INT >= 31){
                Log.e("BaseService", "startForeground failed", e);
                getSystemService(NotificationManager.class).notify(8866,createNotification(this, getPackageName() + 8866));

            }
        }
    }

    public static void createNotificationChannel(Context context, NotificationChannel channel) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // 设置其他通知渠道属性，如声音、震动等
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    public Notification createNotification(Context context, String str) {
        Log.d("BaseService","createNotification");
        NotificationCompat.Builder content = new NotificationCompat.Builder(context, str).setWhen(System.currentTimeMillis()).setPriority(-2).setOngoing(true);
        if (Build.VERSION.SDK_INT >= 26) {
            // NotificationManagerCompat.from(context).createNotificationChannel(new NotificationChannel(str, context.getPackageName(), NotificationManager.IMPORTANCE_MIN));
            createNotificationChannel(context,new NotificationChannel(str, context.getPackageName(), NotificationManager.IMPORTANCE_HIGH));
            //NotificationManagerCompat.from(context).notify(Const.ID_FOREGROUND,content.build());

        }
        return content.build();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}