package com.example.heejun.flooding;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by HEEJUN on 2017-01-12.
 */

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals(intent.ACTION_BOOT_COMPLETED)) {


            Intent i = new Intent(context, SensorService.class);
            context.startService(i);
        }
    }
}
