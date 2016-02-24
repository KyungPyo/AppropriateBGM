package com.kp.appropriatebgm.Setting;


import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.preference.SwitchPreference;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.kp.appropriatebgm.CheckPref;
import com.kp.appropriatebgm.LockScreen.LockNotificationInterface;
import com.kp.appropriatebgm.LockScreen.LockScreenService;
import com.kp.appropriatebgm.R;

import java.util.concurrent.locks.Lock;


/**
 * Created by GD on 2016-02-17.
 */
public class SettingActivity extends AppCompatActivity{

    private CheckPref mPref;
    TextView lockSummary;
    TextView notifySummary;
    Switch lockOnOffSwitch;
    Switch notifyOnOffSwitch;
    LockScreenService lockScreenService;
    SharedPreferences preferences;
    private LockNotificationInterface binder = null;
    // Method : onCreate

    private ServiceConnection lockServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.e("service", "connected");
            binder = LockNotificationInterface.Stub.asInterface(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.e("service", "disconnected");
        }
    };



    // Return Value : void
    // Parameter : savedInstateState
    // Use : PreferenceFragment를 받기 위한 Activity를 만들고 이를 연결
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        mPref = new CheckPref(this);        // 공유 프레퍼런스 객체
        lockScreenService = new LockScreenService();

        lockSummary = (TextView) findViewById(R.id.setting_textview_locksummary);
        notifySummary = (TextView) findViewById(R.id.setting_textview_notificationsummary);
        lockOnOffSwitch = (Switch) findViewById(R.id.setting_switch_lockscreenOnOff);
        notifyOnOffSwitch = (Switch) findViewById(R.id.setting_switch_notificationOnOff);

        preferences = getApplicationContext().getSharedPreferences("AppSetting",
                Context.MODE_PRIVATE);
        lockOnOffSwitch.setOnClickListener(onClickListener);
        notifyOnOffSwitch.setOnClickListener(onClickListener);

        Intent serviceintent = new Intent(SettingActivity.this, LockScreenService.class);
        bindService(serviceintent, lockServiceConnection, BIND_AUTO_CREATE);

        // Use : FragmentManager의 트랜잭션을 SettingFragment(PreferenceFragment)로 대체한다.
        //getFragmentManager().beginTransaction().replace(android.R.id.content, new SettingFragment(getApplicationContext())).commit();
        Log.e("setting", "created");
    }

    // Method : Restart (재시작)
    // Return Value : void
    // Parameter : void
    // Use : mBoolean값을 false로 하여 setChecked시에 리스너의 공유변수 저장이 되지 않도록 하여 뒤엉킴을 방지함

    // Method : onResume (일시정지 후 시작)
    // Return Value : void
    // Parameter : void
    // Use : 체크
    @Override
    public void onResume() {
        lockOnOffSwitch.setChecked(mPref.getLockerOnOff());
        notifyOnOffSwitch.setChecked(mPref.getAlarmOnOff());
        setSummaryText("lockscreen", mPref.getLockerOnOff());
        setSummaryText("notification", mPref.getAlarmOnOff());
        super.onResume();
    }

    Switch.OnClickListener onClickListener = new Switch.OnClickListener()
    {
        @Override
        public void onClick(View v) {
            Switch settingSwitch = (Switch) v;
            if(v.getId() == R.id.setting_switch_lockscreenOnOff) {
                Boolean lockChecked = settingSwitch.isChecked();
                Log.e("notification",lockChecked+"");
                if (lockChecked) {
                    Intent intent = new Intent(SettingActivity.this, LockScreenService.class);
                    startService(intent);
                    setSummaryText("lockscreen", lockChecked);
                } else {
                    Intent intent = new Intent(SettingActivity.this, LockScreenService.class);
                    stopService(intent);
                    setSummaryText("lockscreen", lockChecked);
                    if(notifyOnOffSwitch.isChecked());
                }
                mPref.setLockerOnOff();
                setBinderNotificationOnOff();
            }
            else if(v.getId() == R.id.setting_switch_notificationOnOff)
            {
                Boolean notifyChecked = settingSwitch.isChecked();
                Log.e("notification", notifyChecked + "");
                mPref.setAlarmOnOff(notifyChecked);
                setBinderNotificationOnOff();
                setSummaryText("notification", notifyChecked);
            }
        }
    };

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
    }

    @Override
    protected void onDestroy() {
        unbindService(lockServiceConnection);
        super.onDestroy();
    }

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
