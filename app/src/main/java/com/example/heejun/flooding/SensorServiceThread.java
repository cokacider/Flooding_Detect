package com.example.heejun.flooding;

import android.os.Handler;

/**
 * Created by HEEJUN on 2017-01-12.
 */

public class SensorServiceThread extends Thread {
    private static long THREAD_DELAY = 1000;
    private Handler handler;
    private boolean isRun = true;

    public SensorServiceThread(Handler handler) {
        this.handler = handler;
    }

    public void stopForever() {
        synchronized (this) {
            this.isRun = false;
        }

    }

    @Override
    public void run() {
        while(isRun) {
            handler.sendEmptyMessage(0);
            try {
                Thread.sleep(THREAD_DELAY);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void setThreadDelay(long threadDelay) {
        THREAD_DELAY = threadDelay;
    }
}
