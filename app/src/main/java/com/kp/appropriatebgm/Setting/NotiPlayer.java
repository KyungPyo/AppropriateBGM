package com.kp.appropriatebgm.Setting;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.RemoteViews;

import com.kp.appropriatebgm.CheckPref;
import com.kp.appropriatebgm.DBController.DBManager;
import com.kp.appropriatebgm.DBController.Favorite;
import com.kp.appropriatebgm.LockScreen.LockScreenReceiver;
import com.kp.appropriatebgm.Music.MusicPlayer;
import com.kp.appropriatebgm.R;

import java.util.ArrayList;

/**
 * Created by Darackbang2 on 2016-04-08.
 */
public class NotiPlayer extends Service{

    private DBManager dbManager;
    private ArrayList<Favorite> bgmfavoriteArrayList;
    private NotificationManager nm;
    private Notification notification;
    private RemoteViews contentView;
    private CheckPref checkPref;
    private BroadcastReceiver backReceiver;
    private BroadcastReceiver playReceiver;
    private BroadcastReceiver nextReceiver;
    private BroadcastReceiver exitReceiver;
    private MusicPlayer musicPlayer;

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

    // Method : Notification 생성 생명주기함수
    // Return Value : void
    // Parameter : void
    // Use : SharedPreference에 저장된 빠른재생기능 on/off 여부 받아오고, SQLite 데이터베이스에 접속
    @Override
    public void onCreate() {
        super.onCreate();

        checkPref = new CheckPref(getApplicationContext());

        dbManager = DBManager.getInstance(getApplicationContext());

        Log.e("dddd","oncreate service");

    }

    // Method : Notification 제거 생명주기함수
    // Return Value : void
    // Parameter : void
    // Use : 등록한 Notification과 Receiver를 해제한다
    @Override
    public void onDestroy() {
        Log.e("onDestory", "gogo");
        unregisterReceiver(backReceiver);
        unregisterReceiver(playReceiver);
        unregisterReceiver(nextReceiver);
        unregisterReceiver(exitReceiver);
        //nm.cancel(PLAYERNOTIFYSTARTID);
        super.onDestroy();
    }

    // Method : Notification 실행시 발생 이벤트
    // Return Value : void
    // Parameter : intent(해당 인텐트), flags(재실행 모드 구분 (재전송된 intent인지 아닌지)), startId(스타트 아이디 - 다른 메소드에서 서비스 종료 시 사용)
    // Use : startService를 통해 서비스가 시작되면 이 메소드가 호출되서 노티피케이션을 띄웁니다.
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Notification.Builder builder = new Notification.Builder(getApplicationContext());
        Log.e("onStartCommand", "gogo");

        notification = builder.build();
        notification.when = System.currentTimeMillis();
        notification.tickerText = "빠른 재생을 실행합니다";
        notification.icon = R.mipmap.ic_launcher;

        contentView = new RemoteViews(getApplicationContext().getPackageName(), R.layout.notification_fastplay);

        bgmfavoriteArrayList=dbManager.getFavoriteListNotNull();   //DB

        if(bgmfavoriteArrayList.size() <= 0)
        {
            contentView.setTextViewText(R.id.notification_bgmtitle,"즐겨찾기 목록이 존재하지 않습니다.");
        }
        else
        {
            contentView.setTextViewText(R.id.notification_bgmtitle, bgmfavoriteArrayList.get(0).getBgmName());
        }

        notification.contentView = contentView;
        notification.flags = Notification.FLAG_ONGOING_EVENT;

