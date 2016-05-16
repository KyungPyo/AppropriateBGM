package com.kp.appropriatebgm.Setting;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.kp.appropriatebgm.CheckPref;
import com.kp.appropriatebgm.DBController.DBManager;
import com.kp.appropriatebgm.DBController.Favorite;
import com.kp.appropriatebgm.LockScreen.LockNotificationInterface;
import com.kp.appropriatebgm.R;
import com.kp.appropriatebgm.Tutorial.TutorialActivity;

import java.util.ArrayList;


/**
 * Created by GD on 2016-02-17.
 */
public class SettingActivity extends AppCompatActivity{

    private CheckPref mPref;
    private LockNotificationInterface binder = null;
    private Context mContext;
    private PlayerServieceController servieceController;

    private TextView lockSummary;
    private TextView notiplayerSummary;
    private Switch lockOnOffSwitch;
    private Switch notiplayerOnOffSwitch;
    private LinearLayout tutorialViewGroup;
    private ArrayList<Favorite> bgmfavoriteArrayList;
    private DBManager dbManager;

    // Return Value : void
    // Parameter : savedInstateState
    // Use : PreferenceFragment를 받기 위한 Activity를 만들고 이를 연결
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        dbManager = DBManager.getInstance(getApplicationContext());
        settingInit();
    }

    // Method : 세팅 액티비티 초기 설정
    // Return value : void
    // parameter : void
    // use ; 기본 설정을 해준다. (공유변수 객체 선언 및 스위치, 텍스트뷰등의 구성요소를 받아오고 클릭리스너 등록 및 bind서비스 시작)
    public void settingInit()
    {
        mContext = this;
        mPref = new CheckPref(this);        // 공유 프레퍼런스 객체
        servieceController = new PlayerServieceController(mContext);

        lockSummary = (TextView) findViewById(R.id.setting_textview_locksummary);
        notiplayerSummary = (TextView) findViewById(R.id.setting_textview_notiplayersummary);

        lockOnOffSwitch = (Switch) findViewById(R.id.setting_switch_lockscreenOnOff);
        notiplayerOnOffSwitch = (Switch) findViewById(R.id.setting_switch_notiplayerOnOff);

        tutorialViewGroup = (LinearLayout) findViewById(R.id.setting_viewgroup_tutorial);

        lockOnOffSwitch.setOnClickListener(onClickListener);
        notiplayerOnOffSwitch.setOnClickListener(onClickListener);
        tutorialViewGroup.setOnClickListener(onClickListener);

    }

    // Method : onResume (일시정지 후 시작)
    // Return Value : void
    // Parameter : void
    // Use : 공유변수를 받아와 체크유무를 갱신해주고 이에 따른 상세 설명도 설정해준다.
    @Override
    public void onResume() {
        try {
            lockOnOffSwitch.setChecked(mPref.getLockerOnOff());
            notiplayerOnOffSwitch.setChecked(mPref.getNotiplayerOnOff());
            setSummaryText("lockscreen", mPref.getLockerOnOff());
            setSummaryText("notiplayer", mPref.getNotiplayerOnOff());
            super.onResume();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //Use : 스위치 클릭 리스너 설정
    View.OnClickListener onClickListener = new View.OnClickListener()
    {
        // Method : 클릭 이벤트
        // Return value : void
        // parameter : View(클릭된 뷰 = 여기서는 스위치)
        // use ; 아이디를 비교하여 스위치를 분류하고 세부적으로 잠금화면 스위치가 on/off됨에 따라 서비스 시작/정지
        //       알림 스위치 on/off에 따라 bindservice의 setBinderNotificationOnOff로 알림 띄울지 검사
        //       재생관련 알림 스위치 on/off에 따라 화면 꺼짐 시, 다른 작업 수행 시 종료할지 말지를 검사
        @Override
        public void onClick(View v) {
            Switch settingSwitch;
            if(v.getId() == R.id.setting_switch_lockscreenOnOff) {
                settingSwitch = (Switch) v;
                Boolean lockChecked = settingSwitch.isChecked();
                if (lockChecked) {
                    if(servieceController.startLockerPlayerService()) {
                        setSummaryText("lockscreen", lockChecked);
                    } else {
                        settingSwitch.setChecked(!lockChecked);
                    }
                } else {
                    servieceController.stopLockerPlayerService();
                    setSummaryText("lockscreen", lockChecked);
                }

            }
            else if(v.getId() == R.id.setting_switch_notiplayerOnOff)
            {
                settingSwitch = (Switch) v;
                Boolean notifyChecked = settingSwitch.isChecked();
                if(notifyChecked) {
                    if(servieceController.startFastPlayerService()) {
                        setSummaryText("notiplayer", notifyChecked);
                    } else {
                        settingSwitch.setChecked(!notifyChecked);
                    }
                }
                else {
                    servieceController.stopFastPlayerService();
                    setSummaryText("notiplayer", notifyChecked);
                }
            }
            else if(v.getId() == R.id.setting_viewgroup_tutorial)
            {
                Intent intent = new Intent();
                intent.setClass(getApplicationContext(), TutorialActivity.class);
                startActivity(intent);
            }
        }
    };

    // Method : 상세 내용 설명 설정
    // Return value : void
    // parameter : title(구분하기 위한 제목), checked(스위치 체크 유무)
    // use ; 넘겨온 타이틀 텍스트 값과 체크 유무에 따라서 상세 내용(힌트)이 바뀐다.
    public void setSummaryText(String title, Boolean checked)
    {
        if(title.equals("lockscreen")) {
            if (checked) {
                lockSummary.setText("사용합니다.");
            } else {
                lockSummary.setText("사용하지 않습니다.");
            }
        }
        else if(title.equals("notiplayer"))
        {
            if (checked) {
                notiplayerSummary.setText("사용합니다.");
            } else {
                notiplayerSummary.setText("사용하지 않습니다.");
            }
        }

    }

    // Method : 즐겨찾기 목록 존재확인
    // Return value : 목록이 하나라도 있으면 true, 아니면 false
    // parameter : void
    // use : 사용자가 등록한 즐겨찾기가 하나이상 있는 지 확인하여 리턴한다.
    public boolean isFavoriteListExist()
    {
        bgmfavoriteArrayList = dbManager.getFavoriteListNotNull();
        if(bgmfavoriteArrayList.size() == 0) {
            return false;
        } else {
            return true;
        }
    }
}
