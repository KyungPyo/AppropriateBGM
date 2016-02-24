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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.kp.appropriatebgm.CheckPref;
import com.kp.appropriatebgm.LockScreen.LockScreenService;
import com.kp.appropriatebgm.R;

/**
 * Created by GD on 2016-02-17.
 */
public class SettingFragment extends PreferenceFragment {

    private CheckPref mPref;
    Context context;
    SwitchPreference lockSwitch;

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

    /*@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        /*View view = super.onCreateView(inflater, container, savedInstanceState);
        view.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
        return view;
    }*/

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.activity_setting);
        context = this.getActivity();
        context.setTheme(R.style.PreferencesTheme);
        // Use : 리스너 등록

        lockSwitch = (SwitchPreference) findPreference("lockscreen");
        lockSwitch.setWidgetLayoutResource(R.layout.switch_setting);

        mPref = new CheckPref((AppCompatActivity) getActivity());
        lockSwitch.setOnPreferenceChangeListener(onPreferenceChangeListener);
        findPreference("notification").setOnPreferenceChangeListener(onPreferenceChangeListener);

        // Use : 알림 기능을 위해 필요한 서비스를 얻어옴
        //mNotifyManager = (NotificationManager)  context.getSystemService(Context.NOTIFICATION_SERVICE);

    }

    // Method : onResume (일시정지 후 시작)
    // Return Value : void
    // Parameter : void
    // Use : 체크
    @Override
    public void onResume() {
        ((SwitchPreference) findPreference("lockscreen")).setChecked(mPref.getLockerOnOff());
        Log.e("preference", mPref.getLockerOnOff() + "");
        Log.e("lockSwitch",lockSwitch.getKey());
        super.onResume();
    }

    private Preference.OnPreferenceClickListener onPreferenceClickListener = new Preference.OnPreferenceClickListener() {
        @Override
        public boolean onPreferenceClick(Preference preference) {
            Log.e("click","click");
            if(preference.getKey().equals("lockscreen"))
            {
                SwitchPreference switchPreference = (SwitchPreference) preference;
                if(!switchPreference.isChecked()) {
                    Intent intent = new Intent(context, LockScreenService.class);
                    context.startService(intent);
                }
                // Use : 빠른 실행을 OFF 시키려는 경우 (체크 TRUE)
                else
                {
                    Intent intent = new Intent(context, LockScreenService.class);
                    context.stopService(intent);
                }
                mPref.setLockerOnOff();
            }
            return true;
        }
    };


    /* 환경 설정 스위치 클릭 부분 */
    private Preference.OnPreferenceChangeListener onPreferenceChangeListener = new Preference.OnPreferenceChangeListener(){

        // Method : 스위치 설정 변경 시 이벤트
        // Return Value : void
        // Parameter : preference(사용한 preference 객체), newValue(사용될 새로운 값)
        // Use : 각각의 SwitchPreference의 스위치가 바뀌었을 경우의 이벤트를 정의
        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            String stringValue = newValue.toString();
            Log.e("preference name",preference.getKey());
            if(preference instanceof SwitchPreference)
            {
                SwitchPreference switchPreference = (SwitchPreference) preference;
                preference.setSummary(stringValue);
                if(preference.getKey().equals("lockscreen"))
                {
                    Log.e("lcok checked", switchPreference.isChecked()+"");
                    // Use : 빠른 실행을 ON 시키려는 경우 (체크 FALSE)
                    if(!switchPreference.isChecked()) {
                        Intent intent = new Intent(context, LockScreenService.class);
                        context.startService(intent);
                    }
                    // Use : 빠른 실행을 OFF 시키려는 경우 (체크 TRUE)
                    else
                    {
                        Intent intent = new Intent(context, LockScreenService.class);
                        context.stopService(intent);
                    }
                    mPref.setLockerOnOff();
                }
                else if(preference.getKey().equals("notification"))
                {
                    Log.e("lockSwitch",lockSwitch.isChecked()+"");
                }
            }
            return true;
        }
    };
}
