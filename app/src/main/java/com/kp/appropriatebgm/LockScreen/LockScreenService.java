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
import android.os.Bundle;
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
    private static int notifyStartId = 1;
    private DBManager dbManager;
    private int indexNum = 0;           // 실제 즐겨찾기 목록 index 설정
    private Boolean playCheck = false;  //play 유무 (false 시에 재생버튼 / true시에 일시정지 버튼 보이기 위해서)
    private ArrayList<Favorite> realBgmNameList;
    private RemoteViews contentView;
    private BroadcastReceiver backReceiver;
    private BroadcastReceiver playReceiver;
    private BroadcastReceiver nextReceiver;
    ArrayList<Favorite> bgmfavoriteArrayList;


    LockNotificationInterface.Stub binder = new LockNotificationInterface.Stub() {

        @Override
        public void setNotificationOnOff() throws RemoteException {

            CheckPref checkPref = new CheckPref(getApplicationContext());

            dbManager = DBManager.getInstance(getApplicationContext());

            // 원래 노티피케이션 누르면 settingActivity로 넘어가는 부분인데, 지금 커스텀 노티피케이션이라 작동 안됨
            //Intent noIntent = new Intent(getApplicationContext(), SettingActivity.class);
            //noIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            //notificationIntent = PendingIntent.getActivity(getApplicationContext(), 0, noIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

            Notification.Builder builder = new Notification.Builder(getApplicationContext());

            notification = builder.build();
            notification.when = System.currentTimeMillis();
            notification.tickerText = "빠른 재생을 실행합니다";
            notification.icon = R.mipmap.ic_launcher;

//
            contentView = new RemoteViews(getApplicationContext().getPackageName(), R.layout.notification_lockscreenplay);

            bgmfavoriteArrayList=dbManager.getFavoriteList();   //DB
            realBgmNameList = new ArrayList<>();
            for(int i=0; i < bgmfavoriteArrayList.size() ; i++) {
                if (bgmfavoriteArrayList.get(i).getBgmPath() != null) {
                    realBgmNameList.add(bgmfavoriteArrayList.get(i));
                }
            }
            if(realBgmNameList.size() == 0)
            {
                contentView.setTextViewText(R.id.notification_bgmtitle,"즐겨찾기 목록이 존재하지 않습니다.");
            }
            else
            {
                contentView.setTextViewText(R.id.notification_bgmtitle,realBgmNameList.get(0).getBgmName());
                Log.e("getconvertid", contentView.getLayoutId() + "");
            }
//
            notification.contentView = contentView;
            notification.flags = Notification.FLAG_ONGOING_EVENT;
            if(checkPref.getAlarmOnOff() && checkPref.getLockerOnOff()){
                setListeners(contentView);
                indexNum = 0;
                nm.notify(1,notification);
                startForeground(1, notification);
                Log.e("startforeground","start");
            } else {
                if(notification != null) {
                    stopForeground(true);
                    nm.cancel(notifyStartId);
                    //리시버 해제 안하면 브로드캐스트가 쌓이기 시작해서 목록 넘어가는게 제대로 작동이 안됩니다.
                    //근데 이 경우가 버튼 아무거나 하나 off됬을 때 리시버 해제하는 건데 버튼을 2개 연속으로 해제 했을 시에
                    //이미 첫번째에서 리시버 해제했는데 두번째 것도 off를 해서 이 코드가 돌아가면 에러가 나서 receiver 없을시에 해제하는 경우를
                    //illegalargumentException으로 일단 처리해서 동작하지 않도록 했습니다.
                    try {
                        unregisterReceiver(backReceiver);
                        unregisterReceiver(playReceiver);
                        unregisterReceiver(nextReceiver);
                    }catch(IllegalArgumentException e){}
                    catch(Exception e)
                    {
                        Log.e("Log name",e.getMessage());
                    }
                    Log.e("stopforeground", "stop");
                    //nm.cancel(1); //cancel 코드를 실행시키면 버튼이 제대로 작동하지가 않는다...
                }
            }

            //이게 음악 어플처럼 Task Killer 작동해도 살아있게 해주는 거, Foreground 에서 돌리겠다는 뜻

        }

    };

    public void setListeners(RemoteViews view)
    {
        final RemoteViews mView = view;
        // BackButton
        this.registerReceiver(backReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Toast.makeText(getApplicationContext(), "Back Button", Toast.LENGTH_SHORT).show();
                if(indexNum == 0) {
                    indexNum = realBgmNameList.size() - 1;
                    Log.e("indexloof<<", "-");
                }
                else
                {
                    indexNum--;
                }
                Log.e("indexnum back", indexNum + "");
                mView.setTextViewText(R.id.notification_bgmtitle, realBgmNameList.get(indexNum).getBgmName());
                mView.setImageViewResource(R.id.notification_bgmplaybtn, R.drawable.ic_play_circle_outline_white_24dp);
                playCheck = false;          //play유무 검사 변수인데 나중에 preference 공유변수로 받아와야 될 것 같다.
                nm.notify(notifyStartId,notification);  // 노티피케이션의 텍스트를 업데이트 하고 싶을 경우에는 setTextviewText를 한다고 해도 노티피케이션이 바로 업데이트 되는 것이 아니라
                // 다시 띄워주는 형식을 반복해야 한다. 즉, 노티피케이션 자체를 다시 띄워 업데이트 하는 형식이다.
            }
        }, new IntentFilter("RemoteBack"));
        PendingIntent pBack = PendingIntent.getBroadcast(this, 0, new Intent("RemoteBack"), 0);
        view.setOnClickPendingIntent(R.id.notification_bgmbackbtn, pBack);

        // PlayButton
        this.registerReceiver(playReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Toast.makeText(getApplicationContext(), "Play Button", Toast.LENGTH_SHORT).show();
                if(playCheck == false)
                {
                    mView.setImageViewResource(R.id.notification_bgmplaybtn, R.drawable.ic_pause_circle_outline_white_24dp);
                    playCheck = true;
                }
                else
                {
                    mView.setImageViewResource(R.id.notification_bgmplaybtn, R.drawable.ic_play_circle_outline_white_24dp);
                    playCheck = false;
                }
                nm.notify(notifyStartId,notification);  // 노티피케이션의 텍스트를 업데이트 하고 싶을 경우에는 setTextviewText를 한다고 해도 노티피케이션이 바로 업데이트 되는 것이 아니라
                                                // 다시 띄워주는 형식을 반복해야 한다. 즉, 노티피케이션 자체를 다시 띄워 업데이트 하는 형식이다.
            }
        }, new IntentFilter("RemotePlay"));
        PendingIntent pPlay = PendingIntent.getBroadcast(this, 0, new Intent("RemotePlay"), 0);
        view.setOnClickPendingIntent(R.id.notification_bgmplaybtn, pPlay);

        // NextButton
        this.registerReceiver(nextReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Toast.makeText(getApplicationContext(), "Next Button", Toast.LENGTH_SHORT).show();
                if(indexNum == realBgmNameList.size() - 1) {
                    indexNum = 0;
                    Log.e("indexloof>>", "- ");
                }
                else
                {
                    indexNum++;
                }
                Log.e("indexnum next", indexNum + "");
                mView.setTextViewText(R.id.notification_bgmtitle, realBgmNameList.get(indexNum).getBgmName());
                mView.setImageViewResource(R.id.notification_bgmplaybtn, R.drawable.ic_play_circle_outline_white_24dp);
                playCheck = false;
                nm.notify(notifyStartId,notification);
            }
        }, new IntentFilter("RemoteNext"));
        PendingIntent pNext = PendingIntent.getBroadcast(this, 0, new Intent("RemoteNext"), 0);
        view.setOnClickPendingIntent(R.id.notification_bgmnextbtn, pNext);

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
        try {
            binder.setNotificationOnOff();
        } catch (Exception e) {
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
        if (this != null) {
            unregisterComponentCallbacks(this);
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
