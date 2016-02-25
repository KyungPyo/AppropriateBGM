package com.kp.appropriatebgm.LockScreen;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.telephony.TelephonyManager;

/**
 * Created by GD on 2016-02-11.
 */
public class LockScreenReceiver extends BroadcastReceiver {

    private boolean phoneState = false;

    // Method : 잠금화면 브로드캐스트 리시브
    // Return value : void
    // parameter : context(액티비티), intent(인텐트)
    // use ; 브로드캐스트를 받으면 LockScreenActivity로 인텐트를 넘겨 잠금화면을 띄운다.
    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences preferences = context.getSharedPreferences("AppSetting",
                Context.MODE_PRIVATE);

        String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);

        if (state != null && (TelephonyManager.EXTRA_STATE_OFFHOOK.equals(state)
                || intent.getAction().equals(Intent.ACTION_NEW_OUTGOING_CALL))) {
            phoneState = true;
        } else if (state != null && TelephonyManager.EXTRA_STATE_IDLE.equals(state)) {
            phoneState = false;
        }

        if (preferences.getBoolean("LockerOn", false)) {
            if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
                Intent i = new Intent(context, LockScreenService.class);
                context.startService(i);
            } else if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF) && !phoneState) {// 통화중이 아닐때

                Intent in = new Intent(context, LockScreenActivity.class);
                in.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                in.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

                context.startActivity(in);

            }
        }
    }
}
