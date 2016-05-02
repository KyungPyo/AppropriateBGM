package com.kp.appropriatebgm.LockScreen;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.kp.appropriatebgm.Setting.NotiPlayer;

/**
 * Created by Choi on 2016-02-15.
 */
public class PackageUpdateReceiver extends BroadcastReceiver {

    // Method : 잠금화면 브로드캐스트 리시브
    // Return value : void
    // parameter : context(액티비티), intent(인텐트)
    // use ; 애플리케이션 업데이트 시 발생하는 브로드 캐스트 리시버. LockScreenReceiver
    //       에서 해보려고 했으나 Boot Complete 와 충돌이 생기는 것으로 보임
    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences preferences = context.getSharedPreferences("AppSetting",
                Context.MODE_PRIVATE);


        if (preferences.getBoolean("LockerOn", false)) {
            if (intent.getAction().equals(Intent.ACTION_PACKAGE_REPLACED)) {
                Intent i = new Intent(context, LockScreenService.class);
                context.startService(i);
            }
        }

        if (preferences.getBoolean("notificationPlay", false)) {
            if (intent.getAction().equals(Intent.ACTION_PACKAGE_REPLACED)) {
                Intent i = new Intent(context, NotiPlayer.class);
                context.startService(i);
                Log.e("dddd", "update222222");
            }
        }
    }
}
