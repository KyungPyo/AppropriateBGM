package com.kp.appropriatebgm;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

/**
 * Created by KP on 2016-02-04.
 */
public class PlaybackBarTask extends AsyncTask<Void, Integer, Void> {

    /***** 멤버 선언 및 초기화 *****/
    private final int PROGRESS_INTERVAL = 50;

    Context targetActivity;
    MusicPlayer music;
    SeekBar progressBar;
    TextView playTimeText;
    TextView maxTimeText;
    ImageView playAndPause = null;

    public PlaybackBarTask(){}

    // Method : PlaybackBarTask 생성자
    // Return Value : void
    // Parameter : context(스레드를 사용할 액티비티), progressBar(해당 액티비티의 재생바),
    //             playTime(해당 액티비티의 현재 재생시간을 나타내는 TextView), maxTime(현재 재생중인 음악의 길이를 나타내는 TextView)
    // Use : 재생중에 재생툴을 실시간으로 변경시키고 싶을 때 변경시킬 View들을 넘겨받고 준비를 한다.
    public PlaybackBarTask(Context context, SeekBar progressBar, TextView playTime, TextView maxTime){
        this.targetActivity = context;
        this.progressBar = progressBar;
        this.playTimeText = playTime;
        this.maxTimeText = maxTime;
        setSeekBarListener();
    }

    // Method : 재생하는 음악 등록
    // Return Value : void
    // Parameter : nowPlaying(재생하려는 음악이 등록된 MusicPlayer 인스턴스)
    // Use : 재생툴 조작에 필요한 MusicPlayer 인스턴스 저장
    public void setMusic(MusicPlayer nowPlaying){
        this.music = nowPlaying;
    }

    // Method : 하나의 뷰로 설정된 재생버튼과 일시정지버튼 등록
    // Return Value : void
    // Parameter : btnInstance(재생 및 일시정지버튼)
    // Use : 재생버튼과 일시정지버튼이 하나의 뷰로 이루어져 있을 때 이 메소드를 사용하여 등록한다.
    //      이 메소드로 등록하면 자동으로 재생중 여부를 판별하여 이미지를 변경시켜준다.
    public void setPlayAndPauseBtn(ImageView btnInstance){
        playAndPause = btnInstance;
    }
    /***** 멤버 선언 및 초기화 *****/

    /***** 스레드 동작 *****/
    // Method : 스레드 동작 전 사전작업
    // Return Value : void
    // Parameter : void
    // Use : 재생툴에 현재 재생하려는 음악의 최대길이를 입력하고 시작위치를 0으로 설정
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressBar.setMax(music.getDuration());
        progressBar.setProgress(music.getCurrentPosition());
        setTimeText(playTimeText, music.getCurrentPosition());
        setTimeText(maxTimeText, music.getDuration());

        if (playAndPause != null) {     // 재생,일시정지 토글버튼이 등록된 경우
            // 재생버튼을 일시정지 모양으로 변경
            playAndPause.setImageResource(R.drawable.ic_pause_circle_outline_black_48dp);
        }
    }

    // Method : 실제 스레드 동작
    // Return Value : void
    // Parameter : params(void)
    // Use : PROGRESS_INTERVAL(50ms)마다 재생툴 갱신작업(onProgressUpdate)을 한다.
    @Override
    protected Void doInBackground(Void... params) {
        while (music.isPlaying()) {
            if (isCancelled()) {
                return null;
            } else {
                try {
                    Thread.sleep(PROGRESS_INTERVAL);
                    publishProgress(music.getCurrentPosition());
                } catch (InterruptedException e) {
                    Log.e("PlaybackBarTask Thread", e.toString());
                    return null;
                }
            }
        }
        return null;
    }

    // Method : 스레드 동작 중 재생툴 갱신
    // Return Value : void
    // Parameter : values(현재 재생시간)
    // Use : doInBackground 실행 중 publishProgress가 실행될 때마다 현재 재생시간을 넘겨받아
    //       재생바와 현재 재생시간을 나타내는 텍스트를 갱신한다.
    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);

        int currentTime = values[0];
        if (music.isPlaying()) {     // 재생중이면 재생바 갱신
            progressBar.setProgress(currentTime);
            setTimeText(playTimeText, currentTime); //시간 계속 갱신.
        }
        if (playAndPause != null && (currentTime >= music.getDuration() || !music.isPlaying())) { // 재생시간이 최대시간을 넘기면 or 재생중이 아니면
            // 일시정지 버튼 재생모양으로 변경
            playAndPause.setImageResource(R.drawable.ic_play_circle_outline_black_48dp);
        }
    }

    // Method : 스레드 종료시
    // Return Value : void
    // Parameter : void
    // Use : 재생툴의 현재 재생시간을 0으로 설정
    @Override
    protected void onCancelled() {
        Log.d("onCancelled", "called!!");
        progressBar.setProgress(0);
        setTimeText(playTimeText, 0);
        super.onCancelled();

        if (playAndPause != null) {     // 재생,일시정지 토글버튼이 등록된 경우
            // 일시정지 버튼 재생모양으로 변경
            playAndPause.setImageResource(R.drawable.ic_play_circle_outline_black_48dp);
        }
    }
    /***** 스레드 동작 *****/


    /***** 기타 메소드 *****/
    // Method : TextView에 적절한 형태로 시간을 입력
    // Return Value : void
    // Parameter : targetView(양식대로 입력할 TextView), timeMs(변환할 ms형태의 시간)
    // Use : ms형태의 시간과 변경시킬 TextView를 받아서 시간을 분:초 형태로 변경시켜 TextView에 입력시킴
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

    // Method : 재생바 변경시 발생하는 이벤트 리스너 등록
    // Return Value : void
    // Parameter : void
    // Use : 재생바에서 현재 위치가 사용자에 의해 변경되면 재생중인 시간을 변경시키는 이벤트 리스너를 만들고 현재 조작중인 SeekBar에 등록
    private void setSeekBarListener(){
        SeekBar.OnSeekBarChangeListener seekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser && music.isPlaying()) {
                    music.seekToBgm(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {            }
        };

        progressBar.setOnSeekBarChangeListener(seekBarChangeListener);
    }
    /***** 기타 메소드 *****/
}
