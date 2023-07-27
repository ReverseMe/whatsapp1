package com.whatsapp;

import android.accessibilityservice.AccessibilityService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.PowerManager;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.Timer;
import java.util.TimerTask;

public class WhatsAppAcc extends AccessibilityService {
    public static final String TAG = "WhatsAppAcc:xxxx";


    private Socket socket;
    private BufferedReader reader;
    private OutputStream outputStream;
    private Timer reconnectTimer;

    private PowerManager.WakeLock wlLock;


    @Override
    protected void onServiceConnected() {
        Log.d(TAG, "sendPingMessage");
        super.onServiceConnected();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(new Intent(getApplicationContext(), testService.class));
        }
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wlLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK|PowerManager.ON_AFTER_RELEASE, "com.whatsapp:WhatsAppAcc");
        wlLock.acquire();
        startPingTimer();
    }

    private static final String SERVER_IP = "192.168.188.41";
    private static final int SERVER_PORT = 52113;
    private static final String PING_MESSAGE = "pingffff\n";
    private static final int NOTIFICATION_ID = 1;
    private static final int CONNECTION_TIMEOUT = 5000; // 连接超时时间，单位为毫秒
    private static final int RECONNECT_INTERVAL = 5000; // 重连间隔时间，单位为毫秒


    private  void connectSocket()  {

        try {
            socket = new Socket();
            socket.connect(new InetSocketAddress(SERVER_IP, SERVER_PORT));
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            outputStream = socket.getOutputStream();
        }catch (Exception e){
            e.printStackTrace();
        }

    }


    public  void startPingTimer() {
        Log.d(TAG, "startPingTimer");
        connectSocket();
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    try {
                        if (socket.isConnected()) {
                            // 发送Ping消息
                            Log.d(TAG, "sendPingMessage");
                            sendPingMessage();
                            // 接收服务器返回消息
                            String response = reader.readLine();
                            if (response != null) {
                                Log.d(TAG, "Server response: " + response);
                            }
                        } else {
                            // 如果连接断开，重新连接Socket
                            Log.d(TAG, "reconnectSocket");
                            reconnectSocket();
                        }
                        Thread.sleep(5000);
                    } catch (IOException | InterruptedException e) {
                        e.printStackTrace();
                    }

                }

            }
        }).start();
    }

    private  void sendPingMessage() throws IOException {
        try{
            outputStream.write(PING_MESSAGE.getBytes());
            outputStream.flush();
        }catch (Exception e){
            reconnectSocket();
        }

    }

    private   void reconnectSocket() throws IOException {
        closeSocket();
        connectSocket();
    }

    private   void closeSocket() {
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
            if (reader != null) {
                reader.close();
            }
            if (outputStream != null) {
                outputStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCreate() {


        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (reconnectTimer != null) {
            reconnectTimer.cancel();
        }
        closeSocket();

        if (wlLock != null) {
            wlLock.release();
            wlLock = null;
        }
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        Log.d("WhatsAppAcc","ZHIMAKAIMEN");
    }

    @Override
    public void onInterrupt() {
        Log.d("WhatsAppAcc","ZHIMAKAIMEN");
    }
}
