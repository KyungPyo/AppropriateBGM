package com.kp.appropriatebgm;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.PowerManager;
import android.widget.Toast;

import com.kp.appropriatebgm.DBController.DBManager;

import java.io.File;
import java.io.IOException;

/**
 * Created by KP on 2016-02-03.
 */
public class MusicPlayer {
    private MediaPlayer music = null;
    private Uri uri = null;
    private boolean paused = false;
    private boolean released = false;
    private Context targetContext;
    private String filePath;
    private int fileCode;
    private PowerManager.WakeLock wakeLock;

    // Method : MusicPlayer 생성자
    // Return Value : Constructor
    // Parameter : context(재생할 액티비티), path(재생할 파일경로)
    // Use : 외부 파일을 재생하고 싶을 때 사용. 넘겨받은 파일경로로 재생준비를 한다.
    public MusicPlayer(Context context, String path, boolean loop){
        targetContext = context;
        filePath = path;
        if (prepareToPlay(context, path)) {
            setMusicListeners();
            setLooping(loop);
        }
        screenSleepLocker();
    }

    // Method : MusicPlayer 생성자
    // Return Value : Constructor
    // Parameter : context(재생할 액티비티), innerFileCode
    // Use : 내장 파일을 재생하고 싶을 때 사용. 넘겨받은 내장파일코드로 재생준비를 한다.
    public MusicPlayer(Context context, int innerFileCode, boolean loop){
        targetContext = context;
        fileCode = innerFileCode;
        filePath = null;
        if (prepareToPlay(context, innerFileCode)) {
            setMusicListeners();
            setLooping(loop);
        }
        screenSleepLocker();
    }

    // Method : 재생준비
    // Return Value : boolean(재생준비가 성공적이면 true)
    // Parameter : context(재생할 액티비티), path(재생할 파일경로)
    // Use : 외부 파일을 재생하고 싶을 때 사용. path에 단말기에 저장되어있는 파일의 경로를 넘겨주면 재생준비를 해놓는다.
    //       재생하려는 파일 경로에 해당되는 파일이 존재하지 않는 경우, DB에서 해당 정보를 삭제하고 메세지를 띄우는 작업을 한다.
    private boolean prepareToPlay(Context context, String path){
        File file = new File(path);
        if (file.isFile()) {    // 존재하는 파일인 경우
            uri = Uri.fromFile(file);
            music = MediaPlayer.create(context, uri);
            return true;
        } else {            // 존재하지 않는 파일인 경우
            // DB에서 삭제하고 메세지를 띄워준다. 그리고 리스트를 갱신시켜준다.
            DBManager dbManager = DBManager.getInstance(context);
            String[] filePath = {path};
            dbManager.updateFavoriteForDeleteFile(filePath);
            dbManager.deleteBGMFile(filePath);

            Toast toastMsg = Toast.makeText(context, "재생하려는 파일이 존재하지 않습니다.", Toast.LENGTH_SHORT);
            toastMsg.show();
            return  false;
        }
    }

    // Method : 재생준비
    // Return Value : boolean(재생준비가 성공적이면 true)
    // Parameter : context(재생할 액티비티), innerFileCode
    // Use : 내장 파일을 재생하고 싶을 때 사용. innerFileCode에 내장파일의 R.raw.내장파일ID 를 넣어주면 된다.
    private boolean prepareToPlay(Context context, int innerFileCode){
        music = MediaPlayer.create(context, innerFileCode);
        return true;
    }

    // Method : 화면꺼짐방지 설정
    // Return Value : void
    // Parameter : void
    // Use : 재생중에는 화면이 자동으로 꺼지지 않게 하기 위해 미리 설정해놓는다.
    //       화면이 꺼지지 않기를 원하면 wakeLock.acquire(), 해제하려면 wakeLock.release()를 사용한다.
    private void screenSleepLocker(){
        PowerManager powerManager = (PowerManager)targetContext.getSystemService(Context.POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK,"SleepLock while play");
    }

