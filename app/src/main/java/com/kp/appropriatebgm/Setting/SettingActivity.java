package com.kp.appropriatebgm.Setting;


import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.kp.appropriatebgm.CheckPref;
import com.kp.appropriatebgm.LockScreen.LockNotificationInterface;
import com.kp.appropriatebgm.LockScreen.LockScreenService;
import com.kp.appropriatebgm.R;
import com.kp.appropriatebgm.TutorialActivity;


/**
 * Created by GD on 2016-02-17.
 */
public class SettingActivity extends AppCompatActivity{

    private CheckPref mPref;
    private LockNotificationInterface binder = null;

    TextView lockSummary;
    TextView notifySummary;
    TextView screenPlaySummary;
    TextView differentTaskSummary;
    Switch lockOnOffSwitch;
    Switch notifyOnOffSwitch;
    Switch screenPlayOnOffSwitch;
    Switch differentTaskOnOffSwitch;
    LinearLayout tutorialViewGroup;
    //Use : 서비스 연결 객체 선언
    private ServiceConnection lockServiceConnection;

    // Return Value : void
    // Parameter : savedInstateState
    // Use : PreferenceFragment를 받기 위한 Activity를 만들고 이를 연결
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        settingInit();
    }

    // Method : 세팅 액티비티 초기 설정
    // Return value : void
    // parameter : void
    // use ; 기본 설정을 해준다. (공유변수 객체 선언 및 스위치, 텍스트뷰등의 구성요소를 받아오고 클릭리스너 등록 및 bind서비스 시작)
    public void settingInit()
    {
        mPref = new CheckPref(this);        // 공유 프레퍼런스 객체

        lockSummary = (TextView) findViewById(R.id.setting_textview_locksummary);
        notifySummary = (TextView) findViewById(R.id.setting_textview_notificationsummary);
        screenPlaySummary = (TextView) findViewById(R.id.setting_textview_screenOffPlaysummary);
        differentTaskSummary = (TextView) findViewById(R.id.setting_textview_differenttaskPlaysummary);

        lockOnOffSwitch = (Switch) findViewById(R.id.setting_switch_lockscreenOnOff);
        notifyOnOffSwitch = (Switch) findViewById(R.id.setting_switch_notificationOnOff);
        screenPlayOnOffSwitch = (Switch) findViewById(R.id.setting_switch_screenOffPlayOnOff);
        differentTaskOnOffSwitch = (Switch) findViewById(R.id.setting_switch_differenttaskPlayOnOff);

        tutorialViewGroup = (LinearLayout) findViewById(R.id.setting_viewgroup_tutorial);

        lockOnOffSwitch.setOnClickListener(onClickListener);
        notifyOnOffSwitch.setOnClickListener(onClickListener);
        screenPlayOnOffSwitch.setOnClickListener(onClickListener);
        tutorialViewGroup.setOnClickListener(onClickListener);
        differentTaskOnOffSwitch.setOnClickListener(onClickListener);

        lockServiceConnection = new ServiceConnection() {

            // Return Value : void
            // Parameter : ComponentName(컴포넌트 이름(패키지)), service(서비스)
            // Use : 서비스 연결 되었을 경우 aidl의 인터페이스를 통해 서비스 객체를 받는다.
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                binder = LockNotificationInterface.Stub.asInterface(service);
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
            }
        };

        Intent serviceintent = new Intent(SettingActivity.this, LockScreenService.class);
        bindService(serviceintent, lockServiceConnection, BIND_AUTO_CREATE);
    }

    // Method : onResume (일시정지 후 시작)
    // Return Value : void
    // Parameter : void
    // Use : 공유변수를 받아와 체크유무를 갱신해주고 이에 따른 상세 설명도 설정해준다.
    @Override
    public void onResume() {
        lockOnOffSwitch.setChecked(mPref.getLockerOnOff());
        notifyOnOffSwitch.setChecked(mPref.getAlarmOnOff());
        screenPlayOnOffSwitch.setChecked(mPref.getScreenOffPlayOnOff());
        differentTaskOnOffSwitch.setChecked(mPref.getDifferentTaskPlayOnOff());
        setSummaryText("lockscreen", mPref.getLockerOnOff());
        setSummaryText("notification", mPref.getAlarmOnOff());
        setSummaryText("screenoffplay", mPref.getScreenOffPlayOnOff());
        setSummaryText("differenttaskplay", mPref.getDifferentTaskPlayOnOff());
        super.onResume();
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
            Intent intent = new Intent();
            Switch settingSwitch;
            if(v.getId() == R.id.setting_switch_lockscreenOnOff) {
                settingSwitch = (Switch) v;
                Boolean lockChecked = settingSwitch.isChecked();
                if (lockChecked) {
                    intent.setClass(getApplicationContext(), LockScreenService.class);
                    startService(intent);
                    setSummaryText("lockscreen", lockChecked);
                } else {
                    intent.setClass(getApplicationContext(), LockScreenService.class);
                    stopService(intent);
                    setSummaryText("lockscreen", lockChecked);
                }
                mPref.setLockerOnOff();
                setBinderNotificationOnOff();
            }
            else if(v.getId() == R.id.setting_switch_notificationOnOff)
            {
                settingSwitch = (Switch) v;
                Boolean notifyChecked = settingSwitch.isChecked();
                mPref.setAlarmOnOff(notifyChecked);
                setBinderNotificationOnOff();
                setSummaryText("notification", notifyChecked);
            }
            else if(v.getId() == R.id.setting_switch_screenOffPlayOnOff)
            {
                settingSwitch = (Switch) v;
                Boolean notifyChecked = settingSwitch.isChecked();
                mPref.setScreenOffPlayOnOff(notifyChecked);
                setSummaryText("screenoffplay", notifyChecked);
            }
            else if(v.getId() == R.id.setting_switch_differenttaskPlayOnOff)
            {
                settingSwitch = (Switch) v;
                Boolean notifyChecked = settingSwitch.isChecked();
                mPref.setDifferentTaskPlayOnOff(notifyChecked);
                setSummaryText("differenttaskplay", notifyChecked);
            }
            else if(v.getId() == R.id.setting_viewgroup_tutorial)
            {
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
                lockSummary.setText("잠금화면 사용 상태입니다.");
            } else {
                lockSummary.setText("잠금화면 해제 상태입니다.");
            }
        }
        else if(title.equals("notification"))
        {
            if (checked) {
                notifySummary.setText("알림을 사용합니다.");
            } else {
                notifySummary.setText("알림을 해제합니다.");
            }
        }
        else if(title.equals("screenoffplay"))
        {
            if (checked) {
                screenPlaySummary.setText("화면이 꺼졌을 시에도 재생합니다.");
            } else {
                screenPlaySummary.setText("화면이 꺼졌을 시에 재생하지 않습니다.");
            }
        }
        else if(title.equals("differenttaskplay"))
        {
            if (checked) {
                differentTaskSummary.setText("다른 작업 수행 시 재생합니다.");
            } else {
                differentTaskSummary.setText("다른 작업 수행 시 재생하지 않습니다.");
            }
        }
    }

    // Method : 바인드 서비스 종료
    // Return value : void
    // parameter : void
    // use ; 액티비티가 종료될 시기에 onCreate에서 bindService의 서비스 연결을 해제(unbind)해준다.
    @Override
    protected void onDestroy() {
        unbindService(lockServiceConnection);
        super.onDestroy();
    }

    // Method : 알림 on/off 설정
    // Return value : void
    // parameter : void
    // use ; 서비스 액티비티에서 aidl을 통해 설정해 준 알림 띄우는 함수를 서비스를 연결한 뒤에 함수를 받아온다. (통신)
    //       remoteException 예외 처리를 꼭 해주어야 함!
    public void setBinderNotificationOnOff()
    {
        try{
            binder.setNotificationOnOff();
        }
        catch (RemoteException e)
        {
            e.printStackTrace();
        }
    }

}
