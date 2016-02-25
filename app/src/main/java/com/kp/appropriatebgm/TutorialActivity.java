package com.kp.appropriatebgm;

import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;

public class TutorialActivity extends AppCompatActivity {
    private ViewPager viewPager;
    private ImageAdapter adapter;
    private ImageView startButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);

        viewPager = (ViewPager) findViewById(R.id.view_pager);
        startButton=(ImageView) findViewById(R.id.tutorial_startApp);
        adapter = new ImageAdapter(this);
        viewPager.setAdapter(adapter);

        //바로 시작 하기 버튼 클릭 시 액티비티 종료.
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    //튜토리얼 중 Back 키 막기
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
