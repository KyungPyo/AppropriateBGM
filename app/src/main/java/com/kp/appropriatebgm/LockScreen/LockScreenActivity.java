package com.kp.appropriatebgm.LockScreen;

import android.app.WallpaperManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Layout;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kp.appropriatebgm.DBController.DBManager;
import com.kp.appropriatebgm.DBController.Favorite;
import com.kp.appropriatebgm.R;
import com.kp.appropriatebgm.favoritebgm.FavoriteListAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by GD on 2016-02-11.
 */
public class LockScreenActivity extends AppCompatActivity implements UnlockScreenWidget.OnUnlockListener{

    ArrayList<Favorite> bgmfavoriteArrayList;
    DBManager bgmdbManager;//DB

    BroadcastReceiver timeBroadcastReceiver;
    private final SimpleDateFormat apm_format = new SimpleDateFormat("aa");
    private final SimpleDateFormat time_format = new SimpleDateFormat("h:mm");
    private final SimpleDateFormat day_format = new SimpleDateFormat("M월 d일 E요일");
    private UnlockScreenWidget slide_widget;
    TextView apm_clock;
    TextView time_clock;
    TextView day_clock;
    HorizontalScrollView bgmFavoriteScroll;
    LockScreenBgmButton bgmListButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("잘왔냐~", "Activity onCreate Ok");
        bgmdbManager=DBManager.getInstance(this);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        setContentView(R.layout.activity_lockscreen);
        Log.e("Locker", " : on");

        ImageView background_ImageView = (ImageView) findViewById(R.id.lockscreen_image_background);
        background_ImageView.setImageDrawable(WallpaperManager.getInstance(this).getDrawable());
        bgmFavoriteScroll = (HorizontalScrollView) findViewById(R.id.lockscreen_group_horizontalscroll);
        LinearLayout testLinear = (LinearLayout) findViewById(R.id.lockscreen_group_btnadd);


        apm_clock = (TextView) findViewById(R.id.lockscreen_textview_apmclock);
        time_clock = (TextView) findViewById(R.id.lockscreen_textview_timeclock);
        day_clock = (TextView) findViewById(R.id.lockscreen_textview_dayclock);

        slide_widget = (UnlockScreenWidget) findViewById(R.id.lockscreen_widget_unlock);
        slide_widget.setOnUnlockListener(this);
        timeInit();

        bgmfavoriteArrayList=new ArrayList<Favorite>();

        //listView 설정
        bgmfavoriteArrayList=bgmdbManager.getFavoriteList();//DB
        for(int i=0; i < bgmfavoriteArrayList.size() ; i++) {
            if (bgmfavoriteArrayList.get(i).getBgmPath() != null)
            {
                bgmListButton = new LockScreenBgmButton(this);
                // .setID 지정 가능
                bgmListButton.bgmFavoriteText.setText(bgmfavoriteArrayList.get(i).getBgmName());
                bgmListButton.bgmFavoriteImg.setBackgroundColor(Color.parseColor("#30FF0000"));
                testLinear.addView(bgmListButton);
            }
        }
    }

    // Method : 잠금해제 이벤트
    // Return value : void
    // paremeter : void
    // Use : 잠금화면 액티비티를 종료한다.
    @Override
    public void onUnlock() {
        finish();
    }

    // Method : 액티비티 시작 함수
    // Return value : void
    // paremeter : void
    // Use : 액티비티가 시작하는 경우에 호출되는 함수이다. 내부에서 브로드캐스트 리시버 객체를 생성한 후에
    //       onReceive함수를 통해 브로드캐스트를 받을 때 매분마다 발생하는 ACTION_TIME_TICK 인텐트를 비교하여 시간 업데이트
    @Override
    public void onStart()
    {
        super.onStart();
        timeBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context ctx, Intent intent)
            {
                if (intent.getAction().compareTo(Intent.ACTION_TIME_TICK) == 0) {
                    timeInit();
                    // Update your thing
                }
            }

        };
        registerReceiver(timeBroadcastReceiver, new IntentFilter(Intent.ACTION_TIME_TICK));
    }

    // Method : 액티비티 정지 함수
    // Return value : void
    // paremeter : void
    // Use : 액티비티가 정지하는 경우에 호출되는 함수이다. 정지할 경우에는 액티비티가 죽는 경우이므로 브로드캐스트 리시버를 해제시켜줘야
    //       작동이 되지 않으며 효율적이다.
    @Override
    public void onStop()
    {
        super.onStop();
        if (timeBroadcastReceiver!= null)
            unregisterReceiver(timeBroadcastReceiver);
    }


    // Method : 액티비티 재시작 함수
    // Return value : void
    // paremeter : void
    // Use : 액티비티가 재시작하는 경우에 호출되는 함수이다. 재시작할 경우 시간이 표시되거나 갱신되는데 오래 걸리는 경우가
    //       있어 시간 갱신을 강제로 시켜주도록 한다.
    @Override
    protected void onRestart() {
        timeInit();
        super.onRestart();
    }

    // Method : 키 이벤트 관리
    // Return value : boolean
    // paremeter : 키의 고유 코드(keyCode), 이벤트 타입
    // Use : 잠금화면에서 메뉴 키와 뒤로가기 키가 눌렸을 때 잠금화면을 빠져나오는 경우를 방지하기 위해서 반환 값을 true로 주어 빠져나감을 방지
    //       But, 홈 키의 경우 키 이벤트로는 제어가 불가(내부적으로)
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            if (keyCode == KeyEvent.KEYCODE_MENU) {
                Log.e("Keycode", "Menu");
                return true;
            }

            if (keyCode == KeyEvent.KEYCODE_BACK) {
                Log.e("Keycode", "back");
                return true;
            }
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onUserLeaveHint() {
        finish();
        super.onUserLeaveHint();
    }

    // Method : 시간 설정
    // Return value : void
    // paremeter : void
    // Use : 잠금화면의 TextView에 시간을 설정해주는 역할을 한다.
    public void timeInit()
    {
        apm_clock.setText(apm_format.format(new Date()));
        time_clock.setText(time_format.format(new Date()));
        day_clock.setText(day_format.format(new Date()));
    }
}
