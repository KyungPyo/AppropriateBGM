package com.kp.appropriatebgm;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;

import java.io.File;
import java.io.IOException;

/**
 * Created by KP on 2016-02-03.
 */
public class MusicPlayer {
    private MediaPlayer music = null;
    private Uri uri = null;
    private boolean paused = false;
    private boolean readyToPlay = false;
    private boolean released = false;
    private Context targetContext;
    private String filePath;
    private int fileCode;


    // Method : MusicPlayer 생성자
    // Return Value : Constructor
    // Parameter : context(재생할 액티비티), path(재생할 파일경로)
    // Use : 외부 파일을 재생하고 싶을 때 사용. 넘겨받은 파일경로로 재생준비를 한다.
    public MusicPlayer(Context context, String path){
        targetContext = context;
        filePath = path;
        prepareToPlay(context, path);
    }

    // Method : MusicPlayer 생성자
    // Return Value : Constructor
    // Parameter : context(재생할 액티비티), innerFileCode
    // Use : 내장 파일을 재생하고 싶을 때 사용. 넘겨받은 내장파일코드로 재생준비를 한다.
    public MusicPlayer(Context context, int innerFileCode){
        targetContext = context;
        fileCode = innerFileCode;
        filePath = null;
        prepareToPlay(context, innerFileCode);
    }

    // Method : 재생준비
    // Return Value : void
    // Parameter : context(재생할 액티비티), path(재생할 파일경로)
    // Use : 외부 파일을 재생하고 싶을 때 사용. path에 단말기에 저장되어있는 파일의 경로를 넘겨주면 재생준비를 해놓는다.
    private void prepareToPlay(Context context, String path){
        uri = Uri.fromFile(new File(path));
        music = MediaPlayer.create(context, uri);
        music.setLooping(false);
        setMediaListener();
    }

    // Method : 재생준비
    // Return Value : void
    // Parameter : context(재생할 액티비티), innerFileCode
    // Use : 내장 파일을 재생하고 싶을 때 사용. innerFileCode에 내장파일의 R.raw.내장파일ID 를 넣어주면 된다.
    private void prepareToPlay(Context context, int innerFileCode){
        music = MediaPlayer.create(context, innerFileCode);
        music.setLooping(false);
        setMediaListener();
    }

    private void setMediaListener(){
//        music.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//            @Override
//            public void onPrepared(MediaPlayer mp) {
//                Log.d("onPrepared!!", "!!!!!!!!");
//                if (readyToPlay) {
//                    music.start();
//                    readyToPlay = false;
//                }
//            }
//        });

//        music.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//            @Override
//            public void onCompletion(MediaPlayer mp) {
//                Log.d("onCompletion!!", "?!!?!?");
//                music.release();
//                released = true;
//                readyToPlay = false;
//                paused = false;
//            }
//        });
    }

    public void releaseBgm(){
        music.release();
        released = true;
        music = null;
    }

    // Method : 재생
    // Return Value : void
    // Parameter : void
    // Use : 현재 등록된 음악파일을 재생한다. 재생하기전에 처음으로 되감는 작업을 다시한번 한다. (누르면 처음부터 재생하는 기능을 위해)
    public void playBgmFromStart(){
        if (music != null) {
            music.seekTo(0);            // 처음부터 재생하기
            stopBgm();
            try {
                readyToPlay = true;     // readyToPlay 가 true면 prepare 시 재생한다.
                music.prepare();        // prepare 하면 리스너에서 받아가서 재생한다.
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    // Method : 재생,정지,일시정지
    // Return Value : void
    // Parameter : void
    // Use : 현재 재생중인 음악파일을 재생/정지/일시정지 한다.
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
                    readyToPlay = true;     // readyToPlay 가 true면 prepare 시 재생한다.
                    music.prepare();        // prepare 하면 리스너에서 받아가서 재생한다.
                    music.seekTo(0);        // 처음부터 재생하기
                    music.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    public void stopBgm(){
        if (released) {
            released = false;
        } else {
            paused = false;
            readyToPlay = false;
            music.stop();
            Log.i("stop!!!","했어!!!");
        }
    }
    public void pauseBgm() {
        paused = true;
        music.pause();
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

    public int getDuration() {
        if (music != null && released == false) {
            return music.getDuration();
        } else {
            return 0;
        }
    }

    public int getCurrentPosition(){
        if (music != null && released == false) {
            Log.d("current", "" + music.getCurrentPosition());
            return music.getCurrentPosition();
        } else {
            return 0;
        }
    }
}
