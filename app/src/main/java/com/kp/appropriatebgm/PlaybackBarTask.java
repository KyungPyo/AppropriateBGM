package com.kp.appropriatebgm;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

/**
 * Created by KP on 2016-02-04.
 */
public class PlaybackBarTask extends AsyncTask<Void, Integer, Void> {

    /***** 멤버 선언 및 초기화 *****/
    Context targetActivity;
    MusicPlayer music;
    SeekBar progressBar;
    TextView playTime;
    TextView maxTime;
    ImageView playAndPause = null;

    public PlaybackBarTask(){}
    public PlaybackBarTask(Context context, MusicPlayer nowPlaying, SeekBar progressBar, TextView playTime, TextView maxTime){
        this.targetActivity = context;
        this.music = nowPlaying;
        this.progressBar = progressBar;
        this.playTime = playTime;
        this.maxTime = maxTime;
    }

    public void setPlayAndPauseBtn(ImageView btnInstance){
        playAndPause = btnInstance;
    }
    /***** 멤버 선언 및 초기화 *****/

    /***** 스레드 동작 *****/
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Void doInBackground(Void... params) {
        return null;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);

    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
    }
    /***** 스레드 동작 *****/


    /***** 기타 메소드 *****/
    private void setTimeText(TextView targetView, int timeMs) {
        int sec = 0, min = 0;
        String timeText = "";
        // TextView 설정
        min = timeMs / 60000;
        timeMs = timeMs % 60000;
        sec = timeMs / 1000;

        if (min < 10)
            timeText = "0";
        timeText = timeText + min + ":";
        if (sec < 10)
            timeText = timeText + "0";
        timeText = timeText + sec;

        targetView.setText(timeText);
    }
    /***** 기타 메소드 *****/
}
