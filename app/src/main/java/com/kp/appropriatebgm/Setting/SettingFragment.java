package com.kp.appropriatebgm.Setting;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.kp.appropriatebgm.CheckPref;
import com.kp.appropriatebgm.LockScreen.LockScreenService;
import com.kp.appropriatebgm.R;

/**
 * Created by GD on 2016-02-17.
 */
public class SettingFragment extends PreferenceFragment {

    private CheckPref mPref;    // 공유변수인데 이거 자체가 Preference라서 써야되는지 잘 모르겠음
    private Notification mNotification;
    private NotificationManager mNotifyManager;
    Context context;

    // Method : SettingFragment 생성자
    // Return Value : void
    // Parameter : void
    // Use : context를 받기 위한 default 생성자
    public SettingFragment()
    {
        super();
    }

    // Method : SettingFragment 생성자 (2)
    // Return Value : void
    // Parameter : context(액티비티 정보)
    // Use : SettingActivity의 context를 받기 위한 생성자
    @SuppressLint("ValidFragment")
    public SettingFragment(Context context)
    {
        this.context = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.activity_setting);
        // Use : 리스너 등록
        mPref = new CheckPref((AppCompatActivity) getActivity());
        findPreference("lockscreen").setOnPreferenceChangeListener(onPreferenceChangeListener);
        findPreference("notification").setOnPreferenceChangeListener(onPreferenceChangeListener);

        // Use : 알림 기능을 위해 필요한 서비스를 얻어옴
        //mNotifyManager = (NotificationManager)  context.getSystemService(Context.NOTIFICATION_SERVICE);

    }

    /* 환경 설정 스위치 클릭 부분 */
    private Preference.OnPreferenceChangeListener onPreferenceChangeListener = new Preference.OnPreferenceChangeListener(){
        // Method : 스위치 설정 변경 시 이벤트
        // Return Value : void
        // Parameter : preference(사용한 preference 객체), newValue(사용될 새로운 값)
        // Use : 각각의 SwitchPreference의 스위치가 바뀌었을 경우의 이벤트를 정의
        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            String stringValue = newValue.toString();

            if(preference instanceof SwitchPreference)
            {
                SwitchPreference switchPreference = (SwitchPreference) preference;
                preference.setSummary(stringValue);
                ((SwitchPreference) findPreference("lockscreen")).setChecked(mPref.getLockerOnOff());
                if(preference.getKey().equals("lockscreen"))
                {
                    // Use : 빠른 실행을 ON 시키려는 경우 (체크 FALSE)
                    if(!switchPreference.isChecked()) {
                        Log.e("lock", "잠금 시작");
                        Intent intent = new Intent(context, LockScreenService.class);
                        context.startService(intent);
                    }
                    // Use : 빠른 실행을 OFF 시키려는 경우 (체크 TRUE)
                    else
                    {
                        Log.e("lock", "잠금 종료");
                        Intent intent = new Intent(context, LockScreenService.class);
                        context.stopService(intent);
                    }
                    mPref.setLockerOnOff();
                }
            /*    else if(preference.getKey().equals("notification"))
                {
                    // Use : 빠른 실행을 ON 시키려는 경우 (체크 FALSE)
                    if(!switchPreference.isChecked()) {
                        Log.e("lock", "알림 시작");
                        mNotification.flags = mNotification.FLAG_NO_CLEAR;
                        mNotifyManager.notify(5555, mNotification);
                    }
                    // Use : 알림을 OFF 시키려는 경우 (체크 TRUE)
                    else
                    {
                        Log.e("lock", "알림 종료");
                        mNotifyManager.cancel(5555);
                    }
                }*/
            }
            return true;
        }
    };
}
