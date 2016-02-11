package com.kp.appropriatebgm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.kp.appropriatebgm.LockScreen.LockScreenService;

/**
 * Created by Choi on 2016-02-11.
 */
public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            Log.d("실행됨","ㅇㅇ");
            Intent i = new Intent(context, LockScreenService.class);
            Log.d("실행됨","intent OK");
            context.startService(i);
            Log.d("실행됨", "StartService OK");
        }
    }
}