package com.kp.appropriatebgm.LockScreen;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

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

        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            Log.d("잘왔냐~","BootReceiver Ok");
            Intent i = new Intent(context, LockScreenService.class);
            context.startService(i);
        }else if(intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
            Log.d("잘왔냐~","Screen Off Ok");
            Intent in = new Intent(context, LockScreenActivity.class);
            in.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            //이제 Activity 하나만 쓰게 해주는 플래그 Single Top
            in.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

            context.startActivity(in);
            //StartActivity 까지도 잘돼요 ㅠㅠ 근데 onCreate 를 못하고 서비스가 죽어요, 이유를 모르겠어요ㅠㅠ
            //도와주세여ㅠㅠㅠㅠㅠ
        }

    }
}
