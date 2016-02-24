package com.kp.appropriatebgm.Setting;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.kp.appropriatebgm.CheckPref;
import com.kp.appropriatebgm.LockScreen.LockScreenService;
import com.kp.appropriatebgm.R;


/**
 * Created by GD on 2016-02-17.
 */
public class SettingActivity extends AppCompatActivity implements ViewSwitcher.ViewFactory{

    private CheckPref mPref;
    TextView lockSummary;
    TextView notifySummary;
    Switch lockOnOffSwitch;
    Switch notifyOnOffSwitch;
    // Method : onCreate
    @Override
    public View makeView() {
        return null;
    }
    // Return Value : void
    // Parameter : savedInstateState
    // Use : PreferenceFragment를 받기 위한 Activity를 만들고 이를 연결
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        mPref = new CheckPref(this);        // 공유 프레퍼런스 객체

        lockSummary = (TextView) findViewById(R.id.setting_textview_locksummary);
        notifySummary = (TextView) findViewById(R.id.setting_textview_notifysummary);
        lockOnOffSwitch = (Switch) findViewById(R.id.setting_switch_lockscreenOnOff);
        notifyOnOffSwitch = (Switch) findViewById(R.id.setting_switch_notificationOnOff);

        lockOnOffSwitch.setOnCheckedChangeListener(onCheckedChangeListener);
        notifyOnOffSwitch.setOnCheckedChangeListener(onCheckedChangeListener);

        lockOnOffSwitch.setChecked(mPref.getLockerOnOff());
        setSummaryText("lockscreen",mPref.getLockerOnOff());

        // Use : FragmentManager의 트랜잭션을 SettingFragment(PreferenceFragment)로 대체한다.
        //getFragmentManager().beginTransaction().replace(android.R.id.content, new SettingFragment(getApplicationContext())).commit();
        Log.e("setting", "created");
    }

    Switch.OnCheckedChangeListener onCheckedChangeListener = new Switch.OnCheckedChangeListener()
    {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

            if(buttonView.getId() == R.id.setting_switch_lockscreenOnOff) {
                if (isChecked) {
                    Intent intent = new Intent(SettingActivity.this, LockScreenService.class);
                    startService(intent);
                    setSummaryText("lockscreen",isChecked);
                } else {
                    Intent intent = new Intent(SettingActivity.this, LockScreenService.class);
                    stopService(intent);
                    setSummaryText("lockscreen",isChecked);
                }
                mPref.setLockerOnOff();
            }
            else if(buttonView.getId() == R.id.setting_switch_notificationOnOff)
            {
                if(isChecked)
                {

                }
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
                lockSummary.setText("알림을 사용합니다.");
            } else {
                lockSummary.setText("알림을 해제합니다.");
            }
        }
    }

}