        if(checkPref.getNotiplayerOnOff()) {
            setListeners(contentView);
            indexNum = 0;
            nm.notify(PLAYERNOTIFYSTARTID, notification);
        }
        return START_STICKY_COMPATIBILITY;
    }

    // Method : 버튼 이벤트 처리 리스너 등록
    // Return Value : void
    // Parameter : view(Notification에 출력되는 뷰)
    // Use : 빠른재생 기능에 사용되는 버튼들에 대한 이벤트를 Receiver로 등록해 놓는다.
    public void setListeners(RemoteViews view)
    {
        final RemoteViews rView = view;
        // BackButton
        this.registerReceiver(backReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                bgmfavoriteArrayList=dbManager.getFavoriteListNotNull();    // 즐겨찾기 목록갱신

                if(indexNum == 0) {
                    indexNum = bgmfavoriteArrayList.size() - 1;
                }
                else
                {
                    indexNum--;
                }
                Log.d("indexnum back", indexNum + "");

                if (musicPlayer != null && musicPlayer.isPlaying()) {
                    musicPlayer.stopBgm();
                    rView.setImageViewResource(R.id.notification_bgmplaybtn, R.drawable.ic_play_circle_outline_white_24dp);
                }
                if (bgmfavoriteArrayList.size() > 0) {
                    rView.setTextViewText(R.id.notification_bgmtitle, bgmfavoriteArrayList.get(indexNum).getBgmName());
                } else {
                    rView.setTextViewText(R.id.notification_bgmtitle,"즐겨찾기 목록이 존재하지 않습니다.");
                }
                // 노티피케이션 내 요소(텍스트, 이미지)를 메소드(setTextViewText,setImageViewResource)를 통해 수정하고 notify를 통해 노티피케이션을 업데이트 시켜준다.
                nm.notify(PLAYERNOTIFYSTARTID,notification);

            }
        }, new IntentFilter("RemoteBack"));
        PendingIntent pBack = PendingIntent.getBroadcast(this, 0, new Intent("RemoteBack"), 0);
        view.setOnClickPendingIntent(R.id.notification_bgmbackbtn, pBack);

        // PlayButton
        this.registerReceiver(playReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                if(musicPlayer != null && musicPlayer.isPlaying())
                {
                    musicPlayer.stopBgm();
                    rView.setImageViewResource(R.id.notification_bgmplaybtn, R.drawable.ic_play_circle_outline_white_24dp);
                }
                else
                {
                    playMusic(bgmfavoriteArrayList.get(indexNum), rView);
                    PlaybtnTask playbtnTask = new PlaybtnTask(rView, musicPlayer);
                    playbtnTask.execute();
                }
                // 노티피케이션 내 요소(텍스트, 이미지)를 메소드(setTextViewText,setImageViewResource)를 통해 수정하고 notify를 통해 노티피케이션을 업데이트 시켜준다.
                nm.notify(PLAYERNOTIFYSTARTID, notification);
            }
        }, new IntentFilter("RemotePlay"));
        PendingIntent pPlay = PendingIntent.getBroadcast(this, 0, new Intent("RemotePlay"), 0);
        view.setOnClickPendingIntent(R.id.notification_bgmplaybtn, pPlay);

        // NextButton
        this.registerReceiver(nextReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                bgmfavoriteArrayList=dbManager.getFavoriteListNotNull();    // 즐겨찾기 목록갱신

                if(indexNum == bgmfavoriteArrayList.size() - 1) {
                    indexNum = 0;
                }
                else
                {
                    indexNum++;
                }
                Log.d("indexnum next", indexNum + "");

                if (musicPlayer != null && musicPlayer.isPlaying()) {
                    musicPlayer.stopBgm();
                    rView.setImageViewResource(R.id.notification_bgmplaybtn, R.drawable.ic_play_circle_outline_white_24dp);
                }
                if (bgmfavoriteArrayList.size() > 0) {
                    rView.setTextViewText(R.id.notification_bgmtitle, bgmfavoriteArrayList.get(indexNum).getBgmName());
                } else {
                    rView.setTextViewText(R.id.notification_bgmtitle, "즐겨찾기 목록이 존재하지 않습니다.");
                }
                playCheck = false;
                // 노티피케이션 내 요소(텍스트, 이미지)를 메소드(setTextViewText,setImageViewResource)를 통해 수정하고 notify를 통해 노티피케이션을 업데이트 시켜준다.
                nm.notify(PLAYERNOTIFYSTARTID, notification);
            }
        }, new IntentFilter("RemoteNext"));
        PendingIntent pNext = PendingIntent.getBroadcast(this, 0, new Intent("RemoteNext"), 0);
        view.setOnClickPendingIntent(R.id.notification_bgmnextbtn, pNext);

        // ExitButton
        this.registerReceiver(exitReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // 재생중이면 정지
                if (musicPlayer != null && musicPlayer.isPlaying()) {
                    musicPlayer.stopBgm();
                }

                nm.cancel(PLAYERNOTIFYSTARTID);
                checkPref.setNotiplayerOnOff(false);
                //((SettingActivity)(SettingActivity.mContext)).onResume();

//                if(notification!=null) {
//                    intent.setClass(getApplicationContext(), NotiPlayer.class);
//                    stopService(intent);
//                }
            }
        }, new IntentFilter("RemoteExit"));
        PendingIntent pExit = PendingIntent.getBroadcast(this, 0, new Intent("RemoteExit"), 0);
        view.setOnClickPendingIntent(R.id.notification_cancel, pExit);

    }

    // Method : 빠른재생에서 재생버튼 클릭 시
    // Return Value : void
    // Parameter : item(현재 선택되어있는 즐겨찾기 등록된 파일)
    // Use : 현재 선택된 파일을 재생준비한 후 재생한다. 혹시 경로에 파일이 없는 경우를 대비하여 정말 준비된 경우에만 재생한다.
    private void playMusic(Favorite item, RemoteViews remoteViews){
        if (item != null) {
            if (musicPlayer != null) {
                musicPlayer.stopBgm();
                musicPlayer.releaseBgm();
            }
            if (item.isInnerfile()) {
                musicPlayer = new MusicPlayer(getApplicationContext(), item.getInnerfileCode(), false);
            } else {
                musicPlayer = new MusicPlayer(getApplicationContext(), item.getBgmPath(), false);
            }

            if (musicPlayer.isPrepared()) { // 재생할 파일이 잘 등록되었으면
                musicPlayer.playBgm();
                remoteViews.setImageViewResource(R.id.notification_bgmplaybtn, R.drawable.ic_stop_white_24dp);
            }
        }
    }

    // Class : 재생버튼 이미지 변경을 위한 쓰레드
    // Use : 재생과 거의 동시에 실행되어 재생중에 정지버튼으로 변경되었던 이미지를 재생이 끝나면 재생버튼으로 바꿔준다.
    private class PlaybtnTask extends AsyncTask<Void,Void,Void> {
        RemoteViews remoteViews;
        MusicPlayer taskMusicPlayer;

        // Method : PlaybtnTask 생성자
        // Parameter : rView(상황에 따라 변경될 뷰), musicPlayer(현재 재생중인 MusicPlayer 인스턴스)
        public PlaybtnTask(RemoteViews rView, MusicPlayer musicPlayer){
            this.remoteViews = rView;
            this.taskMusicPlayer = musicPlayer;
        }

        // Method : 스레드 실행
        // Use : execute() 가 실행되면 수행한다. 재생이 시작되면(스레드가 시작되면) 버튼 이미지를 정지버튼으로 변경,
        //      그 후 재생이 끝날 때 까지 기다렸다가 버튼 이미지를 변경한다.
        @Override
        protected Void doInBackground(Void... params) {
            if (taskMusicPlayer != null){
                while (taskMusicPlayer.isPlaying()){
                    try {
                        // 재생할 동안 대기
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                publishProgress();  // 화면 변경 실행
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            remoteViews.setImageViewResource(R.id.notification_bgmplaybtn, R.drawable.ic_play_circle_outline_white_24dp);
            nm.notify(PLAYERNOTIFYSTARTID, notification);
            super.onProgressUpdate(values);
        }
    }
}
