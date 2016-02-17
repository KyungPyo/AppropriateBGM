package com.kp.appropriatebgm.Setting;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;


/**
 * Created by GD on 2016-02-17.
 */
public class SettingActivity extends AppCompatActivity{

    // Method : onCreate
    // Return Value : void
    // Parameter : savedInstateState
    // Use : PreferenceFragment를 받기 위한 Activity를 만들고 이를 연결
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 아래 한줄과 동일한 소스코드 (축약형)
        /*FragmentManager mFragmentManager = getFragmentManager();
        FragmentTransaction mFragmentTransaction = mFragmentManager.beginTransaction();
        SettingFragment prefFragment = new SettingFragment();
        mFragmentTransaction.replace(android.R.id.content, prefFragment);
        mFragmentTransaction.commit();*/

        // Use : FragmentManager의 트랜잭션을 SettingFragment(PreferenceFragment)로 대체한다.
        getFragmentManager().beginTransaction().replace(android.R.id.content, new SettingFragment(getApplicationContext())).commit();
    }
}
