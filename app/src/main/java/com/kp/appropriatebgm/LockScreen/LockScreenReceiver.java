package com.kp.appropriatebgm.LockScreen;

import android.app.PendingIntent;
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
            Log.d("잘왔냐~", "Screen Off Ok");
            //일단 스크린 오프까지는 되는데 Activity 의 onCreate 실행 안하고 서비스가 죽음
            Intent in = new Intent(context, LockScreenActivity.class);
            PendingIntent pendingIntent=PendingIntent.getActivity(context,0,in,PendingIntent.FLAG_ONE_SHOT);

            try {
                pendingIntent.send();
                Log.d("잘왔냐~", "Send Ok");
                // 심지어 send 도 된다. 근데!!! 근데!!!!!!!!!! 왜 onCreate 안되냐고!!!!!!
                // 제발 이거 해결좀 해주세여ㅠㅠㅠㅠㅠ 여기서 서비스가 종료해요 왜 종료되는지 모르겠습니다. 또륵
            }catch (PendingIntent.CanceledException e){
                e.printStackTrace();
            }
        }

    }
}
