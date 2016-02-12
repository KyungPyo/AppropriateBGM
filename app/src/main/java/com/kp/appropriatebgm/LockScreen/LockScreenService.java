package com.kp.appropriatebgm.LockScreen;

/**
 * Created by GD on 2016-02-11.
 */

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

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

        Log.d("잘왔냐~ ", "LockScreenService onCreate Ok");
        lockReceive = new LockScreenReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_BOOT_COMPLETED);
        registerReceiver(lockReceive, filter);

    }

    // Method : 서비스 시작 초기화
    // Return value : void
    // parameter : void
    // use ; 서비스가 시작되는 경우 인텐트 필터를 통해 화면이 꺼졌을 때의 경우를 브로드캐스트 리시버로 등록한다.
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        Log.d("잘왔냐~", "onStartCommand");
        if (intent != null) {
            if (intent.getAction() == null) {
                if (lockReceive == null) {
                    Log.d("잘왔냐~", "onStartCommand, lockReceiver is null");
                    lockReceive = new LockScreenReceiver();
                    IntentFilter filter = new IntentFilter();
                    filter.addAction(Intent.ACTION_BOOT_COMPLETED);
                    filter.addAction(Intent.ACTION_SCREEN_OFF);
                    registerReceiver(lockReceive, filter);
                }
            }
        }
        startForeground(1, new Notification());

        //Notification 안보이게하는거 성공 , startID 때문에 onCreate 에서 못함.
        NotificationManager nm = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        Notification notification;

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB){

            notification = new Notification.Builder(getApplicationContext())
                    .setContentTitle("")
                    .setContentText("")
                    .build();

        }else{
            notification = new Notification(0, "", System.currentTimeMillis());
            //notification.setLatestEventInfo(getApplicationContext(), "", "", null);
        }

        nm.notify(startId, notification);
        nm.cancel(startId);
        //이게 서비스가 죽어도 다시 살아나게 해주는데 계속 종료될 경우는 안살린다고 함 왜쓰는건지!!!!슈ㅣ벌탱!!!!!
        return START_REDELIVER_INTENT;
    }

    // Method : 서비스 종료
    // Return value : void
    // parameter : void
    // use ; 서비스가 종료되는 경우 등록했던 브로드캐스트 리시버를 해제한다.
    @Override
    public void onDestroy() {

        if (lockReceive != null) {
            Log.d("잘왔냐~", "Receiver Free");
            unregisterReceiver(lockReceive);
        }
        super.onDestroy();

     }
}
