package com.kp.appropriatebgm.LockScreen;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.kp.appropriatebgm.CheckPref;
import com.kp.appropriatebgm.DBController.Favorite;
import com.kp.appropriatebgm.favoritebgm.FavoriteActivity;

import java.io.Serializable;

/**
 * Created by GD on 2016-02-11.
 */
public class LockScreenReceiver extends BroadcastReceiver {


    // Method : 잠금화면 브로드캐스트 리시브
    // Return value : void
    // parameter : context(액티비티), intent(인텐트)
    // use ; 브로드캐스트를 받으면 LockScreenActivity로 인텐트를 넘겨 잠금화면을 띄운다.
    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences preferences = context.getSharedPreferences("AppSetting",
                Context.MODE_PRIVATE);

        if (preferences.getBoolean("LockerOn", false)) { //이게 부팅시에는 안먹. New 하는 부분이 Favorite 에 있어서 그런가?
            if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED) ||
                    intent.getAction().equals(Intent.ACTION_PACKAGE_REPLACED)) {
                Intent i = new Intent(context, LockScreenService.class);
                context.startService(i);
            } else if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
                Intent in = new Intent(context, LockScreenActivity.class);
                in.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                in.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

                context.startActivity(in);
            }
        }
    }
}
