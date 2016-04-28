package com.kp.appropriatebgm;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.kp.appropriatebgm.DBController.DBManager;
import com.kp.appropriatebgm.DBController.Favorite;
import com.kp.appropriatebgm.Setting.SettingActivity;

import java.util.ArrayList;

/**
 * Created by Darackbang2 on 2016-04-08.
 */
public class NotiPlayer extends Service{

    private DBManager dbManager;
    private ArrayList<Favorite> bgmfavoriteArrayList;
    private ArrayList<Favorite> realBgmNameList;
    private NotificationManager nm;
    private Notification notification;
    private RemoteViews contentView;
    private String bgmPath;
    private CheckPref checkPref;
    private BroadcastReceiver backReceiver;
    private BroadcastReceiver playReceiver;
    private BroadcastReceiver nextReceiver;
    private BroadcastReceiver exitReceiver;

    private static int PLAYERNOTIFYSTARTID = 1;   //notification 고유 ID 번호
    private int indexNum = 0;           // 실제 즐겨찾기 목록 index 설정
    private Boolean playCheck = false;  //play 유무 (false 시에 재생버튼 / true시에 일시정지 버튼 보이기 위해서)


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public NotiPlayer() {
        super();
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Log.e("onstartcommand", "gogo");
        checkPref = new CheckPref(getApplicationContext());

        dbManager = DBManager.getInstance(getApplicationContext());

    }

    @Override
    public void onDestroy() {
        Log.e("destroy","destroy");
        nm.cancel(PLAYERNOTIFYSTARTID);
        unregisterReceiver(backReceiver);
        unregisterReceiver(playReceiver);
        unregisterReceiver(nextReceiver);
        unregisterReceiver(exitReceiver);
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.e("onstartcommand","gogo");
        nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Notification.Builder builder = new Notification.Builder(getApplicationContext());

        notification = builder.build();
        notification.when = System.currentTimeMillis();
        notification.tickerText = "빠른 재생을 실행합니다";
        notification.icon = R.mipmap.ic_launcher;

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
            bgmPath = realBgmNameList.get(0).getBgmPath();
//                Log.e("getconvertid", contentView.getLayoutId() + "");
        }

        notification.contentView = contentView;
        notification.flags = Notification.FLAG_ONGOING_EVENT;

        if(checkPref.getNotiplayerOnOff()) {
            setListeners(contentView);
            indexNum = 0;
            nm.notify(PLAYERNOTIFYSTARTID, notification);
            Log.e("startforeground","start");
        }
        return START_STICKY_COMPATIBILITY;
    }

    public void setListeners(RemoteViews view)
    {
        final RemoteViews mView = view;
        // BackButton
        this.registerReceiver(backReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if(indexNum == 0) {
                    indexNum = realBgmNameList.size() - 1;
                    Log.e("indexloof<<", "-");
                }
                else
                {
                    indexNum--;
                }
                Log.e("indexnum back", indexNum + "");

                //

                mView.setTextViewText(R.id.notification_bgmtitle, realBgmNameList.get(indexNum).getBgmName());
                mView.setImageViewResource(R.id.notification_bgmplaybtn, R.drawable.ic_play_circle_outline_white_24dp);
                playCheck = false;
                nm.notify(PLAYERNOTIFYSTARTID,notification);  // 노티피케이션의 텍스트를 업데이트 하고 싶을 경우에는 setTextviewText를 한다고 해도 노티피케이션이 바로 업데이트 되는 것이 아니라
                // 다시 띄워주는 형식을 반복해야 한다. 즉, 노티피케이션 자체를 다시 띄워 업데이트 하는 형식이다.
            }
        }, new IntentFilter("RemoteBack"));
        PendingIntent pBack = PendingIntent.getBroadcast(this, 0, new Intent("RemoteBack"), 0);
        view.setOnClickPendingIntent(R.id.notification_bgmbackbtn, pBack);

        // PlayButton
        this.registerReceiver(playReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
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

                nm.notify(PLAYERNOTIFYSTARTID, notification);  // 노티피케이션의 텍스트를 업데이트 하고 싶을 경우에는 setTextviewText를 한다고 해도 노티피케이션이 바로 업데이트 되는 것이 아니라
                // 다시 띄워주는 형식을 반복해야 한다. 즉, 노티피케이션 자체를 다시 띄워 업데이트 하는 형식이다.
            }
        }, new IntentFilter("RemotePlay"));
        PendingIntent pPlay = PendingIntent.getBroadcast(this, 0, new Intent("RemotePlay"), 0);
        view.setOnClickPendingIntent(R.id.notification_bgmplaybtn, pPlay);

        // NextButton
        this.registerReceiver(nextReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
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
                nm.notify(PLAYERNOTIFYSTARTID, notification);
            }
        }, new IntentFilter("RemoteNext"));
        PendingIntent pNext = PendingIntent.getBroadcast(this, 0, new Intent("RemoteNext"), 0);
        view.setOnClickPendingIntent(R.id.notification_bgmnextbtn, pNext);

        // ExitButton
        this.registerReceiver(exitReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                nm.cancel(PLAYERNOTIFYSTARTID);
                checkPref.setNotiplayerOnOff(false);
                //((SettingActivity)(SettingActivity.mContext)).onResume();

                if(notification!=null) {
                    intent.setClass(getApplicationContext(), NotiPlayer.class);
                    stopService(intent);
                }
            }
        }, new IntentFilter("RemoteExit"));
        PendingIntent pExit = PendingIntent.getBroadcast(this, 0, new Intent("RemoteExit"), 0);
        view.setOnClickPendingIntent(R.id.notification_cancel, pExit);

    }

}
