package com.kp.appropriatebgm;

import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.kp.appropriatebgm.DBController.DBManager;
import com.kp.appropriatebgm.record.RecordManager;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by KP on 2016-01-27.
 */
public class LogoActivity extends AppCompatActivity {
    private CheckPref mPref = null;
    private DBManager dbManager = null;
    private final int MY_PERMISSIONS_REQUEST = 1;
    private TextView message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPref = new CheckPref(this);    // 공유 프레퍼런스 객체
        setContentView(R.layout.activity_logo);
        this.setVolumeControlStream(AudioManager.STREAM_MUSIC);

        message = (TextView)findViewById(R.id.logo_text_message);   // 현재 진행상황 표시 텍스트

        int versionCode = Build.VERSION.SDK_INT;
        // Marshmellow 이상 권한부여 확인
        if (versionCode >= Build.VERSION_CODES.M) {
            int grantedInteger = PackageManager.PERMISSION_GRANTED;
            boolean permissionCheck = (ContextCompat.checkSelfPermission(this, Manifest.permission.WAKE_LOCK) == grantedInteger)
                    && (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == grantedInteger)
                    && (ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_BOOT_COMPLETED) == grantedInteger)
                    && (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) == grantedInteger)
                    && (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == grantedInteger)
                    && (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == grantedInteger);

            if (!permissionCheck) {
                // 권한부여가 안됐다고 설정한다.
                mPref.setPermissionsGrant(false);
                // 권한부여가 안됐으면 요청한다.
                String[] requestPermissionsList = new String[]{
                        Manifest.permission.WAKE_LOCK, Manifest.permission.RECORD_AUDIO,
                        Manifest.permission.RECEIVE_BOOT_COMPLETED, Manifest.permission.READ_PHONE_STATE,
                        Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE
                };
                ActivityCompat.requestPermissions(this, requestPermissionsList, MY_PERMISSIONS_REQUEST);
            }
        } else {
            // Lollipop 이하이면 권한부여 체크 스킵
            mPref.setPermissionsGrant(true);
        }

        // 앱 실행에 필요한 권한을 받아왔을 경우 다음 액티비티로 진행한다.
        if (mPref.getPermissionsGrant()) {
            appStartSetting();
        }
        // 권한이 없을 경우 권한을 허용해 달라고 출력한다.
        else {
            message.setText("권한을 허용해야 앱이 실행됩니다.");
        }
    }

    // Method : 앱 실행 시 파일검사, DB검사 등의 초기 작업을 실행한다
    // Return Value : void
    // Parameter : void
    // Use : 최초 실행이면 디렉토리 생성한다.
    //      내장된 MediaStore DB를 이용해서 파일을 SQLite에 추가하는 작업인 initStorageBGMFiles() 실행
    //      성공적으로 수행되었으면 다음 MainActivity로 넘어간다.
    private void appStartSetting(){
        // 최초 실행이면 초기설정을 한다고 메세지를 띄운다.
        if (mPref.getFirstExcute()) {
            message.setText("앱 초기 설정중입니다.");
            mPref.setFirstExcute();

            // RecordManager 객체의 초기설정을 이용하여 기본 디렉토리가 존재하지 않으면 생성한다.
            RecordManager recordManager = new RecordManager(getString(R.string.app_name));
            recordManager = null;
        }

        // 싱글톤 객체를 받아오면서 상속받은 SQLiteOpenHelper 클래스를 이용하여 DB를 생성/수정/열기 한다.
        dbManager = DBManager.getInstance(this);
        // 단말기에 저장된 음악파일 검색 및 무결성 검사
        final boolean isAvailable = initStorageBGMFiles();


        // 로고화면에서 잠시 대기하면서 초기설정을 하고 메인 액티비티로 전환
        Thread waitThread = new Thread("Wait and Start Thread") {
            @Override
            public void run() {
                super.run();
                try {
                    if (isAvailable) {
                        sleep(1000);
                        Intent intent = new Intent();
                        ComponentName componentName = new ComponentName("com.kp.appropriatebgm", "com.kp.appropriatebgm.MainActivity");
                        intent.setComponent(componentName);
                        startActivity(intent);
                    } else {
                        message.setText("파일을 불러올 수가 없습니다.\n잠시 후 다시 실행 해주세요.");
                        sleep(2000);
                    }
                    finish();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    message.setText("잠시 후 다시 실행 해주세요.");
                    finish();
                }

            }
        };

        waitThread.start();
        message.setText("시작하는 중");
    }

    // Method : 파일 무결성 확인
    // Return Value : boolean(정상적으로 파일을 읽어올 수 있으면 true, 아니면 false)
    // Parameter : void
    // Use : 단말기에 저장된 미디어파일을 전부 받아와서 DB와 비교를 요청한다.
    //       아직 미디어풀이 갱신되지 않았을 경우를 대비하여 실제 존재하는 파일인지 다시한번 확인하고 DBManager로 넘겨준다.
    private boolean initStorageBGMFiles() {
        try {
            // 음악파일 목록을 받아올 때 같이 요청하는 정보 목록(파일경로, 파일명)
            String[] requestList = new String[]{MediaStore.Audio.Media.DATA, MediaStore.Audio.Media.DISPLAY_NAME};

            // Storage에서 음악파일 목록을 받아와서 cursor에 저장한다.
            Cursor cursor = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, requestList, "1=1", null, null);

            String[] file;
            ArrayList<String[]> fileList = new ArrayList<>();
            File existCheck;
            while (cursor.moveToNext()) {
                // 미디어풀 DB에 등록이 아직 안되어있을 수 있기 때문에 정말 존재하는 파일인지 확실히 확인한다.
                existCheck = new File(cursor.getString(0));
                if (existCheck.isFile()) {
                    file = new String[2];
                    file[0] = cursor.getString(0);
                    file[1] = cursor.getString(1);
                    fileList.add(file);
                    //Log.i("저장된 파일들", cursor.getString(0) + " : " + cursor.getString(1));
                }
            }

            dbManager.checkBGMList(fileList);
            return true;
        } catch(NullPointerException e) {
            Log.e("initStorageBGMFiles", e.toString());
            e.printStackTrace();
            return false;
        }
    }

    // Method : 권한요청 결과 이벤트
    // Return Value : void
    // Parameter : requestCode(requestPermission 메소드에서 넘겨준 int값), permissions(요청한 권한들), grantResults(각각 요청결과)
    // Use : 앱 최초 실행 시 권한을 요청하고, 그 권한 요청에 따른 결과를 이 이벤트로 처리한다.
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST : {
                if (grantResults.length > 0  && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    mPref.setPermissionsGrant(true);
                    appStartSetting();

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    mPref.setPermissionsGrant(false);
                }
                return;
            }
        }
    }
}
