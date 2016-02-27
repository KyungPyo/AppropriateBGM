package com.kp.appropriatebgm.record;

import android.media.MediaRecorder;
import android.os.Environment;

import java.io.File;
import java.io.IOException;

/**
 * Created by hp on 2016-01-04.
 */

public class RecordManager {
    private MediaRecorder mr = null;
    private String path, filename;
    private String dirName;
    private boolean isRecording = false;

    public RecordManager(){
        dirName = "브금술사";
        init();
    }

    // Method : 생성자 호출
    // Return Value : void
    // Parameter : appName
    // Use :  디덱토리명을 넘겨주고 init() 호출.
    public RecordManager(String appName) {
        // 디렉토리명은 외부에서 생성자를 호출할 때 string.xml의 app_name을 넘겨준다.
        dirName = appName;
        init();
    }

    // Method : 기존 경로에 녹음한 파일을 저장
    // Return Value : void
    // Parameter : View
    // Use :   외장메모리 기본경로/어플명 에 녹음한 파일을 저장한다.
    private void init() {
        path = Environment.getExternalStorageDirectory() + File.separator + dirName;
        File file = new File(path);
        filename = "record_temp.mp3";   // 임시 파일명
        // Use :  디렉토리가 존재하지 않으면 생성
        if ( !file.exists() ) {
            file.mkdirs();
        }
    }

    //Use : 녹음 시작
    public void start() {

        // 처음 녹음을 시작하면 MediaRecorder 객체 생성
        if (mr == null)
            mr = new MediaRecorder();

        mr.reset();
        mr.setAudioSource(MediaRecorder.AudioSource.MIC);   // 마이크로 녹음
        mr.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mr.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
        mr.setOutputFile(path + File.separator + filename);    // 파일 경로 및 파일명으로 녹음한 파일 생성준비

        try {
            isRecording = true;  // 녹음시작
            mr.prepare();
            mr.start();
        } catch (IOException e) {
            e.printStackTrace();
            isRecording = false; //녹음 실패
        }
    }

    //Use:  녹음 중지
    public void stop() {
        isRecording = false;
        if (mr == null) return;
        try {
            mr.stop();
        } catch (Exception e) {
        } finally {
            mr.release();
            mr = null;
        }
    }


    // Method : 현재 녹음여부를 판단.
    // Return Value : boolean
    // Parameter : Void
    // Use :   녹음중인지 확인.
    public boolean isRecording() {
        return isRecording;
    }

    // 녹음된 파일이 저장된 곳은 어디? (파일명 포함해서 넘겨준다)
    public String getPath() { return path + File.separator + filename; }
    // 녹음된 파일 저장된 디렉토리 경로만 넘겨준다
    public String getDirPath() { return path; }
}