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
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.v7.app.NotificationCompat;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import com.kp.appropriatebgm.CheckPref;
import com.kp.appropriatebgm.DBController.BGMInfo;
import com.kp.appropriatebgm.DBController.DBManager;
import com.kp.appropriatebgm.DBController.Favorite;
import com.kp.appropriatebgm.MusicPlayer;
import com.kp.appropriatebgm.R;
import com.kp.appropriatebgm.Setting.SettingActivity;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Created by GD on 2016-02-11.
 */
public class LockScreenService extends Service {

    private LockScreenReceiver lockReceive;
    private Notification notification;
    private PendingIntent notificationIntent;
    private Context context;
    private NotificationManager nm;
    private static int notifyStartId;
    private DBManager dbManager;
    private int indexNum = 0;
    private TextView notiMusicTitle;
    private ArrayList<Favorite> realBgmNameList;
    ArrayList<Favorite> bgmfavoriteArrayList;


    LockNotificationInterface.Stub binder = new LockNotificationInterface.Stub()
    {

        @Override
        public void setNotificationOnOff() throws RemoteException {

            //SharedPreferences preferences = getApplicationContext().getSharedPreferences("AppSetting", Context.MODE_PRIVATE);//
            CheckPref checkPref = new CheckPref(getApplicationContext());

//            dbManager = DBManager.getInstance(getApplicationContext());

            Intent noIntent = new Intent(getApplicationContext(), SettingActivity.class);
            noIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            notificationIntent = PendingIntent.getActivity(getApplicationContext(), 0, noIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

//            Notification.Builder builder = new Notification.Builder(getApplicationContext());

//            notification = builder.getNotification();
//            notification.when = System.currentTimeMillis();
//            notification.tickerText = "빠른 재생 실행 중";
//            notification.icon = R.mipmap.ic_launcher;
//
//            RemoteViews contentView = new RemoteViews(getApplicationContext().getPackageName(), R.layout.notification_lockscreenplay);
//            contentView.setTextViewText(R.id.notification_bgmtitle,"멍청이");

//            bgmfavoriteArrayList=dbManager.getFavoriteList();   //DB
//            realBgmNameList = new ArrayList<>();
//            for(int i=0; i < bgmfavoriteArrayList.size() ; i++) {
//                if (bgmfavoriteArrayList.get(i).getBgmPath() != null) {
//                    realBgmNameList.add(bgmfavoriteArrayList.get(i));
//                }
//            }
//            if(realBgmNameList.size() == 0)
//            {
//                contentView.setTextViewText(R.id.notification_bgmtitle,"즐겨찾기 목록이 존재하지 않습니다.");
//            }
//            else
//            {
//                contentView.setTextViewText(R.id.notification_bgmtitle,realBgmNameList.get(0).getBgmName());
//                Log.e("getconvertid", contentView.getLayoutId() + "");
//            }

            //LayoutInflater layoutInflater = (LayoutInflater)getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            //LinearLayout notiPlayer = (LinearLayout)layoutInflater.inflate(R.layout.notification_lockscreenplay, null);
//            LinearLayout innernotiPlayer = (LinearLayout) notiPlayer.getChildAt(1);
//            notiMusicTitle = (TextView) innernotiPlayer.getChildAt(0);
//            bgmfavoriteArrayList = dbManager.getFavoriteList();
//            realBgmNameList = new ArrayList();
//            for(int i=0; i < bgmfavoriteArrayList.size() ; i++) {
//                if (bgmfavoriteArrayList.get(i).getBgmPath() != null) {
//                    realBgmNameList.add(bgmfavoriteArrayList.get(i));
//                }
//            }
//            if(realBgmNameList.size() == 0)
//            {
//                notiMusicTitle.setText("즐겨찾기 목록이 존재하지 않습니다!");
//            }
//            else
//            {
//                notiMusicTitle.setText(/*realBgmNameList.get(0).getBgmName()*/"ddddddd");
//            }
//                notiPlayer.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        switch (v.getId()) {
//                            case R.id.notification_bgmplaybtn: {
//                                Toast.makeText(getApplicationContext(), realBgmNameList.get(0).getBgmName(), Toast.LENGTH_LONG).show();
//                                notiMusicTitle.setText(realBgmNameList.get(0).getBgmName());
//                                break;
//                            }
//                            case R.id.notification_bgmbackbtn: {
//                                if (indexNum == 0) {
//                                    indexNum = realBgmNameList.size() - 1;
//                                }
//                                notiMusicTitle.setText(realBgmNameList.get(indexNum).toString());
//                                Toast.makeText(getApplicationContext(), "Back Button", Toast.LENGTH_LONG).show();
//                                break;
//                            }
//                            case R.id.notification_bgmnextbtn: {
//                                if(indexNum >= realBgmNameList.size() - 1)
//                                {
//                                    indexNum = 0;
//                                }
//                                notiMusicTitle.setText(realBgmNameList.get(indexNum).toString());
//                                Toast.makeText(getApplicationContext(), "Next Button", Toast.LENGTH_LONG).show();
//                                break;
//                            }
//                        }
//                    }
//                });
//
//            setListeners(contentView);
//
//            notification.contentView = contentView;
//            notification.flags = Notification.FLAG_ONGOING_EVENT;
            if(checkPref.getAlarmOnOff() && checkPref.getLockerOnOff()){
                notification = new NotificationCompat.Builder(getApplicationContext())
                        .setContentTitle("브금술사")
                        .setContentText("빠른 재생이 실행중입니다.")
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                        .setAutoCancel(true)
                        .setContentIntent(notificationIntent)
                        .build();
                startForeground(1, notification);
            } else {
                if(notification != null) {
                    stopForeground(true);
                }
            }

            //이게 음악 어플처럼 Task Killer 작동해도 살아있게 해주는 거, Foreground 에서 돌리겠다는 뜻

        }

    };

    public void setListeners(RemoteViews view)
    {
//        final RemoteViews mView = view;
//        // BackButton
//        this.registerReceiver(new BroadcastReceiver() {
//            @Override
//            public void onReceive(Context context, Intent intent) {
//                Toast.makeText(getApplicationContext(), "Back Button", Toast.LENGTH_SHORT).show();
//                Log.e("indexnum", indexNum + "");
//                if(indexNum == 0) {
//                    indexNum = realBgmNameList.size() - 1;
//                    mView.setTextViewText(R.id.notification_bgmtitle, realBgmNameList.get(indexNum).getBgmName());
//                    Log.e("indexnum==0",indexNum+"");
//                }
//                else
//                {
//                    indexNum--;
//                    mView.setTextViewText(R.id.notification_bgmtitle, realBgmNameList.get(indexNum).getBgmName());
//                    Log.e("indexnum!=0",indexNum+"");
//                }
//
//                mView.setTextViewText(R.id.notification_bgmtitle, realBgmNameList.get(indexNum).getBgmName());
//                Log.e("indexnum", realBgmNameList.get(indexNum).getBgmName());
//                Log.e("indexnum", (mView==null)+"");
//                Log.e("indexnum", mView.getLayoutId()+"");
//            }
//        }, new IntentFilter("RemoteBack"));
//        PendingIntent pBack = PendingIntent.getBroadcast(this, 0, new Intent("RemoteBack"), 0);
//        view.setOnClickPendingIntent(R.id.notification_bgmbackbtn, pBack);
//
//        // PlayButton
//        this.registerReceiver(new BroadcastReceiver() {
//            @Override
//            public void onReceive(Context context, Intent intent) {
//                Toast.makeText(getApplicationContext(), "Play Button", Toast.LENGTH_SHORT).show();
//            }
//        }, new IntentFilter("RemotePlay"));
//        PendingIntent pPlay = PendingIntent.getBroadcast(this, 0, new Intent("RemotePlay"), 0);
//        view.setOnClickPendingIntent(R.id.notification_bgmplaybtn, pPlay);
//
//        // NextButton
//        this.registerReceiver(new BroadcastReceiver() {
//            @Override
//            public void onReceive(Context context, Intent intent) {
//                Toast.makeText(getApplicationContext(), "Next Button", Toast.LENGTH_SHORT).show();
//                if(indexNum == realBgmNameList.size() - 1) {
//                    indexNum = 0;
//                }
//                else
//                {
//                    indexNum++;
//                }
//                mView.setTextViewText(R.id.notification_bgmtitle, realBgmNameList.get(indexNum).getBgmName());
//            }
//        }, new IntentFilter("RemoteNext"));
//        PendingIntent pNext = PendingIntent.getBroadcast(this, 0, new Intent("RemoteNext"), 0);
//        view.setOnClickPendingIntent(R.id.notification_bgmnextbtn, pNext);

    }

    public IBinder onBind(Intent intent) {
        return binder;
    }

    // Method : 서비스 시작 초기화
    // Return value : void
    // parameter : void
    // use ; 서비스가 시작되는 경우 인텐트 필터를 통해 화면이 꺼졌을 때의 경우를 브로드캐스트 리시버로 등록한다.
    @Override
    public void onCreate() {
        super.onCreate();

        lockReceive = new LockScreenReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_BOOT_COMPLETED);
        filter.addAction(Intent.ACTION_PACKAGE_REPLACED);
        filter.addAction(TelephonyManager.ACTION_PHONE_STATE_CHANGED);
        registerReceiver(lockReceive, filter);
        try{
            binder.setNotificationOnOff();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    // Method : 서비스 시작 초기화
    // Return value : void
    // parameter : void
    // use ; 서비스가 시작되는 경우 인텐트 필터를 통해 화면이 꺼졌을 때의 경우를 브로드캐스트 리시버로 등록한다.
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        notifyStartId = startId;
        if (intent != null) {
            if (intent.getAction() == null) {
                if (lockReceive == null) {
                    lockReceive = new LockScreenReceiver();
                    IntentFilter filter = new IntentFilter();
                    filter.addAction(Intent.ACTION_BOOT_COMPLETED);
                    filter.addAction(Intent.ACTION_SCREEN_OFF);
                    filter.addAction(Intent.ACTION_PACKAGE_REPLACED);
                    filter.addAction(TelephonyManager.ACTION_PHONE_STATE_CHANGED);
                    registerReceiver(lockReceive, filter);

                    // Use : 알림 기능의 클릭 이벤트에는 PendingIntent 사용하여 SettingActivity로 인텐트 넘김
                    //       NO_HISTORY 플래그를 설정하고 Manifest에 기능 추가하여 알림 눌렀을 때 액티비티 새로 뜨는 것 방지
                }
            }
        }


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
            unregisterReceiver(lockReceive);
        }
        super.onDestroy();

     }

    public void setNotificationOnOff(int startId, SharedPreferences preferences) {
        notification = new NotificationCompat.Builder(getApplicationContext())
                .setContentTitle("적절한브금")
                .setContentText("빠른 재생이 실행중입니다.")
                .setSmallIcon(R.mipmap.ic_launcher)
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
        return super.onUnbind(intent);
    }
}
