package com.kp.appropriatebgm.LockScreen;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by GD on 2016-02-11.
 */
public class LockScreenReceiver extends BroadcastReceiver{

    // Method : 잠금화면 브로드캐스트 리시브
    // Return value : void
    // parameter : context(액티비티), intent(인텐트)
    // use ; 브로드캐스트를 받으면 LockScreenActivity로 인텐트를 넘겨 잠금화면을 띄운다.
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("BroadcastRec", "Receive on");
        Intent in = new Intent(context, LockScreenActivity.class);
        in.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(in);
    }
}
