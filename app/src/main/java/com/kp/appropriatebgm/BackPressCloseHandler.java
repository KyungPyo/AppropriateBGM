package com.kp.appropriatebgm;

import android.app.Activity;
import android.widget.Toast;

/**
 * Created by Choi on 2016-02-11.
 */
public class BackPressCloseHandler {

    private long backKeyPressedTime = 0;
    private Toast toast;

    private Activity activity;

    public BackPressCloseHandler(Activity context) {
        this.activity = context;
    }

    public void onBackPressed() {
        if (System.currentTimeMillis() > backKeyPressedTime + 2000) { //처음 또는 백키가 눌러진 후 2초 뒤에 다시 눌러졌다면
            backKeyPressedTime = System.currentTimeMillis(); //현재 시간을 저장하고
            showGuide();// 알림
            return;
        }
        if (System.currentTimeMillis() <= backKeyPressedTime + 2000) { //2초 내에 다시 백키가 눌러졌다면
            activity.finish(); //앱종료
            toast.cancel();
        }
    }

    public void showGuide() {
        toast = Toast.makeText(activity,
                "\'뒤로\'버튼을 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT);
        toast.show();
    }
}