    // Method : 음악재생 리스너 등록
    // Return Value : void
    // Parameter : void
    // Use : 재생 상태에 따른 리스너를 등록한다. setOnCompletionListener로 재생완료됐을 때 이벤트를 처리한다.
    private void setMusicListeners(){
        music.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if (wakeLock.isHeld())     // 화면꺼짐방지가 켜져있으면 해제
                    wakeLock.release();
            }
        });
    }

    // Method : 등록된 음악재생정보 release
    // Return Value : void
    // Parameter : void
    // Use : 재생했던 정보를 release 시켜서 충돌을 방지한다.
    public void releaseBgm(){
        if (music != null)
            music.release();
        released = true;
        music = null;
        if(wakeLock.isHeld()) {     // 화면꺼짐방지가 켜져있으면 해제
            wakeLock.release();
        }
    }

    // Method : 재생,정지,일시정지
    // Return Value : void
    // Parameter : void
    // Use : 현재 재생중인 음악파일을 재생/정지/일시정지 한다.
    //       재생은 일시정지 중이면 그냥 바로 재생, 릴리즈 됐으면 다시 create 해서 재생,
    //       그 외에는 처음부터 재생을 위해 정지를 한번 더 실행하고 되감기 후 재생
    public void playBgm(){
        if (music != null) {
            if (paused) {       // 일시정지 중이면
                music.start();  // 바로 다시 재생
                paused = false;
            } else if (released) {
                if (filePath == null)
                    music = MediaPlayer.create(targetContext, fileCode);
                else
                    music = MediaPlayer.create(targetContext, Uri.fromFile(new File(filePath)));

                music.seekTo(0);
                music.start();
                released = false;
            } else {
                stopBgm();
                try {
                    music.prepare();        // prepare 하면 리스너에서 받아가서 재생한다.
                    music.seekTo(0);        // 처음부터 재생하기
                    music.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (!wakeLock.isHeld()) {
                wakeLock.acquire();     // 재생중에는 화면 꺼짐방지 활성화
            }
        }
    }
    public void stopBgm(){
        if (music != null) {
            if (released) {
                released = false;
            } else {
                paused = false;
                music.stop();
            }
        }
        if(wakeLock.isHeld()) {     // 화면꺼짐방지가 켜져있으면 해제
            wakeLock.release();
        }
    }
    public void pauseBgm() {
        if (music.isPlaying()) {
            paused = true;
            music.pause();
        }
        if(wakeLock.isHeld()) {     // 화면꺼짐방지가 켜져있으면 해제
            wakeLock.release();
        }
    }

    // Method : 재생중인 음악 재생시간 조작
    // Return Value : void
    // Parameter : void
    // Use : 외부에서 SeekBar를 조작하여 시간을 전달받으면 해당 시간으로 재생중인 시간을 변경한다.
    public void seekToBgm(int seekTime) {
        if (music != null && released == false)
            music.seekTo(seekTime);
    }

    // Method : 재생중인지 확인
    // Return Value : boolean
    // Parameter : void
    // Use : 재생중이면 true, 아니면 false
    public boolean isPlaying() {
        if (music != null && released == false)
            return music.isPlaying();
        else
            return false;
    }

    // Method : 일시정지중인지 확인
    // Return Value : boolean
    // Parameter : void
    // Use : 일시정지중이면 true, 아니면 false
    public boolean isPaused() {
        if (music != null && released == false)
            return paused;
        else
            return false;
    }

    // Method : 전체 재생길이 받아오기
    // Return Value : int(재생길이 milisecond)
    // Parameter : void
    // Use : 현재 등록된 음악파일의 재생길이를 밀리초 단위로 반환한다.
    public int getDuration() {
        if (music != null && released == false) {
            return music.getDuration();
        } else {
            return 0;
        }
    }

    // Method : 현재 재생위치 받아오기
    // Return Value : int(현재 위치 milisecond)
    // Parameter : void
    // Use : 재생중인 음악파일의 현재 재생위치를 밀리초 단위로 반환한다.
    public int getCurrentPosition(){
        if (music != null && released == false) {
            return music.getCurrentPosition();
        } else {
            return 0;
        }
    }

    // Method : 반복재생여부 받아오기
    // Return Value : boolean(반복재생이면 true, 아니면 false)
    // Parameter : void
    // Use : 현재 반복재생기능이 켜져있는지 확인한다.
    public boolean getLoopSetInfo(){
        return music.isLooping();
    }

    public void setLooping(boolean loopSet){
        music.setLooping(loopSet);
    }
}
