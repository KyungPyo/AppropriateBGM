package com.kp.appropriatebgm.LockScreen;

/**
 * Created by GD on 2016-02-11.
 */

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.v7.app.NotificationCompat;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.kp.appropriatebgm.CheckPref;
import com.kp.appropriatebgm.R;
import com.kp.appropriatebgm.Setting.SettingActivity;

/**
 * Created by GD on 2016-02-11.
 */
public class LockScreenService extends Service {

    private LockScreenReceiver lockReceive;
    private Notification notification;
    private PendingIntent notificationIntent;
    private static int notifyStartId;

    LockNotificationInterface.Stub binder = new LockNotificationInterface.Stub()
    {

        @Override
        public void setNotificationOnOff() throws RemoteException {

            SharedPreferences preferences = getApplicationContext().getSharedPreferences("AppSetting",
                    Context.MODE_PRIVATE);

            Intent noIntent = new Intent(getApplicationContext(), SettingActivity.class);
            noIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            notificationIntent = PendingIntent.getActivity(getApplicationContext(), 0, noIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            notification = new NotificationCompat.Builder(getApplicationContext())
                    .setContentTitle("적절한브금")
                    .setContentText("빠른 재생이 실행중입니다.")
                    .setSmallIcon(R.drawable.ic_queue_music_black_24dp)
                    .setAutoCancel(true)
                    .setContentIntent(notificationIntent)
                    .build();


            //이게 음악 어플처럼 Task Killer 작동해도 살아있게 해주는 거, Foreground 에서 돌리겠다는 뜻
            startForeground(1, notification);

            if (!preferences.getBoolean("alarmOnOff", false) | !preferences.getBoolean("LockerOn", false)) {
                stopForeground(true);
                NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

                notification = new Notification(0, "", System.currentTimeMillis());

                nm.notify(notifyStartId, notification);
                nm.cancel(notifyStartId);
            }
        }

    };
    public IBinder onBind(Intent intent) {
        Log.e("onbind","onbind gogo");
        return binder;
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
        filter.addAction(Intent.ACTION_PACKAGE_REPLACED);
        filter.addAction(TelephonyManager.ACTION_PHONE_STATE_CHANGED);
        registerReceiver(lockReceive, filter);

    }

    // Method : 서비스 시작 초기화
    // Return value : void
    // parameter : void
    // use ; 서비스가 시작되는 경우 인텐트 필터를 통해 화면이 꺼졌을 때의 경우를 브로드캐스트 리시버로 등록한다.
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        notifyStartId = startId;
        Log.d("잘왔냐~", "onStartCommand");
        if (intent != null) {
            if (intent.getAction() == null) {
                if (lockReceive == null) {
                    Log.d("잘왔냐~", "onStartCommand, lockReceiver is null");
                    lockReceive = new LockScreenReceiver();
                    IntentFilter filter = new IntentFilter();
                    filter.addAction(Intent.ACTION_BOOT_COMPLETED);
                    filter.addAction(Intent.ACTION_SCREEN_OFF);
                    filter.addAction(Intent.ACTION_PACKAGE_REPLACED);
                    filter.addAction(TelephonyManager.ACTION_PHONE_STATE_CHANGED);
                    registerReceiver(lockReceive, filter);
                }
            }
        }
        // Use : 알림 기능의 클릭 이벤트에는 PendingIntent 사용하여 SettingActivity로 인텐트 넘김
        //       NO_HISTORY 플래그를 설정하고 Manifest에 기능 추가하여 알림 눌렀을 때 액티비티 새로 뜨는 것 방지


        /* 알림 내용 구현 부분 */
        // Use : 알림 제목과 내용, 아이콘, 인텐트등을 정해주고 빌드

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

    public void setNotificationOnOff(int startId, SharedPreferences preferences) {
        notification = new NotificationCompat.Builder(getApplicationContext())
                .setContentTitle("적절한브금")
                .setContentText("빠른 재생이 실행중입니다.")
                .setSmallIcon(R.drawable.ic_queue_music_black_24dp)
                .setAutoCancel(true)
                .setContentIntent(notificationIntent)
                .build();


        //이게 음악 어플처럼 Task Killer 작동해도 살아있게 해주는 거, Foreground 에서 돌리겠다는 뜻
        startForeground(1, notification);

        if (!preferences.getBoolean("alarmOnOff", false)) {
            stopForeground(true);
            NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

            notification = new Notification(0, "", System.currentTimeMillis());

            nm.notify(startId, notification);
            nm.cancel(startId);
        }
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.e("onunbind","unbind gogo");
        return super.onUnbind(intent);
    }
}
