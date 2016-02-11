package com.kp.appropriatebgm;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;

import java.io.File;
import java.io.IOException;

/**
 * Created by KP on 2016-02-03.
 */
public class MusicPlayer {
    MediaPlayer music = null;
    Uri uri = null;

    // Method : MusicPlayer 생성자
    // Return Value : Constructor
    // Parameter : context(재생할 액티비티), path(재생할 파일경로)
    // Use : 외부 파일을 재생하고 싶을 때 사용. 넘겨받은 파일경로로 재생준비를 한다.
    public MusicPlayer(Context context, String path){
        prepareToPlay(context, path);
    }

    // Method : MusicPlayer 생성자
    // Return Value : Constructor
    // Parameter : context(재생할 액티비티), innerFileCode
    // Use : 내장 파일을 재생하고 싶을 때 사용. 넘겨받은 내장파일코드로 재생준비를 한다.
    public MusicPlayer(Context context, int innerFileCode){
        prepareToPlay(context, innerFileCode);
    }

    // Method : 재생준비
    // Return Value : void
    // Parameter : context(재생할 액티비티), path(재생할 파일경로)
    // Use : 외부 파일을 재생하고 싶을 때 사용. path에 단말기에 저장되어있는 파일의 경로를 넘겨주면 재생준비를 해놓는다.
    public void prepareToPlay(Context context, String path){
        uri = Uri.fromFile(new File(path));
        music = MediaPlayer.create(context, uri);
        music.setLooping(false);
    }

    // Method : 재생준비
    // Return Value : void
    // Parameter : context(재생할 액티비티), innerFileCode
    // Use : 내장 파일을 재생하고 싶을 때 사용. innerFileCode에 내장파일의 R.raw.내장파일ID 를 넣어주면 된다.
    public void prepareToPlay(Context context, int innerFileCode){
        music = MediaPlayer.create(context, innerFileCode);
        music.setLooping(false);
    }

    // Method : 처음부터 다시 재생준비
    // Return Value : void
    // Parameter : void
    // Use : 재생하던 것을 멈추고 다시 재생준비를 한 다음 처음으로 되감는다.
    private void resetPlay(){
        if (music != null) {
            music.stop();
            try {
                music.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }
            music.seekTo(0);
        }
    }

    // Method : 재생
    // Return Value : void
    // Parameter : void
    // Use : 현재 등록된 음악파일을 재생한다. 재생하기전에 처음으로 되감는 작업을 다시한번 한다. (누르면 처음부터 재생하는 기능을 위해)
    public void playFromStartBgm(){     // 처음부터 재생하기
        resetPlay();
        music.start();
    }


    // Method : 재생,정지,일시정지
    // Return Value : void
    // Parameter : void
    // Use : 현재 재생중인 음악파일을 재생/정지/일시정지 한다.
    public void playBgm(){
        music.start();
    }
    public void stopBgm(){
        resetPlay();
    }
    public void pauseBgm() {
        music.pause();
    }

    // Method : 재생중인 음악 재생시간 조작
    // Return Value : void
    // Parameter : void
    // Use : 외부에서 SeekBar를 조작하여 시간을 전달받으면 해당 시간으로 재생중인 시간을 변경한다.
    public void seekToBgm(int seekTime) {
        music.seekTo(seekTime);
    }

    // Method : 재생중인지 확인
    // Return Value : boolean
    // Parameter : void
    // Use : 재생중이면 true, 아니면 false
    public boolean isPlaying() {
        return music.isPlaying();
    }

    public int getDuration() {
        return music.getDuration();
    }

    public int getCurrentPosition(){
        return music.getCurrentPosition();
    }
}
