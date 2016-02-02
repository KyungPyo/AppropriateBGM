package com.kp.appropriatebgm;

import android.content.ComponentName;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.kp.appropriatebgm.DBController.DBManager;
import com.kp.appropriatebgm.record.RecordManager;

import java.util.ArrayList;

/**
 * Created by KP on 2016-01-27.
 */
public class LogoActivity extends AppCompatActivity {
    private CheckPref mPref = null;
    private DBManager dbManager = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPref = new CheckPref(this);    // 공유 프레퍼런스 객체
        setContentView(R.layout.activity_logo);

        final TextView message = (TextView)findViewById(R.id.logo_text_message);   // 현재 진행상황 표시 텍스트

        // 최초 실행이면 초기설정을 한다고 메세지를 띄운다.
        if(mPref.getFirstExcute()){
            message.setText("앱 초기 설정중입니다.");
            Log.i("First Excute!!", "okok");
            mPref.setFirstExcute();

            // RecordManager 객체의 초기설정을 이용하여 기본 디렉토리가 존재하지 않으면 생성한다.
            RecordManager recordManager = new RecordManager(getString(R.string.app_name));
            recordManager = null;
        }

        // 싱글톤 객체를 받아오면서 상속받은 SQLiteOpenHelper 클래스를 이용하여 DB를 생성/수정/열기 한다.
        dbManager = DBManager.getInstance(this);
        // 단말기에 저장된 음악파일 검색 및 무결성 검사
        initStorageBGMFiles();


        // 로고화면에서 잠시 대기하면서 초기설정을 하고 메인 액티비티로 전환
        Thread waitThread = new Thread("Wait and Start Thread"){
            @Override
            public void run() {
                super.run();
                try {
                    sleep(1000);
                    Intent intent = new Intent();
                    ComponentName componentName = new ComponentName("com.kp.appropriatebgm", "com.kp.appropriatebgm.MainActivity");
                    intent.setComponent(componentName);
                    startActivity(intent);
                    finish();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        };

        waitThread.start();
        message.setText("시작하는 중");
    }

    private void initStorageBGMFiles(){
        // 음악파일 목록을 받아올 때 같이 요청하는 정보 목록(파일경로, 파일명)
        String[] requestList = new String[]{MediaStore.Audio.Media.DATA, MediaStore.Audio.Media.DISPLAY_NAME};

        // Storage에서 음악파일 목록을 받아와서 cursor에 저장한다.
        Cursor cursor = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, requestList, "1=1", null, null);

        String[] file;
        ArrayList<String[]> fileList = new ArrayList<>();
        while (cursor.moveToNext()){
            file = new String[2];
            file[0] = cursor.getString(0);
            file[1] = cursor.getString(1);
            fileList.add(file);
            Log.i("저장된 파일들", cursor.getString(0)+" : "+cursor.getString(1));
        }

        dbManager.checkBGMList(fileList);
    }
}
