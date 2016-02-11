package com.kp.appropriatebgm.LockScreen;

/**
 * Created by GD on 2016-02-11.
 */

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Created by GD on 2016-02-11.
 */
public class LockScreenService extends Service {

    private LockScreenReceiver lockReceive;



    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    // Method : 서비스 시작 초기화
    // Return value : void
    // parameter : void
    // use ; 서비스가 시작되는 경우 인텐트 필터를 통해 화면이 꺼졌을 때의 경우를 브로드캐스트 리시버로 등록한다.
    @Override
    public void onCreate() {
        super.onCreate();
        lockReceive = new LockScreenReceiver();
        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
        registerReceiver(lockReceive, filter);
    }

    // Method : 서비스 종료
    // Return value : void
    // parameter : void
    // use ; 서비스가 종료되는 경우 등록했던 브로드캐스트 리시버를 해제한다.
    @Override
    public void onDestroy() {

        if(lockReceive != null)
        {
            unregisterReceiver(lockReceive);
        }
        super.onDestroy();
    }
}
